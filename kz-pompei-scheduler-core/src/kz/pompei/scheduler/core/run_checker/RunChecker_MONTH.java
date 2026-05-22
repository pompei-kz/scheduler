package kz.pompei.scheduler.core.run_checker;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import lombok.RequiredArgsConstructor;

/**
 * Checks whether the specified time belongs to the specified month.
 */
@RequiredArgsConstructor
public class RunChecker_MONTH implements RunChecker {

  private final int      month;
  private final TimeZone timeZone;

  /**
   *
   * `timestampMsFrom` convert to month by this.timeZone to variable `month1`
   * <p>
   * `timestampMsTo` convert to month by this.timeZone to variable `month2`
   * <p>
   * returns month1 == this.month OR month2 == this.month
   *
   * @param timestampStartedAt milliseconds from System.currentTimeMillis() - time of start scheduler
   * @param timestampMsFrom    milliseconds from System.currentTimeMillis() - time from
   * @param timestampMsTo      milliseconds from System.currentTimeMillis() - time to
   * @return true if the specified time belongs to the specified month, false otherwise
   */
  @Override public boolean needRun(long timestampStartedAt, long timestampMsFrom, long timestampMsTo) {
    GregorianCalendar calendar = new GregorianCalendar(timeZone);

    {
      calendar.setTimeInMillis(timestampMsFrom);
      if (month == calendar.get(Calendar.MONTH) + 1) {
        return true;
      }
    }
    {
      calendar.setTimeInMillis(timestampMsTo);
      if (month == calendar.get(Calendar.MONTH) + 1) {
        return true;
      }
    }

    return false;
  }
}
