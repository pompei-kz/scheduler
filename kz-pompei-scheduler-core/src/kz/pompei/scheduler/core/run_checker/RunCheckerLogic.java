package kz.pompei.scheduler.core.run_checker;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RunCheckerLogic implements RunChecker {
  private final @NonNull LogicOp    op;
  private final @NonNull RunChecker arg1;
  private final @NonNull RunChecker arg2;

  @Override public boolean needRun(long timestampStartedAt, long timestampMsFrom, long timestampMsTo) {

    boolean b1 = arg1.needRun(timestampStartedAt, timestampMsFrom, timestampMsTo);

    if (op == LogicOp.OR && b1) return true;
    if (op == LogicOp.AND && !b1) return false;

    return arg2.needRun(timestampStartedAt, timestampMsFrom, timestampMsTo);
  }
}
