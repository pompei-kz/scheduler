package kz.pompei.scheduler.core.annotation;

/**
 * Interface for checking if a task should be run based on time range.
 */
public interface RunChecker {

  /**
   * Returns true if the task should be run based on the given time range.
   *
   * @param timestampStartedAt milliseconds from System.currentTimeMillis() - time of start scheduler
   * @param timestampMsFrom    milliseconds from System.currentTimeMillis() - time from
   * @param timestampMsTo      milliseconds from System.currentTimeMillis() - time to
   * @return true if the task should be run, false otherwise
   */
  boolean needRun(long timestampStartedAt, long timestampMsFrom, long timestampMsTo);
}
