package kz.pompei.scheduler.core.run_checker;

import java.util.TimeZone;
import kz.pompei.scheduler.core.TestParent;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RunChecker_HMS_Test extends TestParent {


  @Test
  public void needRun_shouldReturnTrueWhenScheduledTimeIsInsideInterval() {
    RunChecker_HMS checker   = new RunChecker_HMS(12, 30, 15, UTC);
    long           startedAt = timestamp(UTC, 2026, 5, 21, 10, 0, 0, 0);
    long           from      = timestamp(UTC, 2026, 5, 21, 12, 30, 15, 0);
    long           to        = timestamp(UTC, 2026, 5, 21, 12, 30, 16, 0);

    //
    //
    boolean needRun = checker.needRun(startedAt, from, to);
    //
    //

    assertThat(needRun).isTrue();
  }

  @Test
  public void needRun_shouldReturnFalseWhenScheduledTimeEqualsIntervalEnd() {
    RunChecker_HMS checker   = new RunChecker_HMS(12, 30, 15, UTC);
    long           startedAt = timestamp(UTC, 2026, 5, 21, 10, 0, 0, 0);
    long           from      = timestamp(UTC, 2026, 5, 21, 12, 30, 14, 0);
    long           to        = timestamp(UTC, 2026, 5, 21, 12, 30, 15, 0);

    //
    //
    boolean needRun = checker.needRun(startedAt, from, to);
    //
    //

    assertThat(needRun).isFalse();
  }

  @Test
  public void needRun_shouldReturnFalseWhenScheduledTimeIsBeforeInterval() {
    RunChecker_HMS checker   = new RunChecker_HMS(12, 30, 15, UTC);
    long           startedAt = timestamp(UTC, 2026, 5, 21, 10, 0, 0, 0);
    long           from      = timestamp(UTC, 2026, 5, 21, 12, 30, 16, 0);
    long           to        = timestamp(UTC, 2026, 5, 21, 12, 30, 17, 0);

    //
    //
    boolean needRun = checker.needRun(startedAt, from, to);
    //
    //

    assertThat(needRun).isFalse();
  }

  @Test
  public void needRun_shouldReturnTrueForSameTimeOnNextDay() {
    RunChecker_HMS checker   = new RunChecker_HMS(12, 30, 15, UTC);
    long           startedAt = timestamp(UTC, 2026, 5, 21, 10, 0, 0, 0);
    long           from      = timestamp(UTC, 2026, 5, 22, 12, 30, 15, 0);
    long           to        = timestamp(UTC, 2026, 5, 22, 12, 30, 16, 0);

    //
    //
    boolean needRun = checker.needRun(startedAt, from, to);
    //
    //

    assertThat(needRun).isTrue();
  }

  @Test
  public void needRun_shouldUseDateFromIntervalStartNotSchedulerStart() {
    RunChecker_HMS checker   = new RunChecker_HMS(12, 30, 15, UTC);
    long           startedAt = timestamp(UTC, 2026, 5, 22, 10, 0, 0, 0);
    long           from      = timestamp(UTC, 2026, 5, 21, 12, 30, 15, 0);
    long           to        = timestamp(UTC, 2026, 5, 21, 12, 30, 16, 0);

    //
    //
    boolean needRun = checker.needRun(startedAt, from, to);
    //
    //

    assertThat(needRun).isTrue();
  }

  @Test
  public void needRun_shouldReturnTrueInAnotherMonth() {
    RunChecker_HMS checker   = new RunChecker_HMS(8, 5, 3, UTC);
    long           startedAt = timestamp(UTC, 2026, 5, 21, 10, 0, 0, 0);
    long           from      = timestamp(UTC, 2026, 11, 3, 8, 5, 3, 0);
    long           to        = timestamp(UTC, 2026, 11, 3, 8, 5, 4, 0);

    //
    //
    boolean needRun = checker.needRun(startedAt, from, to);
    //
    //

    assertThat(needRun).isTrue();
  }

  @Test
  public void needRun_shouldReturnTrueInAnotherYear() {
    RunChecker_HMS checker   = new RunChecker_HMS(8, 5, 3, UTC);
    long           startedAt = timestamp(UTC, 2026, 5, 21, 10, 0, 0, 0);
    long           from      = timestamp(UTC, 2031, 11, 3, 8, 5, 3, 0);
    long           to        = timestamp(UTC, 2031, 11, 3, 8, 5, 4, 0);

    //
    //
    boolean needRun = checker.needRun(startedAt, from, to);
    //
    //

    assertThat(needRun).isTrue();
  }

  @Test
  public void needRun_shouldReturnFalseWhenNewMonthMidnightWouldRequireMovingFromPreviousMonth() {
    RunChecker_HMS checker   = new RunChecker_HMS(0, 0, 0, UTC);
    long           startedAt = timestamp(UTC, 2026, 5, 21, 10, 0, 0, 0);
    long           from      = timestamp(UTC, 2026, 1, 31, 23, 59, 59, 999);
    long           to        = timestamp(UTC, 2026, 2, 1, 0, 0, 0, 1);

    //
    //
    boolean needRun = checker.needRun(startedAt, from, to);
    //
    //

    assertThat(needRun).isFalse();
  }

  @Test
  public void needRun_shouldReturnFalseWhenNewYearMidnightWouldRequireMovingFromPreviousYear() {
    RunChecker_HMS checker   = new RunChecker_HMS(0, 0, 0, UTC);
    long           startedAt = timestamp(UTC, 2026, 5, 21, 10, 0, 0, 0);
    long           from      = timestamp(UTC, 2026, 12, 31, 23, 59, 59, 999);
    long           to        = timestamp(UTC, 2027, 1, 1, 0, 0, 0, 1);

    //
    //
    boolean needRun = checker.needRun(startedAt, from, to);
    //
    //

    assertThat(needRun).isFalse();
  }

  @Test
  public void needRun_shouldReturnTrueForLastSecondBeforeMonthBoundary() {
    RunChecker_HMS checker   = new RunChecker_HMS(23, 59, 59, UTC);
    long           startedAt = timestamp(UTC, 2026, 5, 21, 10, 0, 0, 0);
    long           from      = timestamp(UTC, 2026, 1, 31, 23, 59, 59, 0);
    long           to        = timestamp(UTC, 2026, 2, 1, 0, 0, 0, 0);

    //
    //
    boolean needRun = checker.needRun(startedAt, from, to);
    //
    //

    assertThat(needRun).isTrue();
  }

  @Test
  public void needRun_shouldReturnTrueForLastSecondBeforeYearBoundary() {
    RunChecker_HMS checker   = new RunChecker_HMS(23, 59, 59, UTC);
    long           startedAt = timestamp(UTC, 2026, 5, 21, 10, 0, 0, 0);
    long           from      = timestamp(UTC, 2026, 12, 31, 23, 59, 59, 0);
    long           to        = timestamp(UTC, 2027, 1, 1, 0, 0, 0, 0);

    //
    //
    boolean needRun = checker.needRun(startedAt, from, to);
    //
    //

    assertThat(needRun).isTrue();
  }

  @Test
  public void needRun_shouldReturnFalseWhenMidnightWouldRequireMovingToNextDay() {
    RunChecker_HMS checker   = new RunChecker_HMS(0, 0, 0, UTC);
    long           startedAt = timestamp(UTC, 2026, 5, 21, 10, 0, 0, 0);
    long           from      = timestamp(UTC, 2026, 5, 21, 23, 59, 59, 999);
    long           to        = timestamp(UTC, 2026, 5, 22, 0, 0, 0, 1);

    //
    //
    boolean needRun = checker.needRun(startedAt, from, to);
    //
    //

    assertThat(needRun).isFalse();
  }

  @Test
  public void needRun_shouldReturnTrueWhenLastSecondOfDayIsInsideInterval() {
    RunChecker_HMS checker   = new RunChecker_HMS(23, 59, 59, UTC);
    long           startedAt = timestamp(UTC, 2026, 5, 21, 10, 0, 0, 0);
    long           from      = timestamp(UTC, 2026, 5, 21, 23, 59, 59, 0);
    long           to        = timestamp(UTC, 2026, 5, 22, 0, 0, 0, 0);

    //
    //
    boolean needRun = checker.needRun(startedAt, from, to);
    //
    //

    assertThat(needRun).isTrue();
  }

  @Test
  public void needRun_shouldReturnFalseWhenIntervalIsEmpty() {
    RunChecker_HMS checker   = new RunChecker_HMS(12, 30, 15, UTC);
    long           startedAt = timestamp(UTC, 2026, 5, 21, 10, 0, 0, 0);
    long           timestamp = timestamp(UTC, 2026, 5, 21, 12, 30, 15, 0);

    //
    //
    boolean needRun = checker.needRun(startedAt, timestamp, timestamp);
    //
    //

    assertThat(needRun).isFalse();
  }

  @Test
  public void needRun_shouldUseConfiguredTimeZone() {
    TimeZone       almaty    = TimeZone.getTimeZone("Asia/Almaty");
    RunChecker_HMS checker   = new RunChecker_HMS(12, 30, 15, almaty);
    long           startedAt = timestamp(almaty, 2026, 5, 21, 10, 0, 0, 0);
    long           from      = timestamp(almaty, 2026, 5, 21, 12, 30, 15, 0);
    long           to        = timestamp(almaty, 2026, 5, 21, 12, 30, 16, 0);

    //
    //
    boolean needRun = checker.needRun(startedAt, from, to);
    //
    //

    assertThat(needRun).isTrue();
  }
}
