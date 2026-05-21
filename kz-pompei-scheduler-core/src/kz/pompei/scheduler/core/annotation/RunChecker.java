package kz.pompei.scheduler.core.annotation;

public interface RunChecker {
  boolean needRun(long timestampStartedAt, long timestampFrom, long timestampTo);
}
