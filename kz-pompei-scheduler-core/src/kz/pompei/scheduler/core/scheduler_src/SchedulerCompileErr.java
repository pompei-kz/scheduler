package kz.pompei.scheduler.core.scheduler_src;

public class SchedulerCompileErr extends RuntimeException {

  public SchedulerCompileErr(String message) {
    super(message);
  }

  public SchedulerCompileErr(String message, Throwable cause) {
    super(message, cause);
  }
}
