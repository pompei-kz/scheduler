package kz.pompei.scheduler.core.run_checker;

import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RunChecker_PERIODIC_Test extends RunCheckerTestParent {

  @Test
  public void needRun_shouldReturnTrueWhenFirstRunIsInsideInterval() {
    RunChecker_PERIODIC checker   = new RunChecker_PERIODIC(1_000, 5_000);
    long                startedAt = 10_000;
    long                from      = 11_000;
    long                to        = 11_001;

    //
    //
    boolean needRun = checker.needRun(startedAt, from, to);
    //
    //

    assertThat(needRun).isTrue();
  }

  @Test
  public void needRun_shouldReturnFalseWhenRunTimeEqualsIntervalEnd() {
    RunChecker_PERIODIC checker   = new RunChecker_PERIODIC(1_000, 5_000);
    long                startedAt = 10_000;
    long                from      = 10_000;
    long                to        = 11_000;

    //
    //
    boolean needRun = checker.needRun(startedAt, from, to);
    //
    //

    assertThat(needRun).isFalse();
  }

  @Test
  public void needRun_shouldReturnTrueWhenLaterPeriodicRunIsInsideInterval() {
    RunChecker_PERIODIC checker   = new RunChecker_PERIODIC(1_000, 5_000);
    long                startedAt = 10_000;
    long                from      = 25_999;
    long                to        = 26_001;

    //
    //
    boolean needRun = checker.needRun(startedAt, from, to);
    //
    //

    assertThat(needRun).isTrue();
  }

  @Test
  public void needRun_shouldReturnTrueWhenLaterPeriodicRunEqualsIntervalStart() {
    RunChecker_PERIODIC checker   = new RunChecker_PERIODIC(1_000, 5_000);
    long                startedAt = 10_000;
    long                from      = 26_000;
    long                to        = 26_001;

    //
    //
    boolean needRun = checker.needRun(startedAt, from, to);
    //
    //

    assertThat(needRun).isTrue();
  }

  @Test
  public void needRun_shouldReturnFalseWhenNoPeriodicRunIsInsideInterval() {
    RunChecker_PERIODIC checker   = new RunChecker_PERIODIC(1_000, 5_000);
    long                startedAt = 10_000;
    long                from      = 21_001;
    long                to        = 25_999;

    //
    //
    boolean needRun = checker.needRun(startedAt, from, to);
    //
    //

    assertThat(needRun).isFalse();
  }

  @Test
  public void needRun_shouldReturnTrueWhenFirstRunAfterNegativeOffsetIsInsideInterval() {
    RunChecker_PERIODIC checker   = new RunChecker_PERIODIC(-1_000, 5_000);
    long                startedAt = 10_000;
    long                from      = 8_999;
    long                to        = 9_001;

    //
    //
    boolean needRun = checker.needRun(startedAt, from, to);
    //
    //

    assertThat(needRun).isTrue();
  }

  @Test
  public void needRun_shouldReturnFalseWhenIntervalIsBeforeFirstRun() {
    RunChecker_PERIODIC checker   = new RunChecker_PERIODIC(1_000, 5_000);
    long                startedAt = 10_000;
    long                from      = 9_000;
    long                to        = 11_000;

    //
    //
    boolean needRun = checker.needRun(startedAt, from, to);
    //
    //

    assertThat(needRun).isFalse();
  }

  @Test
  public void needRun_shouldReturnFalseWhenIntervalIsEmpty() {
    RunChecker_PERIODIC checker   = new RunChecker_PERIODIC(1_000, 5_000);
    long                startedAt = 10_000;
    long                timestamp = 11_000;

    //
    //
    boolean needRun = checker.needRun(startedAt, timestamp, timestamp);
    //
    //

    assertThat(needRun).isFalse();
  }

  @Test
  public void needRun_shouldReturnFalseWhenPeriodIsZero() {
    RunChecker_PERIODIC checker   = new RunChecker_PERIODIC(1_000, 0);
    long                startedAt = 10_000;
    long                from      = 11_000;
    long                to        = 11_001;

    //
    //
    boolean needRun = checker.needRun(startedAt, from, to);
    //
    //

    assertThat(needRun).isFalse();
  }

  @Test
  public void needRun_shouldReturnFalseWhenPeriodIsNegative() {
    RunChecker_PERIODIC checker   = new RunChecker_PERIODIC(1_000, -5_000);
    long                startedAt = 10_000;
    long                from      = 11_000;
    long                to        = 11_001;

    //
    //
    boolean needRun = checker.needRun(startedAt, from, to);
    //
    //

    assertThat(needRun).isFalse();
  }
}
