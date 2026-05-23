package kz.pompei.scheduler.core.run_checker;

import kz.pompei.scheduler.core.TestParent;
import org.testng.annotations.Test;

import static kz.pompei.scheduler.core.run_checker.LogicOp.AND;
import static kz.pompei.scheduler.core.run_checker.LogicOp.OR;
import static org.assertj.core.api.Assertions.assertThat;

public class RunCheckerLogic_Test extends TestParent {

  @Test
  public void needRun_shouldReturnTrueForAndWhenBothArgumentsReturnTrue() {
    RecordingRunChecker arg1    = new RecordingRunChecker(true);
    RecordingRunChecker arg2    = new RecordingRunChecker(true);
    RunCheckerLogic     checker = new RunCheckerLogic(AND, arg1, arg2);

    //
    //
    boolean needRun = checker.needRun(1, 2, 3);
    //
    //

    assertThat(needRun).isTrue();
    assertThat(arg1.calls).isEqualTo(1);
    assertThat(arg2.calls).isEqualTo(1);
  }

  @Test
  public void needRun_shouldReturnFalseForAndWhenFirstArgumentReturnsTrueAndSecondReturnsFalse() {
    RecordingRunChecker arg1    = new RecordingRunChecker(true);
    RecordingRunChecker arg2    = new RecordingRunChecker(false);
    RunCheckerLogic     checker = new RunCheckerLogic(AND, arg1, arg2);

    //
    //
    boolean needRun = checker.needRun(1, 2, 3);
    //
    //

    assertThat(needRun).isFalse();
    assertThat(arg1.calls).isEqualTo(1);
    assertThat(arg2.calls).isEqualTo(1);
  }

  @Test
  public void needRun_shouldReturnFalseForAndWhenFirstArgumentReturnsFalseAndSecondReturnsTrue() {
    RecordingRunChecker arg1    = new RecordingRunChecker(false);
    RecordingRunChecker arg2    = new RecordingRunChecker(true);
    RunCheckerLogic     checker = new RunCheckerLogic(AND, arg1, arg2);

    //
    //
    boolean needRun = checker.needRun(1, 2, 3);
    //
    //

    assertThat(needRun).isFalse();
    assertThat(arg1.calls).isEqualTo(1);
    assertThat(arg2.calls).isZero();
  }

  @Test
  public void needRun_shouldReturnFalseForAndWhenBothArgumentsReturnFalse() {
    RecordingRunChecker arg1    = new RecordingRunChecker(false);
    RecordingRunChecker arg2    = new RecordingRunChecker(false);
    RunCheckerLogic     checker = new RunCheckerLogic(AND, arg1, arg2);

    //
    //
    boolean needRun = checker.needRun(1, 2, 3);
    //
    //

    assertThat(needRun).isFalse();
    assertThat(arg1.calls).isEqualTo(1);
    assertThat(arg2.calls).isZero();
  }

  @Test
  public void needRun_shouldReturnTrueForOrWhenBothArgumentsReturnTrue() {
    RecordingRunChecker arg1    = new RecordingRunChecker(true);
    RecordingRunChecker arg2    = new RecordingRunChecker(true);
    RunCheckerLogic     checker = new RunCheckerLogic(OR, arg1, arg2);

    //
    //
    boolean needRun = checker.needRun(1, 2, 3);
    //
    //

    assertThat(needRun).isTrue();
    assertThat(arg1.calls).isEqualTo(1);
    assertThat(arg2.calls).isZero();
  }

  @Test
  public void needRun_shouldReturnTrueForOrWhenFirstArgumentReturnsTrueAndSecondReturnsFalse() {
    RecordingRunChecker arg1    = new RecordingRunChecker(true);
    RecordingRunChecker arg2    = new RecordingRunChecker(false);
    RunCheckerLogic     checker = new RunCheckerLogic(OR, arg1, arg2);

    //
    //
    boolean needRun = checker.needRun(1, 2, 3);
    //
    //

    assertThat(needRun).isTrue();
    assertThat(arg1.calls).isEqualTo(1);
    assertThat(arg2.calls).isZero();
  }

  @Test
  public void needRun_shouldReturnTrueForOrWhenFirstArgumentReturnsFalseAndSecondReturnsTrue() {
    RecordingRunChecker arg1    = new RecordingRunChecker(false);
    RecordingRunChecker arg2    = new RecordingRunChecker(true);
    RunCheckerLogic     checker = new RunCheckerLogic(OR, arg1, arg2);

    //
    //
    boolean needRun = checker.needRun(1, 2, 3);
    //
    //

    assertThat(needRun).isTrue();
    assertThat(arg1.calls).isEqualTo(1);
    assertThat(arg2.calls).isEqualTo(1);
  }

  @Test
  public void needRun_shouldReturnFalseForOrWhenBothArgumentsReturnFalse() {
    RecordingRunChecker arg1    = new RecordingRunChecker(false);
    RecordingRunChecker arg2    = new RecordingRunChecker(false);
    RunCheckerLogic     checker = new RunCheckerLogic(OR, arg1, arg2);

    //
    //
    boolean needRun = checker.needRun(1, 2, 3);
    //
    //

    assertThat(needRun).isFalse();
    assertThat(arg1.calls).isEqualTo(1);
    assertThat(arg2.calls).isEqualTo(1);
  }

  @Test
  public void needRun_shouldPassTimestampsToArguments() {
    RecordingRunChecker arg1    = new RecordingRunChecker(true);
    RecordingRunChecker arg2    = new RecordingRunChecker(true);
    RunCheckerLogic     checker = new RunCheckerLogic(AND, arg1, arg2);

    //
    //
    checker.needRun(11, 22, 33);
    //
    //

    assertThat(arg1.timestampStartedAt).isEqualTo(11);
    assertThat(arg1.timestampMsFrom).isEqualTo(22);
    assertThat(arg1.timestampMsTo).isEqualTo(33);
    assertThat(arg2.timestampStartedAt).isEqualTo(11);
    assertThat(arg2.timestampMsFrom).isEqualTo(22);
    assertThat(arg2.timestampMsTo).isEqualTo(33);
  }

  private static class RecordingRunChecker implements RunChecker {

    private final boolean result;

    private int  calls = 0;
    private long timestampStartedAt;
    private long timestampMsFrom;
    private long timestampMsTo;

    private RecordingRunChecker(boolean result) {
      this.result = result;
    }

    @Override public boolean needRun(long timestampStartedAt, long timestampMsFrom, long timestampMsTo) {
      calls++;
      this.timestampStartedAt = timestampStartedAt;
      this.timestampMsFrom    = timestampMsFrom;
      this.timestampMsTo      = timestampMsTo;
      return result;
    }
  }
}
