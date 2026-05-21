package kz.pompei.scheduler.core;

import lombok.NonNull;

/**
 * A wrapper for running a task. The task itself is launched through it by calling the run() method.
 * It also contains a component for determining the task's start time.
 * This wrapper contains all the necessary API for running this task according to its schedule.
 */
public interface ScheduledTask {

  /**
   * Returns the source of the schedule for this task.
   *
   * @return the schedule source
   */
  @NonNull ScheduleSrc src();

  /**
   * Returns the task to run.
   *
   * @return the task
   */
  @NonNull Task task();
}
