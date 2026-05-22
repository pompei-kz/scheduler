package kz.pompei.scheduler.core.annotation;

import java.util.GregorianCalendar;
import java.util.TimeZone;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RunChecker_DAY_OF_MONTH_Test {

  private static final TimeZone UTC = TimeZone.getTimeZone("UTC");

  @Test
  public void needRun_shouldReturnTrueWhenFromDayMatches() {
    RunChecker_DAY_OF_MONTH checker   = new RunChecker_DAY_OF_MONTH(21, UTC);
    long                    startedAt = timestamp(UTC, 2026, 5, 1, 0, 0, 0, 0);
    long                    from      = timestamp(UTC, 2026, 5, 21, 10, 0, 0, 0);
    long                    to        = timestamp(UTC, 2026, 5, 22, 10, 0, 0, 0);

    //
    //
    boolean needRun = checker.needRun(startedAt, from, to);
    //
    //

    assertThat(needRun).isTrue();
  }

  @Test
  public void needRun_shouldReturnTrueWhenToDayMatches() {
    RunChecker_DAY_OF_MONTH checker   = new RunChecker_DAY_OF_MONTH(22, UTC);
    long                    startedAt = timestamp(UTC, 2026, 5, 1, 0, 0, 0, 0);
    long                    from      = timestamp(UTC, 2026, 5, 21, 10, 0, 0, 0);
    long                    to        = timestamp(UTC, 2026, 5, 22, 10, 0, 0, 0);

    //
    //
    boolean needRun = checker.needRun(startedAt, from, to);
    //
    //

    assertThat(needRun).isTrue();
  }

  @Test
  public void needRun_shouldReturnFalseWhenNeitherDayMatches() {
    RunChecker_DAY_OF_MONTH checker   = new RunChecker_DAY_OF_MONTH(23, UTC);
    long                    startedAt = timestamp(UTC, 2026, 5, 1, 0, 0, 0, 0);
    long                    from      = timestamp(UTC, 2026, 5, 21, 10, 0, 0, 0);
    long                    to        = timestamp(UTC, 2026, 5, 22, 10, 0, 0, 0);

    //
    //
    boolean needRun = checker.needRun(startedAt, from, to);
    //
    //

    assertThat(needRun).isFalse();
  }

  @Test
  public void needRun_shouldReturnTrueWhenFromAndToAreSameMatchingDay() {
    RunChecker_DAY_OF_MONTH checker   = new RunChecker_DAY_OF_MONTH(21, UTC);
    long                    startedAt = timestamp(UTC, 2026, 5, 1, 0, 0, 0, 0);
    long                    timestamp = timestamp(UTC, 2026, 5, 21, 10, 0, 0, 0);

    //
    //
    boolean needRun = checker.needRun(startedAt, timestamp, timestamp);
    //
    //

    assertThat(needRun).isTrue();
  }

  @Test
  public void needRun_shouldUseConfiguredTimeZoneForFromTimestamp() {
    TimeZone                almaty    = TimeZone.getTimeZone("Asia/Almaty");
    RunChecker_DAY_OF_MONTH checker   = new RunChecker_DAY_OF_MONTH(22, almaty);
    long                    startedAt = timestamp(UTC, 2026, 5, 1, 0, 0, 0, 0);
    long                    from      = timestamp(UTC, 2026, 5, 21, 19, 0, 0, 0);
    long                    to        = timestamp(UTC, 2026, 5, 21, 20, 0, 0, 0);

    //
    //
    boolean needRun = checker.needRun(startedAt, from, to);
    //
    //

    assertThat(needRun).isTrue();
  }

  @Test
  public void needRun_shouldIgnoreSchedulerStartTimestamp() {
    RunChecker_DAY_OF_MONTH checker   = new RunChecker_DAY_OF_MONTH(21, UTC);
    long                    startedAt = timestamp(UTC, 2030, 1, 1, 0, 0, 0, 0);
    long                    from      = timestamp(UTC, 2026, 5, 21, 10, 0, 0, 0);
    long                    to        = timestamp(UTC, 2026, 5, 22, 10, 0, 0, 0);

    //
    //
    boolean needRun = checker.needRun(startedAt, from, to);
    //
    //

    assertThat(needRun).isTrue();
  }

  private static long timestamp(TimeZone timeZone, int year, int month, int day, int hour, int minute, int second, int millisecond) {
    GregorianCalendar calendar = new GregorianCalendar(timeZone);
    calendar.clear();
    //noinspection MagicConstant
    calendar.set(year, month - 1, day, hour, minute, second);
    calendar.set(GregorianCalendar.MILLISECOND, millisecond);
    return calendar.getTimeInMillis();
  }
}
