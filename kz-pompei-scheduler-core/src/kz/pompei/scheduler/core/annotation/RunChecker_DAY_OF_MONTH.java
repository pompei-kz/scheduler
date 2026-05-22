package kz.pompei.scheduler.core.annotation;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import lombok.RequiredArgsConstructor;

/**
 * Checks whether the specified time belongs to the specified day of the month.
 */
@RequiredArgsConstructor
public class RunChecker_DAY_OF_MONTH implements RunChecker {

  private final int      dayOfMonth;
  private final TimeZone timeZone;

  /**
   *
   * `timestampMsFrom` convert to day of month by this.timeZone to variable `day1`
   * <p>
   * `timestampMsTo` convert to day of month by this.timeZone to variable `day2`
   * <p>
   * returns day1 == this.dayOfMonth OR day2 == this.dayOfMonth
   *
   * @param timestampStartedAt milliseconds from System.currentTimeMillis() - time of start scheduler
   * @param timestampMsFrom    milliseconds from System.currentTimeMillis() - time from
   * @param timestampMsTo      milliseconds from System.currentTimeMillis() - time to
   * @return true if the specified time belongs to the specified day of the month, false otherwise
   */
  @Override public boolean needRun(long timestampStartedAt, long timestampMsFrom, long timestampMsTo) {
    GregorianCalendar calendar = new GregorianCalendar(timeZone);

    {
      calendar.setTimeInMillis(timestampMsFrom);
      if (dayOfMonth == calendar.get(Calendar.DAY_OF_MONTH)) {
        return true;
      }
    }
    {
      calendar.setTimeInMillis(timestampMsTo);
      if (dayOfMonth == calendar.get(Calendar.DAY_OF_MONTH)) {
        return true;
      }
    }

    return false;
  }
}
