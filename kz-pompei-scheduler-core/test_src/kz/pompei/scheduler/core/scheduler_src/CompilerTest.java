package kz.pompei.scheduler.core.scheduler_src;

import kz.pompei.scheduler.core.TestParent;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CompilerTest extends TestParent {

  private static final String currentExecutorName = "test-executor";

  @SuppressWarnings("SpellCheckingInspection")
  @DataProvider
  public Object[][] scheduleExpressionsFromJavadoc() {
    long mondayMay25StartedAt = timestamp(UTC, 2026, 5, 25, 0, 0, 0, 0);

    return new Object[][]{
      {
        "hh:MM",
        "13:00",
        mondayMay25StartedAt,
        timestamp(UTC, 2026, 5, 25, 13, 0, 0, 0),
        timestamp(UTC, 2026, 5, 25, 13, 0, 1, 0)
      },
      {
        "hh:MM:SS",
        "13:00:15",
        mondayMay25StartedAt,
        timestamp(UTC, 2026, 5, 25, 13, 0, 15, 0),
        timestamp(UTC, 2026, 5, 25, 13, 0, 16, 0)
      },
      {
        "hh:MM - hh:MM",
        "10:00 - 12:00",
        mondayMay25StartedAt,
        timestamp(UTC, 2026, 5, 25, 10, 30, 0, 0),
        timestamp(UTC, 2026, 5, 25, 10, 30, 1, 0)
      },
      {
        "hh:MM:SS - hh:MM:SS",
        "10:00:15 - 12:00:45",
        mondayMay25StartedAt,
        timestamp(UTC, 2026, 5, 25, 10, 30, 0, 0),
        timestamp(UTC, 2026, 5, 25, 10, 30, 1, 0)
      },
      {
        "hh:MM - hh:MM every PERIOD",
        "10:00 - 12:00 every 30 min",
        mondayMay25StartedAt,
        timestamp(UTC, 2026, 5, 25, 10, 30, 0, 0),
        timestamp(UTC, 2026, 5, 25, 10, 30, 1, 0)
      },
      {
        "hh:MM - hh:MM кажд... PERIOD",
        "10:00 - 12:00 каждую 30 минут",
        mondayMay25StartedAt,
        timestamp(UTC, 2026, 5, 25, 10, 30, 0, 0),
        timestamp(UTC, 2026, 5, 25, 10, 30, 1, 0)
      },
      {
        "day D",
        "day 25",
        mondayMay25StartedAt,
        timestamp(UTC, 2026, 5, 25, 10, 0, 0, 0),
        timestamp(UTC, 2026, 5, 25, 10, 0, 1, 0)
      },
      {
        "day-of-week",
        "Monday",
        mondayMay25StartedAt,
        timestamp(UTC, 2026, 5, 25, 10, 0, 0, 0),
        timestamp(UTC, 2026, 5, 25, 10, 0, 1, 0)
      },
      {
        "Month",
        "May",
        mondayMay25StartedAt,
        timestamp(UTC, 2026, 5, 25, 10, 0, 0, 0),
        timestamp(UTC, 2026, 5, 25, 10, 0, 1, 0)
      },
      {
        "YYYY year",
        "2026 year",
        mondayMay25StartedAt,
        timestamp(UTC, 2026, 5, 25, 10, 0, 0, 0),
        timestamp(UTC, 2026, 5, 25, 10, 0, 1, 0)
      },
      {
        "YYYY год",
        "2026 год",
        mondayMay25StartedAt,
        timestamp(UTC, 2026, 5, 25, 10, 0, 0, 0),
        timestamp(UTC, 2026, 5, 25, 10, 0, 1, 0)
      },
      {
        "YYYY г.",
        "2026 г.",
        mondayMay25StartedAt,
        timestamp(UTC, 2026, 5, 25, 10, 0, 0, 0),
        timestamp(UTC, 2026, 5, 25, 10, 0, 1, 0)
      },
      {
        "repeat every PERIOD",
        "repeat every 10 s",
        1_000L,
        11_000L,
        12_000L
      },
      {
        "повторять каждые PERIOD",
        "повторять каждые 10 секунд",
        1_000L,
        11_000L,
        12_000L
      },
      {
        "repeat every PERIOD starts with PERIOD",
        "repeat every 10 s starts with 5 s",
        1_000L,
        6_000L,
        7_000L
      },
      {
        "повторять каждые PERIOD начиная с PERIOD",
        "повторять каждые 10 секунд начиная с 5 секунд",
        1_000L,
        6_000L,
        7_000L
      }
    };
  }

  @Test(dataProvider = "scheduleExpressionsFromJavadoc")
  public void compile_shouldParseSchedulerExpressionsDescribedInJavadoc(String caseName,
                                                                        String expression,
                                                                        long startedAt,
                                                                        long from,
                                                                        long to) {
    ScheduleSrc schedule = Compiler.compile(expression, UTC, currentExecutorName);

    //
    //
    boolean needRun = schedule.needRun(startedAt, from, to);
    //
    //

    assertThat(needRun).as(caseName).isTrue();
  }

  @Test
  public void compile_shouldParseTimeAndDayOfWeekIntersection() {
    ScheduleSrc schedule = Compiler.compile("13:00 * Monday", UTC, currentExecutorName);
    long        started  = timestamp(UTC, 2026, 5, 25, 0, 0, 0, 0);
    long        from     = timestamp(UTC, 2026, 5, 25, 13, 0, 0, 0);
    long        to       = timestamp(UTC, 2026, 5, 25, 13, 0, 1, 0);

    //
    //
    boolean needRun = schedule.needRun(started, from, to);
    //
    //

    assertThat(needRun).isTrue();
  }

  @Test
  public void compile_shouldRespectOperatorPrecedenceAndParentheses() {
    ScheduleSrc withoutParentheses = Compiler.compile("13:00 + 14:00 * Tuesday", UTC, currentExecutorName);
    ScheduleSrc withParentheses    = Compiler.compile("(13:00 + 14:00) * Tuesday", UTC, currentExecutorName);
    long        started            = timestamp(UTC, 2026, 5, 25, 0, 0, 0, 0);
    long        from               = timestamp(UTC, 2026, 5, 25, 13, 0, 0, 0);
    long        to                 = timestamp(UTC, 2026, 5, 25, 13, 0, 1, 0);

    //
    //
    boolean needRunWithoutParentheses = withoutParentheses.needRun(started, from, to);
    boolean needRunWithParentheses    = withParentheses.needRun(started, from, to);
    //
    //

    assertThat(needRunWithoutParentheses).isTrue();
    assertThat(needRunWithParentheses).isFalse();
  }

  @Test
  public void compile_shouldParseTimeRangeWithEveryPeriod() {
    ScheduleSrc schedule = Compiler.compile("10:00 - 12:00 every 30 min", UTC, currentExecutorName);
    long        started  = timestamp(UTC, 2026, 5, 25, 0, 0, 0, 0);
    long        from     = timestamp(UTC, 2026, 5, 25, 10, 30, 0, 0);
    long        to       = timestamp(UTC, 2026, 5, 25, 10, 30, 1, 0);

    //
    //
    boolean needRun = schedule.needRun(started, from, to);
    //
    //

    assertThat(needRun).isTrue();
  }

  @Test
  public void compile_shouldParseRepeatEveryWithStartOffset() {
    ScheduleSrc schedule = Compiler.compile("repeat every 10 s starts with 5 s", UTC, currentExecutorName);
    long        started  = 1_000;
    long        from     = 6_000;
    long        to       = 7_000;

    //
    //
    boolean needRun = schedule.needRun(started, from, to);
    //
    //

    assertThat(needRun).isTrue();
  }

  @Test
  public void compile_shouldParseRussianAliases() {
    ScheduleSrc schedule = Compiler.compile("10:00 - 11:00 каждую 15 минут * Пн * Май * 2026 год", UTC, currentExecutorName);
    long        started  = timestamp(UTC, 2026, 5, 25, 0, 0, 0, 0);
    long        from     = timestamp(UTC, 2026, 5, 25, 10, 15, 0, 0);
    long        to       = timestamp(UTC, 2026, 5, 25, 10, 15, 1, 0);

    //
    //
    boolean needRun = schedule.needRun(started, from, to);
    //
    //

    assertThat(needRun).isTrue();
  }

  @Test
  public void compile_shouldThrowSchedulerCompileErrForUnknownExpression() {
    assertThatThrownBy(() -> Compiler.compile("unknown", UTC, currentExecutorName)).isInstanceOf(SchedulerCompileErr.class)
                                                                                   .hasMessageContaining("Expected schedule expression");
  }

  @Test
  public void compile_shouldReturnNeverRunWhenTrimmedExpressionStartsWithHash() {
    ScheduleSrc schedule = Compiler.compile("  # 13:00", UTC, currentExecutorName);

    //
    //
    boolean needRun = schedule.needRun(
      timestamp(UTC, 2026, 5, 25, 0, 0, 0, 0),
      timestamp(UTC, 2026, 5, 25, 13, 0, 0, 0),
      timestamp(UTC, 2026, 5, 25, 13, 0, 1, 0)
    );
    //
    //

    assertThat(schedule).isSameAs(ScheduleSrc.NEVER_RUN);
    assertThat(needRun).isFalse();
    assertThat(schedule.isParallel()).isFalse();
  }

  @Test
  public void compile_shouldTrimExpressionBeforeParsing() {
    ScheduleSrc schedule = Compiler.compile("  13:00  ", UTC, currentExecutorName);
    long        started  = timestamp(UTC, 2026, 5, 25, 0, 0, 0, 0);
    long        from     = timestamp(UTC, 2026, 5, 25, 13, 0, 0, 0);
    long        to       = timestamp(UTC, 2026, 5, 25, 13, 0, 1, 0);

    //
    //
    boolean needRun = schedule.needRun(started, from, to);
    //
    //

    assertThat(needRun).isTrue();
  }

  @DataProvider
  public Object[][] executorScheduleExpressions() {
    return new Object[][]{
      {"Exe(custom-executor) 13:00", "custom-executor"},
      {"exe(custom-executor) 13:00", "custom-executor"},
      {"EXE(custom-executor) 13:00", "custom-executor"},
      {"  ExE(  custom-executor  ) 13:00  ", "custom-executor"}
    };
  }

  @Test(dataProvider = "executorScheduleExpressions")
  public void compile_shouldUseExecutorNameFromExePrefix(String expression, String expectedExecutorName) {
    ScheduleSrc schedule = Compiler.compile(expression, UTC, currentExecutorName);
    long        started  = timestamp(UTC, 2026, 5, 25, 0, 0, 0, 0);
    long        from     = timestamp(UTC, 2026, 5, 25, 13, 0, 0, 0);
    long        to       = timestamp(UTC, 2026, 5, 25, 13, 0, 1, 0);

    //
    //
    boolean needRun = schedule.needRun(started, from, to);
    //
    //

    assertThat(schedule.executorName()).isEqualTo(expectedExecutorName);
    assertThat(needRun).isTrue();
  }

  @Test
  public void compile_shouldUsePassedExecutorNameWhenExePrefixIsAbsent() {
    ScheduleSrc schedule = Compiler.compile("13:00", UTC, currentExecutorName);

    assertThat(schedule.executorName()).isEqualTo(currentExecutorName);
  }

  @Test
  public void compile_shouldThrowSchedulerCompileErrWhenExePrefixHasNoClosingParenthesis() {
    assertThatThrownBy(() -> Compiler.compile("Exe(custom-executor 13:00", UTC, currentExecutorName)).isInstanceOf(SchedulerCompileErr.class)
                                                                                                      .hasMessageContaining("Expected `)` after executor name");
  }

  @DataProvider
  public Object[][] combinedPrefixScheduleExpressions() {
    return new Object[][]{
      {"Exe(custom-executor) parallel 13:00"},
      {"parallel Exe(custom-executor) 13:00"},
      {"  параллельно ExE(  custom-executor  ) 13:00  "}
    };
  }

  @Test(dataProvider = "combinedPrefixScheduleExpressions")
  public void compile_shouldUseExecutorAndParallelPrefixesInAnyOrder(String expression) {
    ScheduleSrc schedule = Compiler.compile(expression, UTC, currentExecutorName);
    long        started  = timestamp(UTC, 2026, 5, 25, 0, 0, 0, 0);
    long        from     = timestamp(UTC, 2026, 5, 25, 13, 0, 0, 0);
    long        to       = timestamp(UTC, 2026, 5, 25, 13, 0, 1, 0);

    //
    //
    boolean needRun = schedule.needRun(started, from, to);
    //
    //

    assertThat(schedule.executorName()).isEqualTo("custom-executor");
    assertThat(schedule.isParallel()).isTrue();
    assertThat(needRun).isTrue();
  }

  @SuppressWarnings("SpellCheckingInspection") @DataProvider
  public Object[][] parallelScheduleExpressions() {
    return new Object[][]{
      {"parallel 13:00"},
      {"paral 13:00"},
      {"  parallel 13:00  "},
      {"Параллельно 13:00"},
      {"парал 13:00"}
    };
  }

  @Test(dataProvider = "parallelScheduleExpressions")
  public void compile_shouldMarkScheduleAsParallelWhenTrimmedExpressionStartsWithParallelWord(String expression) {
    ScheduleSrc schedule = Compiler.compile(expression, UTC, currentExecutorName);
    long        started  = timestamp(UTC, 2026, 5, 25, 0, 0, 0, 0);
    long        from     = timestamp(UTC, 2026, 5, 25, 13, 0, 0, 0);
    long        to       = timestamp(UTC, 2026, 5, 25, 13, 0, 1, 0);

    //
    //
    boolean needRun = schedule.needRun(started, from, to);
    //
    //

    assertThat(schedule.isParallel()).isTrue();
    assertThat(needRun).isTrue();
  }

  @Test
  public void compile_shouldNotMarkScheduleAsParallelWithoutParallelPrefix() {
    ScheduleSrc schedule = Compiler.compile("13:00", UTC, currentExecutorName);

    assertThat(schedule.isParallel()).isFalse();
  }
}
