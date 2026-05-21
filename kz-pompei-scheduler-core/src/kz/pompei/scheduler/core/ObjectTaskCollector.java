package kz.pompei.scheduler.core;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import kz.pompei.hotconfig.core.ConfigTunnel;
import kz.pompei.scheduler.core.annotation.Schedule;
import lombok.NonNull;

public class ObjectTaskCollector {

  private final @NonNull ConfigTunnel tunnel;

  public ObjectTaskCollector(@NonNull ConfigTunnel tunnel) {
    this.tunnel = tunnel;
  }

  public @NonNull List<ScheduledTask> collect(@NonNull Object object) {
    List<ScheduledTask> ret = new ArrayList<>();

    Method[] methods = object.getClass().getMethods();

    for (Method method : methods) {

      method.getAnnotation(Schedule.class);

    }

    return ret;
  }

}
