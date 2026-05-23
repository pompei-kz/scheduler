package kz.pompei.scheduler.core.scheduler_src;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import kz.pompei.hotconfig.core.model.ConfParam;
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
   * <p>
   * Scheduler define by using several schedule expressions connected by operations. Operation is one of: + (union), * (intersection).
   * And you can use parentheses to group expressions.
   * <p>
   * A schedule expression produces a temporal set.
   * <p>
   * <h2>Temporal Sets</h2>
   * A temporal set is a set of time points.
   * <p>
   * Temporal sets can be divided into two categories:
   * <ul>
   *   <li>continuous temporal sets</li>
   *   <li>discrete temporal sets</li>
   * </ul>
   *
   * <h2>Continuous Temporal Set</h2>
   * <p>
   * A continuous temporal set contains at least one non-zero time interval.
   * In other words, there exists a finite time range that contains infinitely
   * many time points belonging to the set.
   * <p>
   * Examples:
   * <ul>
   *   <li>from 13:00 to 14:00</li>
   *   <li>every moment during Tuesday</li>
   *   <li>from 2026-05-01 10:00 to 2026-05-01 12:00</li>
   * </ul>
   * <p>
   * Example explanation:
   * <blockquote>
   * The interval from 13:00 to 14:00 is a continuous temporal set because
   * every instant inside that interval belongs to the set.
   * </blockquote>
   *
   * <h2>Discrete Temporal Set</h2>
   * <p>
   * A discrete temporal set consists only of isolated time points.
   * No finite interval exists in which all contained time points belong
   * to the set continuously.
   * <p>
   * Examples:
   * <ul>
   *   <li>every minute</li>
   *   <li>every 5 seconds</li>
   *   <li>every Monday at 09:00</li>
   *   <li>timestamps generated once per hour</li>
   * </ul>
   * <p>
   * Example explanation:
   * <blockquote>
   * "Every minute" is a discrete temporal set because it contains separate
   * isolated moments rather than continuous intervals.
   * </blockquote>
   *
   * <h2>Comparison</h2>
   *
   * <table border="1">
   *   <tr>
   *     <th>Temporal Set</th>
   *     <th>Type</th>
   *   </tr>
   *   <tr>
   *     <td>13:00-14:00</td>
   *     <td>continuous temporal set</td>
   *   </tr>
   *   <tr>
   *     <td>every minute</td>
   *     <td>discrete temporal set</td>
   *   </tr>
   *   <tr>
   *     <td>every Tuesday</td>
   *     <td>continuous temporal set</td>
   *   </tr>
   *   <tr>
   *     <td>every Tuesday at 10:00</td>
   *     <td>discrete temporal set</td>
   *   </tr>
   * </table>
   * <p>
   * A temporal set may therefore represent either continuous spans of time
   * or separate individual moments distributed over time.
   * <p>
   * Temporal sets can be combined using union and intersection operations.
   * <p>
   * The following abbreviations are used:
   * <ul>
   *   <li>CTS — continuous temporal set</li>
   *   <li>DTS — discrete temporal set</li>
   * </ul>
   *
   * <h2>Union</h2>
   * <p>
   * The union operation combines all-time points belonging to either
   * temporal set.
   * <p>
   * The plus sign denotes the union operation:
   * <pre>
   * +
   * </pre>
   * <p>
   * Example:
   * <pre>
   * [13:00-14:00] + every minute
   * </pre>
   * <p>
   * The resulting temporal set contains all-time points from both operands.
   *
   * <h2>Intersection</h2>
   * <p>
   * The intersection operation contains only time points that belong
   * to both temporal sets simultaneously.
   * <p>
   * The intersection operation is denoted by the multiplication sign:
   * <pre>
   * *
   * </pre>
   * <p>
   * Example:
   * <pre>
   * [13:00-14:00] * every minute
   * </pre>
   * <p>
   * The resulting temporal set contains only minute marks located
   * inside the interval from 13:00 to 14:00.
   *
   * <h2>Type Rules</h2>
   * <p>
   * The following rules are always true:
   *
   * <pre>
   * CTS + anyTS = CTS
   * DTS * anyTS = DTS
   * </pre>
   * <p>
   * where:
   * <ul>
   *   <li>CTS — continuous temporal set</li>
   *   <li>DTS — discrete temporal set</li>
   *   <li>anyTS — any temporal set</li>
   * </ul>
   *
   * <h3>Explanation</h3>
   *
   * <pre>
   * CTS + anyTS = CTS
   * </pre>
   * <p>
   * If at least one operand of the union operation is continuous,
   * the resulting temporal set is also continuous.
   * <p>
   * This is because the continuous operand already contains a non-zero
   * continuous interval.
   *
   * <pre>
   * DTS * anyTS = DTS
   * </pre>
   * <p>
   * If one operand of the intersection operation is discrete,
   * the resulting temporal set is also discrete.
   * <p>
   * This is because the intersection cannot introduce new continuous
   * intervals that were not present in the discrete operand.
   * <h2>Scheduler Expressions</h2>
   * To describe schedule expressions, the following designations are used:
   * <ul>
   *   <li>
   *     {@code hh} — hours of day from 0 to 23.
   *     It can be specified as a single number or two numbers.
   *
   *   <li>
   *     {@code MM} — minutes of an hour from 00 to 59.
   *     It can be specified as two numbers.
   *     Single number is not allowed.
   *
   *   <li>
   *     {@code SS} — seconds of a minute from 00 to 59.
   *     It can be specified as two numbers.
   *     Single number is not allowed.
   *
   *   <li>
   *     {@code Month} — name of a month. It can be:
   *
   *     <ul>
   *       <li>Full name in English:
   *         January, February, March, April, May, June,
   *         July, August, September, October, November, December.
   *
   *       <li>First three letters of a name in English:
   *         Jan, Feb, Mar, Apr, May, Jun,
   *         Jul, Aug, Sep, Oct, Nov, Dec.
   *
   *       <li>Full name in Russian:
   *         Январь, Февраль, Март, Апрель, Май, Июнь,
   *         Июль, Август, Сентябрь, Октябрь, Ноябрь, Декабрь.
   *
   *       <li>First three letters of a name in Russian:
   *         Янв, Фев, Мар, Апр, Май, Июн,
   *         Июл, Авг, Сен, Окт, Ноя, Дек.
   *     </ul>
   *
   *     <li>
   *       {@code YYYY} - year in four digits.
   *
   *       <li>
   *         {@code []} - square brackets indicate optional parts of the expression
   * </ul>
   *
   * @param schedulerTxt text to compile into ScheduleSrc
   * @param timeZone     time zone for time in schedulerTxt
   * @return compiled schedule model
   * @throws SchedulerCompileErr throws when compilation error or warning or notice
   */
  public static @NonNull ScheduleSrc compile(String schedulerTxt, @NonNull TimeZone timeZone) throws SchedulerCompileErr {
    throw new RuntimeException("2026-05-23 07:50 Created empty method body Compiler.compile()");
  }
}
