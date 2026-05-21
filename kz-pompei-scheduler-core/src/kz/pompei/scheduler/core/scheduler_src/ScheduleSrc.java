package kz.pompei.scheduler.core.scheduler_src;

import org.jetbrains.annotations.Nullable;

/**
 * Source of information about scheduling for a task.
 */
public interface ScheduleSrc {

  /**
   * Check does this task must be run in the interval of time: timestampFrom <= time && time < timestampTo.
   * <p>
   * This interval is closed from left and open from right.
   *
   * @param timestampStartedAt beginning of the task execution
   * @param timestampFrom      previous checking time
   * @param timestampTo        current checking time
   * @return need the task to be run
   */
  boolean needRun(long timestampStartedAt, long timestampFrom, long timestampTo);

  /**
   * Executor name to be used to run a task.
   * <p>
   * If this method returns null or returns an unknown name, then the system will use the default executor.
   *
   * @return executor name
   */
  @Nullable String executorName();

  /**
   * Does this task can be run in parallel with self, while the previous run of this task is still running?
   *
   * @return true if this task can be run in parallel with itself, false otherwise
   */
  boolean isParallel();

  ScheduleSrc NEVER_RUN = new ScheduleSrc() {
    @Override public boolean needRun(long timestampStartedAt, long timestampFrom, long timestampTo) {
      return false;
    }

    @Override public @Nullable String executorName() {
      return null;
    }

    @Override public boolean isParallel() {
      return false;
    }
  };
}
