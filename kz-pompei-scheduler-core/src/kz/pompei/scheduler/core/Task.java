package kz.pompei.scheduler.core;

/**
 * Represents an api to run a task
 */
public interface Task {

  /**
   * Returns the task name.
   * <p>
   * If the task was created by running a class method, the name will contain the class name, a period, and the method name.
   *
   * @return the task name
   */
  String taskName();

  /**
   * Runs the task.
   *
   * @throws Throwable if an error occurs during task execution
   */
  void run() throws Throwable;

}
