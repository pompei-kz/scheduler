package kz.pompei.scheduler.core.run_checker;

import java.util.TimeZone;
import kz.pompei.scheduler.core.TestParent;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RunChecker_FromHMS_ToHMS_Test extends TestParent {


  @Test
  public void needRun_shouldReturnTrueWhenFromTimestampIsInsideInterval() {
    RunChecker_FromHMS_ToHMS checker   = new RunChecker_FromHMS_ToHMS(UTC, 10, 0, 0, 12, 0, 0);
    long                     startedAt = timestamp(UTC, 2026, 5, 1, 0, 0, 0, 0);
    long                     from      = timestamp(UTC, 2026, 5, 21, 11, 0, 0, 0);
    long                     to        = timestamp(UTC, 2026, 5, 21, 13, 0, 0, 0);

    //
    //
    boolean needRun = checker.needRun(startedAt, from, to);
    //
    //

    assertThat(needRun).isTrue();
  }

  @Test
  public void needRun_shouldReturnTrueWhenToTimestampIsInsideInterval() {
    RunChecker_FromHMS_ToHMS checker   = new RunChecker_FromHMS_ToHMS(UTC, 10, 0, 0, 12, 0, 0);
    long                     startedAt = timestamp(UTC, 2026, 5, 1, 0, 0, 0, 0);
    long                     from      = timestamp(UTC, 2026, 5, 21, 9, 0, 0, 0);
    long                     to        = timestamp(UTC, 2026, 5, 21, 11, 0, 0, 0);

    //
    //
    boolean needRun = checker.needRun(startedAt, from, to);
    //
    //

    assertThat(needRun).isTrue();
  }

  @Test
  public void needRun_shouldReturnFalseWhenNeitherTimestampIsInsideInterval() {
    RunChecker_FromHMS_ToHMS checker   = new RunChecker_FromHMS_ToHMS(UTC, 10, 0, 0, 12, 0, 0);
    long                     startedAt = timestamp(UTC, 2026, 5, 1, 0, 0, 0, 0);
    long                     from      = timestamp(UTC, 2026, 5, 21, 9, 0, 0, 0);
    long                     to        = timestamp(UTC, 2026, 5, 21, 13, 0, 0, 0);

    //
    //
    boolean needRun = checker.needRun(startedAt, from, to);
    //
    //

    assertThat(needRun).isFalse();
  }

  @Test
  public void needRun_shouldReturnFalseWhenTimestampEqualsFromBoundary() {
    RunChecker_FromHMS_ToHMS checker   = new RunChecker_FromHMS_ToHMS(UTC, 10, 0, 0, 12, 0, 0);
    long                     startedAt = timestamp(UTC, 2026, 5, 1, 0, 0, 0, 0);
    long                     from      = timestamp(UTC, 2026, 5, 21, 10, 0, 0, 0);
    long                     to        = timestamp(UTC, 2026, 5, 21, 13, 0, 0, 0);

    //
    //
    boolean needRun = checker.needRun(startedAt, from, to);
    //
    //

    assertThat(needRun).isFalse();
  }

  @Test
  public void needRun_shouldReturnFalseWhenTimestampEqualsToBoundary() {
    RunChecker_FromHMS_ToHMS checker   = new RunChecker_FromHMS_ToHMS(UTC, 10, 0, 0, 12, 0, 0);
    long                     startedAt = timestamp(UTC, 2026, 5, 1, 0, 0, 0, 0);
    long                     from      = timestamp(UTC, 2026, 5, 21, 8, 0, 0, 0);
    long                     to        = timestamp(UTC, 2026, 5, 21, 12, 0, 0, 0);

    //
    //
    boolean needRun = checker.needRun(startedAt, from, to);
    //
    //

    assertThat(needRun).isFalse();
  }

  @Test
  public void needRun_shouldUseDateFromEachTimestamp() {
    RunChecker_FromHMS_ToHMS checker   = new RunChecker_FromHMS_ToHMS(UTC, 10, 0, 0, 12, 0, 0);
    long                     startedAt = timestamp(UTC, 2026, 5, 1, 0, 0, 0, 0);
    long                     from      = timestamp(UTC, 2026, 5, 21, 9, 0, 0, 0);
    long                     to        = timestamp(UTC, 2026, 5, 22, 11, 0, 0, 0);

    //
    //
    boolean needRun = checker.needRun(startedAt, from, to);
    //
    //

    assertThat(needRun).isTrue();
  }

  @Test
  public void needRun_shouldUseConfiguredTimeZone() {
    TimeZone                 almaty    = TimeZone.getTimeZone("Asia/Almaty");
    RunChecker_FromHMS_ToHMS checker   = new RunChecker_FromHMS_ToHMS(almaty, 1, 0, 0, 3, 0, 0);
    long                     startedAt = timestamp(UTC, 2026, 5, 1, 0, 0, 0, 0);
    long                     from      = timestamp(UTC, 2026, 5, 21, 20, 30, 0, 0);
    long                     to        = timestamp(UTC, 2026, 5, 21, 22, 0, 0, 0);

    //
    //
    boolean needRun = checker.needRun(startedAt, from, to);
    //
    //

    assertThat(needRun).isTrue();
  }

  @Test
  public void needRun_shouldUseTokyoTimeZoneForNextLocalDay() {
    TimeZone                 tokyo     = TimeZone.getTimeZone("Asia/Tokyo");
    RunChecker_FromHMS_ToHMS checker   = new RunChecker_FromHMS_ToHMS(tokyo, 4, 0, 0, 6, 0, 0);
    long                     startedAt = timestamp(UTC, 2026, 5, 1, 0, 0, 0, 0);
    long                     from      = timestamp(UTC, 2026, 5, 21, 20, 30, 0, 0);
    long                     to        = timestamp(UTC, 2026, 5, 21, 23, 0, 0, 0);

    //
    //
    boolean needRun = checker.needRun(startedAt, from, to);
    //
    //

    assertThat(needRun).isTrue();
  }

  @Test
  public void needRun_shouldUseNewYorkTimeZoneForPreviousLocalDay() {
    TimeZone                 newYork   = TimeZone.getTimeZone("America/New_York");
    RunChecker_FromHMS_ToHMS checker   = new RunChecker_FromHMS_ToHMS(newYork, 20, 0, 0, 22, 0, 0);
    long                     startedAt = timestamp(UTC, 2026, 5, 1, 0, 0, 0, 0);
    long                     from      = timestamp(UTC, 2026, 5, 22, 1, 30, 0, 0);
    long                     to        = timestamp(UTC, 2026, 5, 22, 3, 0, 0, 0);

    //
    //
    boolean needRun = checker.needRun(startedAt, from, to);
    //
    //

    assertThat(needRun).isTrue();
  }

  @Test
  public void needRun_shouldReturnFalseWhenUtcTimeMatchesButConfiguredTimeZoneDoesNot() {
    TimeZone                 newYork   = TimeZone.getTimeZone("America/New_York");
    RunChecker_FromHMS_ToHMS checker   = new RunChecker_FromHMS_ToHMS(newYork, 1, 0, 0, 3, 0, 0);
    long                     startedAt = timestamp(UTC, 2026, 5, 1, 0, 0, 0, 0);
    long                     from      = timestamp(UTC, 2026, 5, 22, 1, 30, 0, 0);
    long                     to        = timestamp(UTC, 2026, 5, 22, 4, 0, 0, 0);

    //
    //
    boolean needRun = checker.needRun(startedAt, from, to);
    //
    //

    assertThat(needRun).isFalse();
  }

  @Test
  public void needRun_shouldIgnoreSchedulerStartTimestamp() {
    RunChecker_FromHMS_ToHMS checker   = new RunChecker_FromHMS_ToHMS(UTC, 10, 0, 0, 12, 0, 0);
    long                     startedAt = timestamp(UTC, 2030, 1, 1, 0, 0, 0, 0);
    long                     from      = timestamp(UTC, 2026, 5, 21, 11, 0, 0, 0);
    long                     to        = timestamp(UTC, 2026, 5, 21, 13, 0, 0, 0);

    //
    //
    boolean needRun = checker.needRun(startedAt, from, to);
    //
    //

    assertThat(needRun).isTrue();
  }

  @Test
  public void needRun_shouldReturnFalseWhenFromHmsIsAfterToHms() {
    RunChecker_FromHMS_ToHMS checker   = new RunChecker_FromHMS_ToHMS(UTC, 12, 0, 0, 10, 0, 0);
    long                     startedAt = timestamp(UTC, 2026, 5, 1, 0, 0, 0, 0);
    long                     from      = timestamp(UTC, 2026, 5, 21, 11, 0, 0, 0);
    long                     to        = timestamp(UTC, 2026, 5, 21, 13, 0, 0, 0);

    //
    //
    boolean needRun = checker.needRun(startedAt, from, to);
    //
    //

    assertThat(needRun).isFalse();
  }
}
