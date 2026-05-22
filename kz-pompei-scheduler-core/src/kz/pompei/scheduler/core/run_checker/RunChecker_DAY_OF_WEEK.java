package kz.pompei.scheduler.core.run_checker;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import lombok.RequiredArgsConstructor;

/**
 * Checks whether the specified time belongs to the specified day of the week.
 */
@RequiredArgsConstructor
public class RunChecker_DAY_OF_WEEK implements RunChecker {

  private final DayOfWeek dayOfWeek;
  private final TimeZone  timeZone;

  /**
   *
   * `timestampMsFrom` convert to day of week by this.timeZone to variable `dayOfWeek1`
   * <p>
   * `timestampMsTo` convert to day of week by this.timeZone to variable `dayOfWeek2`
   * <p>
   * returns dayOfWeek1 == this.dayOfWeek OR dayOfWeek2 == this.dayOfWeek
   *
   * @param timestampStartedAt milliseconds from System.currentTimeMillis() - time of start scheduler
   * @param timestampMsFrom    milliseconds from System.currentTimeMillis() - time from
   * @param timestampMsTo      milliseconds from System.currentTimeMillis() - time to
   * @return true if the specified time belongs to the specified day of the week, false otherwise
   */
  @Override public boolean needRun(long timestampStartedAt, long timestampMsFrom, long timestampMsTo) {
    GregorianCalendar calendar = new GregorianCalendar(timeZone);

    {
      calendar.setTimeInMillis(timestampMsFrom);
      if (dayOfWeek == DayOfWeek.fromCalendarDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK))) {
        return true;
      }
    }
    {
      calendar.setTimeInMillis(timestampMsTo);
      if (dayOfWeek == DayOfWeek.fromCalendarDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK))) {
        return true;
      }
    }

    return false;
  }
}
