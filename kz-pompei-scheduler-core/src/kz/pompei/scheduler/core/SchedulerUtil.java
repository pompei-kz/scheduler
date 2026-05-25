package kz.pompei.scheduler.core;

import lombok.NonNull;

public class SchedulerUtil {
  public static @NonNull String extractClassName(@NonNull Class<?> objectClass) {
    {
      String simpleName = objectClass.getSimpleName();
      if (!simpleName.isEmpty()) return simpleName;
    }
    {
      String name = objectClass.getName();
      int    idx  = name.lastIndexOf('.');
      return idx < 0 ? name : name.substring(idx + 1);
    }
  }

  public static String trimStr(String str) {
    return str == null ? null : str.trim();
  }
}
