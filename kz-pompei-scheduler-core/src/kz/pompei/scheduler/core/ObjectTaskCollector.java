package kz.pompei.scheduler.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import kz.pompei.hotconfig.core.ConfigTunnel;
import kz.pompei.hotconfig.core.ann.ConfDoc;
import kz.pompei.hotconfig.core.model.Conf;
import kz.pompei.hotconfig.core.model.ConfParam;
import kz.pompei.scheduler.core.annotation.FromConf;
import kz.pompei.scheduler.core.annotation.Schedule;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import static kz.pompei.scheduler.core.ReflectUtil.findAnnotation;

public class ObjectTaskCollector {

  private final @NonNull ConfigTunnel tunnel;

  public ObjectTaskCollector(@NonNull ConfigTunnel tunnel) {
    this.tunnel = tunnel;
  }

  public @NonNull List<ScheduledTask> collect(@NonNull Object object) {
    List<ScheduledTask> ret                       = new ArrayList<>();
    Method[]            methods                   = object.getClass().getMethods();
    Conf                confDefault               = new Conf();

    {
      @Nullable ReflectUtil.Ann_Class<ConfDoc> classConfDoc = findAnnotation(object.getClass(), ConfDoc.class);
      if (classConfDoc != null) {
        Collections.addAll(confDefault.confComments, classConfDoc.ann.value().split("\n"));
      }
    }



    Map<String, Task> taskName_to_task = new HashMap<>();

    for (Method method : methods) {

      @Nullable ReflectUtil.Ann_Method<Schedule> schedule = findAnnotation(method, Schedule.class);

      if (schedule == null) continue;

      if (method.getParameterCount() > 0) {
        throw new RuntimeException("82J5nXW9Mg :: scheduler method must be without parameters: " + method);
      }

      @NonNull Task   task     = createTask(object, method);
      @NonNull String taskName = method.getName();

      taskName_to_task.put(taskName, task);

      @Nullable ReflectUtil.Ann_Method<FromConf> fromConf = findAnnotation(method, FromConf.class);

      if (fromConf != null) {

        ConfParam confParam = new ConfParam(taskName, schedule.ann.value());
        confDefault.params.add(confParam);

        @Nullable ReflectUtil.Ann_Method<ConfDoc> paramDoc = findAnnotation(method, ConfDoc.class);
        if (paramDoc != null) {
          confParam.comment(paramDoc.ann.value());
        }

      }
    }

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
