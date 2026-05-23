package kz.pompei.scheduler.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import kz.pompei.hotconfig.core.ConfigTunnel;
import kz.pompei.hotconfig.core.ann.ConfDoc;
import kz.pompei.hotconfig.core.ann.ConfFolder;
import kz.pompei.hotconfig.core.model.Conf;
import kz.pompei.hotconfig.core.model.ConfParam;
import kz.pompei.scheduler.core.annotation.FromConf;
import kz.pompei.scheduler.core.annotation.Schedule;
import kz.pompei.scheduler.core.annotation.UseTimeZone;
import kz.pompei.scheduler.core.scheduler_src.CompileResult;
import kz.pompei.scheduler.core.scheduler_src.Compiler;
import kz.pompei.scheduler.core.scheduler_src.ScheduleSrc;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import static kz.pompei.scheduler.core.ReflectUtil.findAnnotation;

public class ObjectTaskCollector {

  private final @NonNull ConfigTunnel tunnel;
  private final @NonNull Def          def;

  private final List<Runnable> refreshHandlers = new ArrayList<>();

  ObjectTaskCollector(@NonNull ConfigTunnel tunnel, @NonNull Def def) {
    this.tunnel = tunnel;
    this.def    = def;
  }

  public static @NonNull ObjectTaskCollectorBuilder builder() {
    return new ObjectTaskCollectorBuilder();
  }

  @RequiredArgsConstructor
  static class Def {
    final @NonNull String   extension;
    final @NonNull TimeZone timeZoneDefault;
  }

  public void refresh() {
    refreshHandlers.forEach(Runnable::run);
  }

  public @NonNull List<ScheduledTask> collect(@NonNull Object object) {
    List<ScheduledTask> ret         = new ArrayList<>();
    Method[]            methods     = object.getClass().getMethods();
    Conf                confDefault = new Conf();

    Map<String, Task>                      taskName_to_task = new HashMap<>();
    ConcurrentHashMap<String, ScheduleSrc> taskName_to_src  = new ConcurrentHashMap<>();

    for (Method method : methods) {

      ReflectUtil.Ann_Method<Schedule> schedule = findAnnotation(method, Schedule.class).orElse(null);

      if (schedule == null) continue;

      if (method.getParameterCount() > 0) {
        throw new RuntimeException("82J5nXW9Mg :: scheduler method must be without parameters: " + method);
      }

      if (findAnnotation(method, FromConf.class).isEmpty()) {
        ret.add(new ScheduledTask() {
          @NonNull final TimeZone timeZone = findAnnotation(method, UseTimeZone.class).map(x -> x.ann)
                                                                                      .map(UseTimeZone::value)
                                                                                      .map(TimeZone::getTimeZone)
                                                                                      .orElseGet(() -> def.timeZoneDefault);

          @NonNull final ScheduleSrc scheduleSrc = Compiler.compile(schedule.ann.value(), timeZone);
          @NonNull final Task        task        = createTask(object, method);

          @Override public @NonNull ScheduleSrc src() {
            return scheduleSrc;
          }

          @Override public @NonNull Task task() {
            return task;
          }
        });
        continue;
      }

      @NonNull Task   task     = createTask(object, method);
      @NonNull String taskName = method.getName();

      taskName_to_task.put(taskName, task);

      ConfParam confParam = new ConfParam(taskName, schedule.ann.value());

      confDefault.params.add(confParam);

      findAnnotation(method, ConfDoc.class).ifPresent(paramDoc -> confParam.comment(paramDoc.ann.value()));

      ret.add(new ScheduledTask() {

        @Override public @NonNull ScheduleSrc src() {
          ScheduleSrc scheduleSrc = taskName_to_src.get(taskName);
          return scheduleSrc != null ? scheduleSrc : ScheduleSrc.NEVER_RUN;
        }

        @Override public @NonNull Task task() {
          return task;
        }
      });
    }

    if (confDefault.params.isEmpty()) {
      return ret;
    }

    findAnnotation(object.getClass(), ConfDoc.class).ifPresent(
      classConfDoc -> Collections.addAll(confDefault.confComments, classConfDoc.ann.value().split("\n")));

    @NonNull String folder    = findAnnotation(object.getClass(), ConfFolder.class).map(s -> s + "/").orElse("");
    @NonNull String localPath = folder + object.getClass().getSimpleName() + def.extension;

    AtomicLong lastModificationMarker = new AtomicLong(0L);

    Runnable update = () -> {
      Conf conf               = tunnel.read(localPath);
      Long modificationMarker = tunnel.modificationMarker(localPath);

      if (conf == null || modificationMarker == null) {
        tunnel.write(localPath, confDefault);
        modificationMarker = tunnel.modificationMarker(localPath);
        conf               = confDefault;
      } else {

        Conf    confNew = conf.copy();
        boolean changed = false;

        for (ConfParam paramDefault : confDefault.params) {

          boolean notFound = true;

          for (ConfParam param : confNew.params) {
            if (Objects.equals(paramDefault.name, param.name)) {
              notFound = false;
              break;
            }
          }

          if (notFound) {
            changed = true;
            confNew.params.add(paramDefault);
          }
        }

        if (changed) {
          tunnel.write(localPath, confNew);
          modificationMarker = tunnel.modificationMarker(localPath);
          conf               = confNew;
        }
      }
      if (modificationMarker != null) {
        lastModificationMarker.set(modificationMarker);
      }

      //
      //
      CompileResult compileResult = Compiler.compileAll(conf.params, def.timeZoneDefault, taskName_to_task.keySet());
      //
      //

      tunnel.writeNoticeLines(localPath, compileResult.noticeMessages);

      for (ConfParam param : conf.params) {
        String taskName = param.name;
        Task   task     = taskName_to_task.get(taskName);
        if (task == null) continue;

        ScheduleSrc scheduleSrc = compileResult.taskName_to_scheduleSrc.get(taskName);

        if (scheduleSrc == null) {
          taskName_to_src.put(taskName, ScheduleSrc.NEVER_RUN);
        } else {
          taskName_to_src.put(taskName, scheduleSrc);
        }
      }
    };

    refreshHandlers.add(() -> {

      Long modificationMarker = tunnel.modificationMarker(localPath);
      if (modificationMarker == null) return;
      if (lastModificationMarker.longValue() == modificationMarker) return;

      update.run();
    });

    return ret;
  }

  private static @NonNull Task createTask(@NonNull Object object, @NonNull Method method) {
    return new Task() {
      @Override public String taskName() {
        return object.getClass().getSimpleName() + "." + method.getName();
      }

      @Override public void run() throws Throwable {

        try {
          method.invoke(object);
        } catch (InvocationTargetException e) {
          throw e.getTargetException();
        }

      }
    };
  }

}
