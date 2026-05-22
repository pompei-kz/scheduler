package kz.pompei.scheduler.core.run_checker;

import java.util.GregorianCalendar;
import java.util.TimeZone;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import static kz.pompei.scheduler.core.run_checker.RunCheckerUtil.setCalendarHMS;

/**
 * Implementation of RunChecker that checks if a task should be run based on the hour, minute, and second of any day.
 */
@RequiredArgsConstructor
public class RunChecker_HMS implements RunChecker {

  private final int hour, minute, second;
  private final @NonNull TimeZone timeZone;

  /**
   * First, the method gets (year,month,day) from `timestampMsFrom` and `timeZone` (using GregorianCalendar).
   * <p>
   * Then (year, month, day), `hour`, `minute`, `second` converts to milliseconds (using GregorianCalendar) to variable `timestampMs`.
   * <p>
   * And then returns true if `timestampMsFrom` <= `timestampMs` < `timestampMsTo`.
   * <p>
   * Note, the first sign contains equals, the second sign does not contain equals.
   *
   * @param timestampStartedAt milliseconds from System.currentTimeMillis() - time of start scheduler
   * @param timestampMsFrom    milliseconds from System.currentTimeMillis() - time from
   * @param timestampMsTo      milliseconds from System.currentTimeMillis() - time to
   * @return result of check
   */
  @Override public boolean needRun(long timestampStartedAt, long timestampMsFrom, long timestampMsTo) {
    if (timestampMsTo <= timestampMsFrom) {
      return false;
    }

    GregorianCalendar calendar = new GregorianCalendar(timeZone);
    calendar.setTimeInMillis(timestampMsFrom);

    setCalendarHMS(calendar, hour, minute, second);

    long timestampMs = calendar.getTimeInMillis();

    return timestampMsFrom <= timestampMs && timestampMs < timestampMsTo;
  }
}
