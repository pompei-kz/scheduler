package kz.pompei.scheduler.core.scheduler_src;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import kz.pompei.hotconfig.core.model.ConfParam;
import kz.pompei.scheduler.core.annotation.Schedule;
import lombok.NonNull;

import static java.util.stream.Collectors.toList;

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
                                                  @NonNull Set<String> taskNameFilter) {
    CompileResult result          = new CompileResult();
    TimeZone      currentTimeZone = timeZoneDefault;

    for (ConfParam param : params) {

      String taskName = param.name;

      if ("/TZ".equals(taskName)) {
        currentTimeZone = TimeZone.getTimeZone(param.valueStr);
        continue;
      }

      if (!(taskNameFilter.contains(taskName))) {
        continue;
      }

      try {
        ScheduleSrc scheduleSrc = compile(param.valueStr, currentTimeZone);
        result.taskName_to_scheduleSrc.put(taskName, scheduleSrc);
      } catch (RuntimeException e) {
        //noinspection CallToPrintStackTrace
        e.printStackTrace();

        List<String> lines = Arrays.stream(e.getMessage().split("\n")).collect(toList(/*need modifiable list*/));

        String message = "ERR: " + param.name + "=" + param.valueStr + " => " + e.getClass().getSimpleName() + ": " + lines.removeFirst();

        result.noticeMessages.add(message);

        for (String line : lines) {
          result.noticeMessages.add("  " + line);
        }
      }
    }

    return result;
  }

  /**
   * Compile schedulerTxt to ScheduleSrc. See {@link Schedule} how it is doing.
   *
   * @param schedulerTxt scheduler text to compile
   * @param timeZone     time zone to use for parsing
   * @return compiled ScheduleSrc
   * @throws SchedulerCompileErr if compilation fails
   */
  public static @NonNull ScheduleSrc compile(String schedulerTxt, @NonNull TimeZone timeZone) throws SchedulerCompileErr {
    throw new RuntimeException("2026-05-23 07:50 Created empty method body Compiler.compile()");
  }
}
