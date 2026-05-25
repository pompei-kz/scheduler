package kz.pompei.scheduler.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to describe the schedule for running a single task.
 * <h2>Scheduler</h2>
 * Scheduler define by using several schedule expressions connected by operations. Operation is one of: + (union), * (intersection).
 * <p>
 * And you can use parentheses to group expressions.
 * <p>
 * The precedence of these operations is the usual - (*) is stronger than (+),
 * i.e., the expression {@code A + B * C} is equivalent to the expression {@code A + (B*C)}
 * <p>
 * For example:
 * <p>
 * {@code (13:00 + 14:00) * (Monday + Tuesday)} - means every Monday and Tuesday at 13:00 and 14:00
 *
 * <h2>Schedule Text Prefixes</h2>
 * Leading and trailing spaces in the schedule text are ignored.
 * <p>
 * If the trimmed schedule text starts with {@code #}, the task never runs.
 * This can be used to disable a schedule without deleting its text.
 * <p>
 * If the trimmed schedule text starts with {@code Exe(executorName)}, then
 * {@code executorName} is used as the executor name for this task. The {@code Exe}
 * keyword is case-insensitive, and spaces around the executor name are ignored.
 * The schedule expression starts after the closing {@code )}.
 * <p>
 * For example:
 * <p>
 * {@code Exe(background-pool) 13:00}
 * <p>
 * If the first word of the remaining schedule text starts with {@code paral}
 * or {@code парал}, the task may run in parallel with itself. The marker word
 * is removed before parsing the schedule expression.
 * <p>
 * For example:
 * <p>
 * {@code parallel 13:00}
 * <p>
 * Prefixes can be combined in any order:
 * <p>
 * {@code Exe(background-pool) parallel 13:00}
 * <p>
 * {@code parallel Exe(background-pool) 13:00}
 *
 * <h2>Temporal Sets</h2>
 * A schedule expression produces a temporal set.
 * <p>
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
 *   <li>
 *     {@code D} — day of month from 1 to 31.
 *     It can be specified as a single number.
 *   <li>
 *     {@code Week} - a name of week.
 *     It can be specified as a single word.
 *     <ul>
 *       <li>Full name in English:
 *          Sunday, Monday, Tuesday, Wednesday, Thursday, Friday, Saturday
 *       <li>First three letters of a name in English:
 *         mon, tue, wed, thu, fri, sat, sun
 *       <li>Full name in Russian:
 *         Воскресенье, Понедельник, Вторник, Среда, Четверг, Пятница, Суббота
 *       <li>First three letters of a name in Russian:
 *         Вос, Пон, Вто, Сре, Чет, Пят, Суб
 *       <li>Or two letters:
 *         Вс, Пн, Вт, Ср, Чт, Пт, Сб
 *     </ul>
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
 *     <li>
 *       {@code []} - square brackets indicate optional parts of the expression
 *
 *     <li>
 *       {@code PERIOD} - describes periodicity of the expression. Consists of a positive integer followed by a time unit (e.g., '1 h', '2 d', '3 m').
 *       Time units:
 *
 *       <ul>
 *         <li>ms, millis, milliseconds, мс, миллисекунд... (с любым окончанием):
 *           Milliseconds
 *         <li>s, sec, seconds, с, сек, сек...(с любым окончанием):
 *           Seconds
 *         <li>m, min, minute, minutes, м, минут...(с любым окончанием):
 *           Minutes
 *         <li>р, hour, hours, ч, час...(с любым окончанием):
 *           Hours
 *         <li>d, day, days, д, дн, дня...(с любым окончанием):
 *           Days
 *         <li>month, months, мес, месяца...(с любым окончанием):
 *           Months
 *         <li>y, year, years, г, лет, года...(с любым окончанием):
 *           Years
 *       </ul>
 * </ul>
 *
 *  <table border="1">
 *    <tr>
 *      <th>Scheduler Expression</th>
 *      <th>Category of temporal set</th>
 *      <th>Description</th>
 *      <th>Implementation</th>
 *    </tr>
 *    <tr>
 *      <td>{@code hh:MM[:SS]}</td>
 *      <td>Discrete</td>
 *      <td>Describes a specific time of day. The task is selected when the checked time range contains that time on the current day.</td>
 *      <td>{@code RunChecker_HMS}</td>
 *    </tr>
 *    <tr>
 *      <td>{@code hh:MM[:SS] - hh:MM[:SS]}</td>
 *      <td>Continuous</td>
 *      <td>Describes a time interval inside a day. The task is selected when the checked time range intersects that interval.</td>
 *      <td>{@code RunChecker_FromHMS_ToHMS}</td>
 *    </tr>
 *    <tr>
 *      <td>{@code hh:MM[:SS] - hh:MM[:SS] every PERIOD}
 *      <br>
 *      {@code hh:MM[:SS] - hh:MM[:SS] кажд...(с слюбым окончанием) PERIOD}</td>
 *      <td>Discrete</td>
 *      <td>Describes periodic instants inside a daily time interval. The task is selected when the checked time range contains one of these instants.</td>
 *      <td>{@code RunChecker_FromHMS_ToHMS_Every}</td>
 *    </tr>
 *    <tr>
 *      <td>{@code day D}</td>
 *      <td>Continuous</td>
 *      <td>Describes a specific day of the month. The task is selected while the checked time range belongs to that day in the configured time zone.</td>
 *      <td>{@code RunChecker_DAY_OF_MONTH}</td>
 *    </tr>
 *    <tr>
 *      <td>{@code day-of-week}</td>
 *      <td>Continuous</td>
 *      <td>Describes a specific day of the week. The task is selected while the checked time range belongs to that day in the configured time zone.</td>
 *      <td>{@code RunChecker_DAY_OF_WEEK}</td>
 *    </tr>
 *    <tr>
 *      <td>{@code Month}</td>
 *      <td>Continuous</td>
 *      <td>Describes a specific month. The task is selected while the checked time range belongs to that month in the configured time zone.</td>
 *      <td>{@code RunChecker_MONTH}</td>
 *    </tr>
 *    <tr>
 *      <td>{@code YYYY year}
 *      <br>{@code YYYY год}
 *      <br>{@code YYYY г.}
 *      </td>
 *      <td>Continuous</td>
 *      <td>Describes a specific year. The task is selected while the checked time range belongs to that year in the configured time zone.</td>
 *      <td>{@code RunChecker_YEAR}</td>
 *    </tr>
 *    <tr>
 *      <td>{@code repeat every PERIOD}
 *      <br>{@code повторять каждые PERIOD}
 *      </td>
 *      <td>Discrete</td>
 *      <td>Describes periodic instants counted from scheduler start plus an offset.
 *      The task is selected when the checked time range contains one of these instants.</td>
 *      <td>{@code RunChecker_PERIODIC}</td>
 *    </tr>
 *    <tr>
 *      <td>{@code repeat every PERIOD starts with PERIOD}
 *      <br>{@code повторять каждые PERIOD начиная с PERIOD}
 *      </td>
 *      <td>Discrete</td>
 *      <td>Describes periodic instants counted from scheduler start plus the specified start offset.
 *      The first instant is at scheduler start plus the offset, and the next instants repeat after each period.</td>
 *      <td>{@code RunChecker_PERIODIC}</td>
 *    </tr>
 *  </table>
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Schedule {

  /**
   * Schedule expression
   *
   * @return Schedule expression
   */
  String value();
}
