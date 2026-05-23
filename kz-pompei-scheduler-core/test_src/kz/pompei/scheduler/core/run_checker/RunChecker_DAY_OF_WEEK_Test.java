package kz.pompei.scheduler.core.run_checker;

import java.util.TimeZone;
import kz.pompei.scheduler.core.TestParent;
import org.testng.annotations.Test;

import static kz.pompei.scheduler.core.run_checker.DayOfWeek.FRIDAY;
import static kz.pompei.scheduler.core.run_checker.DayOfWeek.MONDAY;
import static kz.pompei.scheduler.core.run_checker.DayOfWeek.SATURDAY;
import static kz.pompei.scheduler.core.run_checker.DayOfWeek.SUNDAY;
import static kz.pompei.scheduler.core.run_checker.DayOfWeek.THURSDAY;
import static kz.pompei.scheduler.core.run_checker.DayOfWeek.TUESDAY;
import static kz.pompei.scheduler.core.run_checker.DayOfWeek.WEDNESDAY;
import static org.assertj.core.api.Assertions.assertThat;

public class RunChecker_DAY_OF_WEEK_Test extends TestParent {


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
  public void needRun_shouldSupportSunday() {
    RunChecker_DAY_OF_WEEK checker = new RunChecker_DAY_OF_WEEK(SUNDAY, UTC);
    long                   from    = timestamp(UTC, 2026, 5, 24, 0, 0, 0, 0);
    long                   to      = timestamp(UTC, 2026, 5, 24, 1, 0, 0, 0);

    //
    //
    boolean needRun = checker.needRun(0, from, to);
    //
    //

    assertThat(needRun).isTrue();
  }

  @Test
  public void needRun_shouldSupportMonday() {
    RunChecker_DAY_OF_WEEK checker = new RunChecker_DAY_OF_WEEK(MONDAY, UTC);
    long                   from    = timestamp(UTC, 2026, 5, 25, 0, 0, 0, 0);
    long                   to      = timestamp(UTC, 2026, 5, 25, 1, 0, 0, 0);

    //
    //
    boolean needRun = checker.needRun(0, from, to);
    //
    //

    assertThat(needRun).isTrue();
  }

  @Test
  public void needRun_shouldSupportTuesday() {
    RunChecker_DAY_OF_WEEK checker = new RunChecker_DAY_OF_WEEK(TUESDAY, UTC);
    long                   from    = timestamp(UTC, 2026, 5, 26, 0, 0, 0, 0);
    long                   to      = timestamp(UTC, 2026, 5, 26, 1, 0, 0, 0);

    //
    //
    boolean needRun = checker.needRun(0, from, to);
    //
    //

    assertThat(needRun).isTrue();
  }

  @Test
  public void needRun_shouldSupportWednesday() {
    RunChecker_DAY_OF_WEEK checker = new RunChecker_DAY_OF_WEEK(WEDNESDAY, UTC);
    long                   from    = timestamp(UTC, 2026, 5, 27, 0, 0, 0, 0);
    long                   to      = timestamp(UTC, 2026, 5, 27, 1, 0, 0, 0);

    //
    //
    boolean needRun = checker.needRun(0, from, to);
    //
    //

    assertThat(needRun).isTrue();
  }

  @Test
  public void needRun_shouldSupportThursday() {
    RunChecker_DAY_OF_WEEK checker = new RunChecker_DAY_OF_WEEK(THURSDAY, UTC);
    long                   from    = timestamp(UTC, 2026, 5, 28, 0, 0, 0, 0);
    long                   to      = timestamp(UTC, 2026, 5, 28, 1, 0, 0, 0);

    //
    //
    boolean needRun = checker.needRun(0, from, to);
    //
    //

    assertThat(needRun).isTrue();
  }

  @Test
  public void needRun_shouldSupportFriday() {
    RunChecker_DAY_OF_WEEK checker = new RunChecker_DAY_OF_WEEK(FRIDAY, UTC);
    long                   from    = timestamp(UTC, 2026, 5, 29, 0, 0, 0, 0);
    long                   to      = timestamp(UTC, 2026, 5, 29, 1, 0, 0, 0);

    //
    //
    boolean needRun = checker.needRun(0, from, to);
    //
    //

    assertThat(needRun).isTrue();
  }

  @Test
  public void needRun_shouldSupportSaturday() {
    RunChecker_DAY_OF_WEEK checker = new RunChecker_DAY_OF_WEEK(SATURDAY, UTC);
    long                   from    = timestamp(UTC, 2026, 5, 30, 0, 0, 0, 0);
    long                   to      = timestamp(UTC, 2026, 5, 30, 1, 0, 0, 0);

    //
    //
    boolean needRun = checker.needRun(0, from, to);
    //
    //

    assertThat(needRun).isTrue();
  }
}
