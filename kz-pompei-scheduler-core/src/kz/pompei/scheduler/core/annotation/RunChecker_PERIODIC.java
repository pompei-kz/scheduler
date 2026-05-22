package kz.pompei.scheduler.core.annotation;

import lombok.RequiredArgsConstructor;

/**
 * RunChecker_PERIODIC checks if a periodic task should be run based on the given timestamp range.
 */
@RequiredArgsConstructor
public class RunChecker_PERIODIC implements RunChecker {

  /**
   * Offset in milliseconds from `timestampStartedAt` to start run. The first run should be at `timestampStartedAt + offsetMs`.
   * <p>
   * The next runs should be at `timestampStartedAt + offsetMs + periodMs * n`, where `n` is a non-negative integer.
   */
  private final long offsetMs;

  /**
   * Period in milliseconds between runs.
   * The next run should be at `timestampStartedAt + offsetMs + periodMs * n`, where `n` is a non-negative integer.
   */
  private final long periodMs;

  /**
   *
   *
   * Let ts = timestampStartedAt + offsetMs + periodMs * n
   * <p>
   * Return true if timestampStartedAt + timestampMsFrom <= ts < timestampStartedAt + timestampMsTo
   * <p>
   * Otherwise false.
   * <p>
   * where `n` is a non-negative integer.
   *
   * @param timestampStartedAt milliseconds from System.currentTimeMillis() - time of start scheduler
   * @param timestampMsFrom    milliseconds from System.currentTimeMillis() - time from
   * @param timestampMsTo      milliseconds from System.currentTimeMillis() - time to
   * @return true if you need to run, false otherwise
   */
  @Override public boolean needRun(long timestampStartedAt, long timestampMsFrom, long timestampMsTo) {
    if (timestampMsTo <= timestampMsFrom || periodMs <= 0) {
      return false;
    }

    long firstTimestampMs = timestampStartedAt + offsetMs;
    if (timestampMsFrom <= firstTimestampMs) {
      return firstTimestampMs < timestampMsTo;
    }

    long deltaMs = timestampMsFrom - firstTimestampMs;
    long periods = deltaMs / periodMs;
    if (deltaMs % periodMs != 0) {
      periods++;
    }

    long timestampMs = firstTimestampMs + periodMs * periods;

    return timestampMs < timestampMsTo;
  }
}
