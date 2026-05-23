package kz.pompei.scheduler.core.run_checker;

import java.util.TimeZone;
import kz.pompei.scheduler.core.TestParent;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RunChecker_MONTH_Test extends TestParent {


  @Test
  public void needRun_shouldReturnTrueWhenFromMonthMatches() {
    RunChecker_MONTH checker   = new RunChecker_MONTH(5, UTC);
    long             startedAt = timestamp(UTC, 2026, 1, 1, 0, 0, 0, 0);
    long             from      = timestamp(UTC, 2026, 5, 21, 10, 0, 0, 0);
    long             to        = timestamp(UTC, 2026, 6, 22, 10, 0, 0, 0);

    //
    //
    boolean needRun = checker.needRun(startedAt, from, to);
    //
    //

    assertThat(needRun).isTrue();
  }

  @Test
  public void needRun_shouldReturnTrueWhenToMonthMatches() {
    RunChecker_MONTH checker   = new RunChecker_MONTH(6, UTC);
    long             startedAt = timestamp(UTC, 2026, 1, 1, 0, 0, 0, 0);
    long             from      = timestamp(UTC, 2026, 5, 21, 10, 0, 0, 0);
    long             to        = timestamp(UTC, 2026, 6, 22, 10, 0, 0, 0);

    //
    //
    boolean needRun = checker.needRun(startedAt, from, to);
    //
    //

    assertThat(needRun).isTrue();
  }

  @Test
  public void needRun_shouldReturnFalseWhenNeitherMonthMatches() {
    RunChecker_MONTH checker   = new RunChecker_MONTH(7, UTC);
    long             startedAt = timestamp(UTC, 2026, 1, 1, 0, 0, 0, 0);
    long             from      = timestamp(UTC, 2026, 5, 21, 10, 0, 0, 0);
    long             to        = timestamp(UTC, 2026, 6, 22, 10, 0, 0, 0);

    //
    //
    boolean needRun = checker.needRun(startedAt, from, to);
    //
    //

    assertThat(needRun).isFalse();
  }

  @Test
  public void needRun_shouldReturnTrueWhenFromAndToAreSameMatchingMonth() {
    RunChecker_MONTH checker   = new RunChecker_MONTH(5, UTC);
    long             startedAt = timestamp(UTC, 2026, 1, 1, 0, 0, 0, 0);
    long             timestamp = timestamp(UTC, 2026, 5, 21, 10, 0, 0, 0);

    //
    //
    boolean needRun = checker.needRun(startedAt, timestamp, timestamp);
    //
    //

    assertThat(needRun).isTrue();
  }

  @Test
  public void needRun_shouldUseConfiguredTimeZoneForFromTimestamp() {
    TimeZone         almaty    = TimeZone.getTimeZone("Asia/Almaty");
    RunChecker_MONTH checker   = new RunChecker_MONTH(6, almaty);
    long             startedAt = timestamp(UTC, 2026, 1, 1, 0, 0, 0, 0);
    long             from      = timestamp(UTC, 2026, 5, 31, 19, 0, 0, 0);
    long             to        = timestamp(UTC, 2026, 5, 31, 20, 0, 0, 0);

    //
    //
    boolean needRun = checker.needRun(startedAt, from, to);
    //
    //

    assertThat(needRun).isTrue();
  }

  @Test
  public void needRun_shouldIgnoreSchedulerStartTimestamp() {
    RunChecker_MONTH checker   = new RunChecker_MONTH(5, UTC);
    long             startedAt = timestamp(UTC, 2030, 1, 1, 0, 0, 0, 0);
    long             from      = timestamp(UTC, 2026, 5, 21, 10, 0, 0, 0);
    long             to        = timestamp(UTC, 2026, 6, 22, 10, 0, 0, 0);

    //
    //
    boolean needRun = checker.needRun(startedAt, from, to);
    //
    //

    assertThat(needRun).isTrue();
  }
}
