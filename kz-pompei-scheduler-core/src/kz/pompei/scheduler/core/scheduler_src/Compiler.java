package kz.pompei.scheduler.core.scheduler_src;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.function.Consumer;
import kz.pompei.hotconfig.core.model.ConfParam;
import kz.pompei.scheduler.core.annotation.Schedule;
import kz.pompei.scheduler.core.run_checker.RunChecker;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import static kz.pompei.scheduler.core.SchedulerUtil.trimStr;

/**
 * Class for compiling task schedules from configuration.
 */
public class Compiler {

  /**
   * Compiles tasks from the configuration. Only those tasks specified in taskNameFilter are used.
   *
   * @param params          list of configuration parameters
   * @param timeZoneDefault default time zone
   * @param taskNameFilter  list of tasks to compile
   * @return compilation result
   */
  public static @NonNull CompileResult compileAll(@NonNull List<ConfParam> params,
                                                  @NonNull TimeZone timeZoneDefault,
                                                  @NonNull Set<String> taskNameFilter,
                                                  @NonNull Consumer<Throwable> errorConsumer) {
    CompileResult result              = new CompileResult();
    TimeZone      currentTimeZone     = timeZoneDefault;
    String        currentExecutorName = null;

    for (ConfParam param : params) {

      String taskName = trimStr(param.name);

      if ("/TZ".equals(taskName)) {
        currentTimeZone = TimeZone.getTimeZone(trimStr(param.valueStr));
        continue;
      }

      if ("/EXECUTOR".equals(taskName)) {
        currentExecutorName = trimStr(param.valueStr);
        continue;
      }

      if (!(taskNameFilter.contains(taskName))) {
        continue;
      }

      ScheduleSrc scheduleSrc = ScheduleSrc.NEVER_RUN;
      String      error       = null;

      try {
        scheduleSrc = compile(param.valueStr, currentTimeZone, currentExecutorName);
      } catch (Throwable e) {

        errorConsumer.accept(e);

        List<String> errors = new ArrayList<>();

        Throwable current = e;

        while (current != null) {
          errors.add(current.getClass().getSimpleName() + ": " + String.join("\n  ", current.getMessage().split("\n")));
          current = current.getCause();
        }

        error = String.join("\n", errors);
      }

      result.taskName_to_scheduleSrc.put(taskName, new CompiledScheduleSrc(scheduleSrc, error));
    }

    return result;
  }

  /**
   * Compile schedulerTxt to ScheduleSrc. See {@link Schedule} how it is doing.
   *
   * @param schedulerTxt scheduler text to compile
   * @param timeZone     time zone to use for parsing
   * @param executorName executor name in what tasks will be executed
   * @return compiled ScheduleSrc
   * @throws SchedulerCompileErr if compilation fails
   */
  public static @NonNull ScheduleSrc compile(String schedulerTxt,
                                             @NonNull TimeZone timeZone,
                                             @Nullable String executorName) throws SchedulerCompileErr {
    if (schedulerTxt == null) {
      throw new SchedulerCompileErr("OA4yRsQnMr :: Schedule expression is null");
    }

    String source = schedulerTxt.trim();

    if (source.startsWith("#")) {
      return ScheduleSrc.NEVER_RUN;
    }

    boolean isParallel = false;
    String  executorName0 = executorName;

    while (true) {
      if (startsWithExecutorPrefix(source)) {
        int executorNameEnd = source.indexOf(')', "Exe(".length());
        if (executorNameEnd < 0) {
          throw new SchedulerCompileErr("u2GfTcAY1T :: Expected `)` after executor name");
        }

        executorName0 = source.substring("Exe(".length(), executorNameEnd).trim();
        source        = source.substring(executorNameEnd + 1).trim();
        continue;
      }

      String firstWord = firstWord(source).toLowerCase();

      //noinspection SpellCheckingInspection
      if (firstWord.startsWith("paral") || firstWord.startsWith("парал")) {
        isParallel = true;
        source     = source.substring(firstWord.length()).trim();
        continue;
      }

      break;
    }

    RunChecker runChecker   = new SchedulerTxtParser(source, timeZone).parse();
    boolean    isParallel0  = isParallel;
    String     executorName1 = executorName0;

    return new ScheduleSrc() {
      @Override public boolean needRun(long timestampStartedAt, long timestampFrom, long timestampTo) {
        return runChecker.needRun(timestampStartedAt, timestampFrom, timestampTo);
      }

      @Override public @Nullable String executorName() {
        return executorName1;
      }

      @Override public boolean isParallel() {
        return isParallel0;
      }
    };
  }

  private static boolean startsWithExecutorPrefix(@NonNull String source) {
    return source.length() >= "Exe(".length() && source.regionMatches(true, 0, "Exe(", 0, "Exe(".length());
  }

  private static @NonNull String firstWord(@NonNull String source) {
    for (int i = 0; i < source.length(); i++) {
      if (Character.isWhitespace(source.charAt(i))) {
        return source.substring(0, i);
      }
    }
    return source;
  }
}
