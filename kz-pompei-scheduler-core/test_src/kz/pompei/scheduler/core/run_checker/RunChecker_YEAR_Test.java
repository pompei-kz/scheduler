package kz.pompei.scheduler.core.run_checker;

import java.util.TimeZone;
import kz.pompei.scheduler.core.TestParent;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RunChecker_YEAR_Test extends TestParent {


  @Test
  public void needRun_shouldReturnTrueWhenFromYearMatches() {
    RunChecker_YEAR checker   = new RunChecker_YEAR(2026, UTC);
    long            startedAt = timestamp(UTC, 2020, 1, 1, 0, 0, 0, 0);
    long            from      = timestamp(UTC, 2026, 5, 21, 10, 0, 0, 0);
    long            to        = timestamp(UTC, 2027, 6, 22, 10, 0, 0, 0);

    //
    //
    boolean needRun = checker.needRun(startedAt, from, to);
    //
    //

    assertThat(needRun).isTrue();
  }

  @Test
  public void needRun_shouldReturnTrueWhenToYearMatches() {
    RunChecker_YEAR checker   = new RunChecker_YEAR(2027, UTC);
    long            startedAt = timestamp(UTC, 2020, 1, 1, 0, 0, 0, 0);
    long            from      = timestamp(UTC, 2026, 5, 21, 10, 0, 0, 0);
    long            to        = timestamp(UTC, 2027, 6, 22, 10, 0, 0, 0);

    //
    //
    boolean needRun = checker.needRun(startedAt, from, to);
    //
    //

    assertThat(needRun).isTrue();
  }

  @Test
  public void needRun_shouldReturnFalseWhenNeitherYearMatches() {
    RunChecker_YEAR checker   = new RunChecker_YEAR(2028, UTC);
    long            startedAt = timestamp(UTC, 2020, 1, 1, 0, 0, 0, 0);
    long            from      = timestamp(UTC, 2026, 5, 21, 10, 0, 0, 0);
    long            to        = timestamp(UTC, 2027, 6, 22, 10, 0, 0, 0);

    //
    //
    boolean needRun = checker.needRun(startedAt, from, to);
    //
    //

    assertThat(needRun).isFalse();
  }

  @Test
  public void needRun_shouldReturnTrueWhenFromAndToAreSameMatchingYear() {
    RunChecker_YEAR checker   = new RunChecker_YEAR(2026, UTC);
    long            startedAt = timestamp(UTC, 2020, 1, 1, 0, 0, 0, 0);
    long            timestamp = timestamp(UTC, 2026, 5, 21, 10, 0, 0, 0);

    //
    //
    boolean needRun = checker.needRun(startedAt, timestamp, timestamp);
    //
    //

    assertThat(needRun).isTrue();
  }

  @Test
  public void needRun_shouldUseConfiguredTimeZoneForFromTimestamp() {
    TimeZone        almaty    = TimeZone.getTimeZone("Asia/Almaty");
    RunChecker_YEAR checker   = new RunChecker_YEAR(2027, almaty);
    long            startedAt = timestamp(UTC, 2020, 1, 1, 0, 0, 0, 0);
    long            from      = timestamp(UTC, 2026, 12, 31, 19, 0, 0, 0);
    long            to        = timestamp(UTC, 2026, 12, 31, 20, 0, 0, 0);

    //
    //
    boolean needRun = checker.needRun(startedAt, from, to);
    //
    //

    assertThat(needRun).isTrue();
  }

  @Test
  public void needRun_shouldIgnoreSchedulerStartTimestamp() {
    RunChecker_YEAR checker   = new RunChecker_YEAR(2026, UTC);
    long            startedAt = timestamp(UTC, 2030, 1, 1, 0, 0, 0, 0);
    long            from      = timestamp(UTC, 2026, 5, 21, 10, 0, 0, 0);
    long            to        = timestamp(UTC, 2027, 6, 22, 10, 0, 0, 0);

    //
    //
    boolean needRun = checker.needRun(startedAt, from, to);
    //
    //

    assertThat(needRun).isTrue();
  }
}
