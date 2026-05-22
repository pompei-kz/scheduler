package kz.pompei.scheduler.core.run_checker;

import java.util.GregorianCalendar;
import java.util.TimeZone;
import org.testng.annotations.Test;

import static kz.pompei.scheduler.core.run_checker.DayOfWeek.FRIDAY;
import static kz.pompei.scheduler.core.run_checker.DayOfWeek.MONDAY;
import static kz.pompei.scheduler.core.run_checker.DayOfWeek.SATURDAY;
import static kz.pompei.scheduler.core.run_checker.DayOfWeek.SUNDAY;
import static kz.pompei.scheduler.core.run_checker.DayOfWeek.THURSDAY;
import static kz.pompei.scheduler.core.run_checker.DayOfWeek.TUESDAY;
import static kz.pompei.scheduler.core.run_checker.DayOfWeek.WEDNESDAY;
import static org.assertj.core.api.Assertions.assertThat;

public class RunChecker_DAY_OF_WEEK_Test {

  private static final TimeZone UTC = TimeZone.getTimeZone("UTC");

  @Test
  public void needRun_shouldReturnTrueWhenFromWeekMatches() {
    RunChecker_DAY_OF_WEEK checker   = new RunChecker_DAY_OF_WEEK(THURSDAY, UTC);
    long                   startedAt = timestamp(UTC, 2026, 1, 1, 0, 0, 0, 0);
    long                   from      = timestamp(UTC, 2026, 5, 21, 10, 0, 0, 0);
    long                   to        = timestamp(UTC, 2026, 5, 22, 10, 0, 0, 0);

    //
    //
    boolean needRun = checker.needRun(startedAt, from, to);
    //
    //

    assertThat(needRun).isTrue();
  }

  @Test
  public void needRun_shouldReturnTrueWhenToWeekMatches() {
    RunChecker_DAY_OF_WEEK checker   = new RunChecker_DAY_OF_WEEK(THURSDAY, UTC);
    long                   startedAt = timestamp(UTC, 2026, 1, 1, 0, 0, 0, 0);
    long                   from      = timestamp(UTC, 2026, 5, 20, 10, 0, 0, 0);
    long                   to        = timestamp(UTC, 2026, 5, 21, 10, 0, 0, 0);

    //
    //
    boolean needRun = checker.needRun(startedAt, from, to);
    //
    //

    assertThat(needRun).isTrue();
  }

  @Test
  public void needRun_shouldReturnFalseWhenNeitherWeekMatches() {
    RunChecker_DAY_OF_WEEK checker   = new RunChecker_DAY_OF_WEEK(SATURDAY, UTC);
    long                   startedAt = timestamp(UTC, 2026, 1, 1, 0, 0, 0, 0);
    long                   from      = timestamp(UTC, 2026, 5, 21, 10, 0, 0, 0);
    long                   to        = timestamp(UTC, 2026, 5, 22, 10, 0, 0, 0);

    //
    //
    boolean needRun = checker.needRun(startedAt, from, to);
    //
    //

    assertThat(needRun).isFalse();
  }

  @Test
  public void needRun_shouldReturnTrueWhenFromAndToAreSameMatchingWeek() {
    RunChecker_DAY_OF_WEEK checker   = new RunChecker_DAY_OF_WEEK(THURSDAY, UTC);
    long                   startedAt = timestamp(UTC, 2026, 1, 1, 0, 0, 0, 0);
    long                   timestamp = timestamp(UTC, 2026, 5, 21, 10, 0, 0, 0);

    //
    //
    boolean needRun = checker.needRun(startedAt, timestamp, timestamp);
    //
    //

    assertThat(needRun).isTrue();
  }

  @Test
  public void needRun_shouldUseConfiguredTimeZoneForFromTimestamp() {
    TimeZone               almaty    = TimeZone.getTimeZone("Asia/Almaty");
    RunChecker_DAY_OF_WEEK checker   = new RunChecker_DAY_OF_WEEK(MONDAY, almaty);
    long                   startedAt = timestamp(UTC, 2026, 1, 1, 0, 0, 0, 0);
    long                   from      = timestamp(UTC, 2026, 5, 24, 19, 0, 0, 0);
    long                   to        = timestamp(UTC, 2026, 5, 24, 20, 0, 0, 0);

    //
    //
    boolean needRun = checker.needRun(startedAt, from, to);
    //
    //

    assertThat(needRun).isTrue();
  }

  @Test
  public void needRun_shouldIgnoreSchedulerStartTimestamp() {
    RunChecker_DAY_OF_WEEK checker   = new RunChecker_DAY_OF_WEEK(THURSDAY, UTC);
    long                   startedAt = timestamp(UTC, 2030, 1, 1, 0, 0, 0, 0);
    long                   from      = timestamp(UTC, 2026, 5, 21, 10, 0, 0, 0);
    long                   to        = timestamp(UTC, 2026, 5, 22, 10, 0, 0, 0);

    //
    //
    boolean needRun = checker.needRun(startedAt, from, to);
    //
    //

    assertThat(needRun).isTrue();
  }

  @Test
  public void needRun_shouldSupportAllWeekValues() {
    assertThat(needRun(SUNDAY, 2026, 5, 24)).isTrue();
    assertThat(needRun(MONDAY, 2026, 5, 25)).isTrue();
    assertThat(needRun(TUESDAY, 2026, 5, 26)).isTrue();
    assertThat(needRun(WEDNESDAY, 2026, 5, 27)).isTrue();
    assertThat(needRun(THURSDAY, 2026, 5, 28)).isTrue();
    assertThat(needRun(FRIDAY, 2026, 5, 29)).isTrue();
    assertThat(needRun(SATURDAY, 2026, 5, 30)).isTrue();
  }

  private static boolean needRun(DayOfWeek dayOfWeek, int year, int month, int day) {
    return new RunChecker_DAY_OF_WEEK(dayOfWeek, UTC).needRun(
        0,
        timestamp(UTC, year, month, day, 0, 0, 0, 0),
        timestamp(UTC, year, month, day, 1, 0, 0, 0)
    );
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
