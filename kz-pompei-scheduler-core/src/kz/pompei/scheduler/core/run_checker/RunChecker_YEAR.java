package kz.pompei.scheduler.core.run_checker;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import lombok.RequiredArgsConstructor;

/**
 * Checks whether the specified time belongs to the specified year.
 */
@RequiredArgsConstructor
public class RunChecker_YEAR implements RunChecker {

  private final int      year;
  private final TimeZone timeZone;

  /**
   *
   * `timestampMsFrom` convert to year by this.timeZone to variable `year1`
   * <p>
   * `timestampMsTo` convert to year by this.timeZone to variable `year2`
   * <p>
   * returns year1 == this.year OR year2 == this.year
   *
   * @param timestampStartedAt milliseconds from System.currentTimeMillis() - time of start scheduler
   * @param timestampMsFrom    milliseconds from System.currentTimeMillis() - time from
   * @param timestampMsTo      milliseconds from System.currentTimeMillis() - time to
   * @return true if the specified time belongs to the specified year, false otherwise
   */
  @Override public boolean needRun(long timestampStartedAt, long timestampMsFrom, long timestampMsTo) {
    GregorianCalendar calendar = new GregorianCalendar(timeZone);

    {
      calendar.setTimeInMillis(timestampMsFrom);
      if (year == calendar.get(Calendar.YEAR)) {
        return true;
      }
    }
    {
      calendar.setTimeInMillis(timestampMsTo);
      if (year == calendar.get(Calendar.YEAR)) {
        return true;
      }
    }

    return false;
  }
}
