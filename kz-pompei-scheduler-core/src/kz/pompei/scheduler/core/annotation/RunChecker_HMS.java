package kz.pompei.scheduler.core.annotation;

import java.util.TimeZone;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RunChecker_HMS implements RunChecker {

  private final int hour, minus, second;
  private final @NonNull TimeZone timeZone;

  @Override public boolean needRun(long timestampStartedAt, long timestampFrom, long timestampTo) {
    throw new RuntimeException("dOK428XrLM :: Not impl yet RunChecker_HMS.needRun()");
  }
}
