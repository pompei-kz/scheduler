package kz.pompei.scheduler.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
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

import static kz.pompei.scheduler.core.ReflectUtil.findAnnotation;

public class Collector {

  private final @NonNull Scheduler.Def  def;
  private final          List<Runnable> refreshHandlers = new ArrayList<>();

  Collector(@NonNull Scheduler.Def def) {
    this.def = def;
  }

  void refresh() {
    refreshHandlers.forEach(Runnable::run);
  }

  public @NonNull List<ScheduledTask> collect(@NonNull Object object) {
    List<ScheduledTask>                    ret              = new ArrayList<>();
    Method[]                               methods          = object.getClass().getMethods();
    Conf                                   confDefault      = new Conf();
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
                                                                                      .orElseGet(def::getTimezone);

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
    @NonNull String localPath = folder + SchedulerUtil.extractClassName(object.getClass()) + def.configExtension;

    AtomicLong lastModificationMarker = new AtomicLong(0L);

    Runnable update = () -> {
      Conf          conf               = def.tunnel.read(localPath);
      Long          modificationMarker = def.tunnel.modificationMarker(localPath);
      CompileResult compileResult;

      if (conf == null || modificationMarker == null) {

        conf = confDefault.copy();

        compileResult = Compiler.compileAll(confDefault.params, def.getTimezone(), taskName_to_task.keySet(), def.taskErrorConsumer);

        compileResult.assignTo(conf);

        def.tunnel.write(localPath, confDefault);
        Long mm = def.tunnel.modificationMarker(localPath);
        lastModificationMarker.set(mm == null ? 0 : mm);


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

        compileResult = Compiler.compileAll(conf.params, def.getTimezone(), taskName_to_task.keySet(), def.taskErrorConsumer);
        compileResult.assignTo(confNew);

        if (changed || !confNew.equals(conf)) {
          def.tunnel.write(localPath, confNew);
          modificationMarker = def.tunnel.modificationMarker(localPath);
          conf               = confNew;
        }
        if (modificationMarker != null) {
          lastModificationMarker.set(modificationMarker);
        }
      }

      for (ConfParam param : conf.params) {
        String taskName = param.name;
        Task   task     = taskName_to_task.get(taskName);
        if (task == null) continue;

        Optional.of(compileResult)
                .map(x -> x.taskName_to_scheduleSrc.get(taskName))
                .map(x -> x.src)
                .ifPresentOrElse(src -> taskName_to_src.put(taskName, src),
                                 () -> taskName_to_src.remove(taskName));
      }
    };

    refreshHandlers.add(() -> {
      Long modificationMarker = def.tunnel.modificationMarker(localPath);
      if (modificationMarker != null && modificationMarker == lastModificationMarker.longValue()) return;
      update.run();
    });

    update.run();

    return ret;
  }

  private static @NonNull Task createTask(@NonNull Object object, @NonNull Method method) {
    method.setAccessible(true);
    return new Task() {
      @Override public String taskName() {
        return SchedulerUtil.extractClassName(object.getClass()) + '.' + method.getName();
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
