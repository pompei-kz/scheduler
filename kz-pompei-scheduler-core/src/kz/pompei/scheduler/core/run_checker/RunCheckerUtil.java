package kz.pompei.scheduler.core.run_checker;

import java.util.Calendar;
import lombok.NonNull;

public class RunCheckerUtil {
  public static void setCalendarHMS(@NonNull Calendar calendar, int hourFrom, int minuteFrom, int secondFrom) {
    calendar.set(Calendar.HOUR_OF_DAY, hourFrom);
    calendar.set(Calendar.MINUTE, minuteFrom);
    calendar.set(Calendar.SECOND, secondFrom);
    calendar.set(Calendar.MILLISECOND, 0);
  }
}
