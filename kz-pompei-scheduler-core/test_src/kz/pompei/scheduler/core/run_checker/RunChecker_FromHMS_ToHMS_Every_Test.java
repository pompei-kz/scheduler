package kz.pompei.scheduler.core.run_checker;

import java.util.TimeZone;
import kz.pompei.scheduler.core.TestParent;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RunChecker_FromHMS_ToHMS_Every_Test extends TestParent {

  @Test
  public void needRun_shouldReturnTrueWhenFirstPointEqualsIntervalStart() {
    RunChecker_FromHMS_ToHMS_Every checker   = new RunChecker_FromHMS_ToHMS_Every(UTC, 10, 0, 0, 12, 0, 0, 30 * 60 * 1000);
    long                           startedAt = timestamp(UTC, 2026, 5, 1, 0, 0, 0, 0);
    long                           from      = timestamp(UTC, 2026, 5, 21, 10, 0, 0, 0);
    long                           to        = timestamp(UTC, 2026, 5, 21, 10, 0, 1, 0);

    //
    //
    boolean needRun = checker.needRun(startedAt, from, to);
    //
    //

    assertThat(needRun).isTrue();
  }

  @Test
  public void needRun_shouldReturnFalseWhenPointEqualsIntervalEnd() {
    RunChecker_FromHMS_ToHMS_Every checker   = new RunChecker_FromHMS_ToHMS_Every(UTC, 10, 0, 0, 12, 0, 0, 30 * 60 * 1000);
    long                           startedAt = timestamp(UTC, 2026, 5, 1, 0, 0, 0, 0);
    long                           from      = timestamp(UTC, 2026, 5, 21, 9, 59, 59, 0);
    long                           to        = timestamp(UTC, 2026, 5, 21, 10, 0, 0, 0);

    //
    //
    boolean needRun = checker.needRun(startedAt, from, to);
    //
    //

    assertThat(needRun).isFalse();
  }

  @Test
  public void needRun_shouldReturnTrueWhenLaterPointIsInsideInterval() {
    RunChecker_FromHMS_ToHMS_Every checker   = new RunChecker_FromHMS_ToHMS_Every(UTC, 10, 0, 0, 12, 0, 0, 30 * 60 * 1000);
    long                           startedAt = timestamp(UTC, 2026, 5, 1, 0, 0, 0, 0);
    long                           from      = timestamp(UTC, 2026, 5, 21, 10, 29, 59, 0);
    long                           to        = timestamp(UTC, 2026, 5, 21, 10, 30, 1, 0);

    //
    //
    boolean needRun = checker.needRun(startedAt, from, to);
    //
    //

    assertThat(needRun).isTrue();
  }

  @Test
  public void needRun_shouldReturnFalseWhenNoPointIsInsideInterval() {
    RunChecker_FromHMS_ToHMS_Every checker   = new RunChecker_FromHMS_ToHMS_Every(UTC, 10, 0, 0, 12, 0, 0, 30 * 60 * 1000);
    long                           startedAt = timestamp(UTC, 2026, 5, 1, 0, 0, 0, 0);
    long                           from      = timestamp(UTC, 2026, 5, 21, 10, 1, 0, 0);
    long                           to        = timestamp(UTC, 2026, 5, 21, 10, 29, 59, 0);

    //
    //
    boolean needRun = checker.needRun(startedAt, from, to);
    //
    //

    assertThat(needRun).isFalse();
  }

  @Test
  public void needRun_shouldReturnFalseWhenPointEqualsDailyToBoundary() {
    RunChecker_FromHMS_ToHMS_Every checker   = new RunChecker_FromHMS_ToHMS_Every(UTC, 10, 0, 0, 12, 0, 0, 30 * 60 * 1000);
    long                           startedAt = timestamp(UTC, 2026, 5, 1, 0, 0, 0, 0);
    long                           from      = timestamp(UTC, 2026, 5, 21, 12, 0, 0, 0);
    long                           to        = timestamp(UTC, 2026, 5, 21, 12, 0, 1, 0);

    //
    //
    boolean needRun = checker.needRun(startedAt, from, to);
    //
    //

    assertThat(needRun).isFalse();
  }

  @Test
  public void needRun_shouldUseConfiguredTimeZone() {
    TimeZone                       almaty    = TimeZone.getTimeZone("Asia/Almaty");
    RunChecker_FromHMS_ToHMS_Every checker   = new RunChecker_FromHMS_ToHMS_Every(almaty, 1, 0, 0, 3, 0, 0, 30 * 60 * 1000);
    long                           startedAt = timestamp(almaty, 2026, 5, 1, 0, 0, 0, 0);
    long                           from      = timestamp(almaty, 2026, 5, 22, 2, 29, 59, 0);
    long                           to        = timestamp(almaty, 2026, 5, 22, 2, 30, 1, 0);

    //
    //
    boolean needRun = checker.needRun(startedAt, from, to);
    //
    //

    assertThat(needRun).isTrue();
  }

  @Test
  public void needRun_shouldUseTokyoTimeZoneForNextLocalDay() {
    TimeZone                       tokyo     = TimeZone.getTimeZone("Asia/Tokyo");
    RunChecker_FromHMS_ToHMS_Every checker   = new RunChecker_FromHMS_ToHMS_Every(tokyo, 4, 0, 0, 6, 0, 0, 30 * 60 * 1000);
    long                           startedAt = timestamp(tokyo, 2026, 5, 1, 0, 0, 0, 0);
    long                           from      = timestamp(tokyo, 2026, 5, 22, 4, 29, 59, 0);
    long                           to        = timestamp(tokyo, 2026, 5, 22, 4, 30, 1, 0);

    //
    //
    boolean needRun = checker.needRun(startedAt, from, to);
    //
    //

    assertThat(needRun).isTrue();
  }

  @Test
  public void needRun_shouldUseNewYorkTimeZoneForPreviousLocalDay() {
    TimeZone                       newYork   = TimeZone.getTimeZone("America/New_York");
    RunChecker_FromHMS_ToHMS_Every checker   = new RunChecker_FromHMS_ToHMS_Every(newYork, 20, 0, 0, 22, 0, 0, 30 * 60 * 1000);
    long                           startedAt = timestamp(newYork, 2026, 5, 1, 0, 0, 0, 0);
    long                           from      = timestamp(newYork, 2026, 5, 21, 20, 29, 59, 0);
    long                           to        = timestamp(newYork, 2026, 5, 21, 20, 30, 1, 0);

    //
    //
    boolean needRun = checker.needRun(startedAt, from, to);
    //
    //

    assertThat(needRun).isTrue();
  }

  @Test
  public void needRun_shouldReturnFalseWhenUtcTimeMatchesButConfiguredTimeZoneDoesNot() {
    TimeZone                       newYork   = TimeZone.getTimeZone("America/New_York");
    RunChecker_FromHMS_ToHMS_Every checker   = new RunChecker_FromHMS_ToHMS_Every(newYork, 1, 0, 0, 3, 0, 0, 30 * 60 * 1000);
    long                           startedAt = timestamp(newYork, 2026, 5, 1, 0, 0, 0, 0);
    long                           from      = timestamp(UTC, 2026, 5, 22, 1, 29, 59, 0);
    long                           to        = timestamp(UTC, 2026, 5, 22, 1, 30, 1, 0);

    //
    //
    boolean needRun = checker.needRun(startedAt, from, to);
    //
    //

    assertThat(needRun).isFalse();
  }

  @Test
  public void needRun_shouldReturnFalseWhenDailyFromIsAfterDailyTo() {
    RunChecker_FromHMS_ToHMS_Every checker   = new RunChecker_FromHMS_ToHMS_Every(UTC, 12, 0, 0, 10, 0, 0, 30 * 60 * 1000);
    long                           startedAt = timestamp(UTC, 2026, 5, 1, 0, 0, 0, 0);
    long                           from      = timestamp(UTC, 2026, 5, 21, 10, 0, 0, 0);
    long                           to        = timestamp(UTC, 2026, 5, 22, 13, 0, 0, 0);

    //
    //
    boolean needRun = checker.needRun(startedAt, from, to);
    //
    //

    assertThat(needRun).isFalse();
  }

  @Test
  public void needRun_shouldReturnFalseWhenEveryMsIsNotPositive() {
    RunChecker_FromHMS_ToHMS_Every checker   = new RunChecker_FromHMS_ToHMS_Every(UTC, 10, 0, 0, 12, 0, 0, 0);
    long                           startedAt = timestamp(UTC, 2026, 5, 1, 0, 0, 0, 0);
    long                           from      = timestamp(UTC, 2026, 5, 21, 10, 0, 0, 0);
    long                           to        = timestamp(UTC, 2026, 5, 21, 10, 0, 1, 0);

    //
    //
    boolean needRun = checker.needRun(startedAt, from, to);
    //
    //

    assertThat(needRun).isFalse();
  }
}
