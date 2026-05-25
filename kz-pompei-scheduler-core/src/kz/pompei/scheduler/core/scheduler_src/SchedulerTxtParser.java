package kz.pompei.scheduler.core.scheduler_src;

import java.util.TimeZone;
import kz.pompei.scheduler.core.run_checker.DayOfWeek;
import kz.pompei.scheduler.core.run_checker.LogicOp;
import kz.pompei.scheduler.core.run_checker.RunChecker;
import kz.pompei.scheduler.core.run_checker.RunCheckerLogic;
import kz.pompei.scheduler.core.run_checker.RunChecker_DAY_OF_MONTH;
import kz.pompei.scheduler.core.run_checker.RunChecker_DAY_OF_WEEK;
import kz.pompei.scheduler.core.run_checker.RunChecker_FromHMS_ToHMS;
import kz.pompei.scheduler.core.run_checker.RunChecker_FromHMS_ToHMS_Every;
import kz.pompei.scheduler.core.run_checker.RunChecker_HMS;
import kz.pompei.scheduler.core.run_checker.RunChecker_MONTH;
import kz.pompei.scheduler.core.run_checker.RunChecker_PERIODIC;
import kz.pompei.scheduler.core.run_checker.RunChecker_YEAR;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

class SchedulerTxtParser {

  private final @NonNull String   source;
  private final @NonNull String   sourceLower;
  private final @NonNull TimeZone timeZone;
  private                int      pos;

  SchedulerTxtParser(@NonNull String source, @NonNull TimeZone timeZone) {
    this.source      = source;
    this.sourceLower = source.toLowerCase();
    this.timeZone    = timeZone;
  }

  @NonNull RunChecker parse() {
    RunChecker ret = parseUnion();
    skipSpaces();
    if (!isEnd()) {
      throw err("CydNvK9nlR :: Unexpected text");
    }
    return ret;
  }

  private @NonNull RunChecker parseUnion() {
    RunChecker ret = parseIntersection();

    while (true) {
      skipSpaces();
      if (!take('+')) {
        return ret;
      }
      ret = new RunCheckerLogic(LogicOp.OR, ret, parseIntersection());
    }
  }

  private @NonNull RunChecker parseIntersection() {
    RunChecker ret = parsePrimary();

    while (true) {
      skipSpaces();
      if (!take('*')) {
        return ret;
      }
      ret = new RunCheckerLogic(LogicOp.AND, ret, parsePrimary());
    }
  }

  private @NonNull RunChecker parsePrimary() {
    skipSpaces();

    if (take('(')) {
      RunChecker ret = parseUnion();
      skipSpaces();
      expect(')');
      return ret;
    }

    {
      RunChecker ret = parseRepeat();
      if (ret != null) return ret;
    }

    {
      RunChecker ret = parseTimeExpression();
      if (ret != null) return ret;
    }

    {
      RunChecker ret = parseDayOfMonth();
      if (ret != null) return ret;
    }

    {
      RunChecker ret = parseYear();
      if (ret != null) return ret;
    }

    {
      RunChecker ret = parseDayOfWeek();
      if (ret != null) return ret;
    }

    {
      RunChecker ret = parseMonth();
      if (ret != null) return ret;
    }

    throw err("uUq6GO2TCc :: Expected schedule expression");
  }

  private @Nullable RunChecker parseRepeat() {
    int start = pos;
    if (!(takeWord("repeat") || takeWord("повторять"))) {
      pos = start;
      return null;
    }

    skipSpaces();
    if (!(takeWord("every") || takeRussianEvery())) {
      pos = start;
      return null;
    }

    long periodMs = parsePeriodMs();
    long offsetMs = 0;

    skipSpaces();
    int beforeStartsWith = pos;
    if (takeWords("starts", "with") || takeWords("начиная", "с")) {
      offsetMs = parsePeriodMs();
    } else {
      pos = beforeStartsWith;
    }

    return new RunChecker_PERIODIC(offsetMs, periodMs);
  }

  private @Nullable RunChecker parseTimeExpression() {
    int start = pos;
    Hms from  = parseHms();
    if (from == null) {
      pos = start;
      return null;
    }

    skipSpaces();
    if (!take('-')) {
      return new RunChecker_HMS(from.hour, from.minute, from.second, timeZone);
    }

    Hms to = parseHmsRequired();
    skipSpaces();

    if (takeWord("every") || takeRussianEvery()) {
      long everyMs = parsePeriodMs();
      return new RunChecker_FromHMS_ToHMS_Every(timeZone,
                                                from.hour,
                                                from.minute,
                                                from.second,
                                                to.hour,
                                                to.minute,
                                                to.second,
                                                everyMs);
    }

    return new RunChecker_FromHMS_ToHMS(timeZone, from.hour, from.minute, from.second, to.hour, to.minute, to.second);
  }

  private @Nullable RunChecker parseDayOfMonth() {
    int start = pos;
    if (!(takeWord("day") || takeWord("день"))) {
      pos = start;
      return null;
    }

    int day = parseIntRequired("YIt9thQ1z5 :: Expected day of month after `day`");
    if (day < 1 || day > 31) {
      throw err("axDSVArTnG :: Day of month must be from 1 to 31");
    }
    return new RunChecker_DAY_OF_MONTH(day, timeZone);
  }

  private @Nullable RunChecker parseYear() {
    int     start = pos;
    Integer year  = parseUnsignedInt();
    if (year == null || year < 1000 || year > 9999) {
      pos = start;
      return null;
    }

    skipSpaces();
    if (!(takeWord("year") || takeWord("years") || takeWord("год") || takeWord("года") || takeWord("лет") || takeWord("г."))) {
      pos = start;
      return null;
    }

    return new RunChecker_YEAR(year, timeZone);
  }

  private @Nullable RunChecker parseDayOfWeek() {
    for (DayWord dayWord : DAY_WORDS) {
      if (takeWord(dayWord.word)) {
        return new RunChecker_DAY_OF_WEEK(dayWord.dayOfWeek, timeZone);
      }
    }
    return null;
  }

  private @Nullable RunChecker parseMonth() {
    for (MonthWord monthWord : MONTH_WORDS) {
      if (takeWord(monthWord.word)) {
        return new RunChecker_MONTH(monthWord.month, timeZone);
      }
    }
    return null;
  }

  private @Nullable Hms parseHms() {
    int     start = pos;
    Integer hour  = parseUnsignedInt();
    if (hour == null) {
      pos = start;
      return null;
    }
    if (hour < 0 || hour > 23) {
      pos = start;
      return null;
    }
    if (!take(':')) {
      pos = start;
      return null;
    }
    int minute = parseFixed2Int("minute");
    if (minute < 0 || minute > 59) {
      throw err("j6WlO9UPp6 :: Minute must be from 00 to 59");
    }

    int second = 0;
    if (take(':')) {
      second = parseFixed2Int("second");
      if (second < 0 || second > 59) {
        throw err("JS1A6aIh4J :: Second must be from 00 to 59");
      }
    }

    return new Hms(hour, minute, second);
  }

  private @NonNull Hms parseHmsRequired() {
    Hms ret = parseHms();
    if (ret == null) {
      throw err("IhzD8dl1hv :: Expected time in hh:MM[:SS] format");
    }
    return ret;
  }

  private long parsePeriodMs() {
    skipSpaces();
    int  start = pos;
    long value = parseLongRequired("FsaAVpeASo :: Expected period value");
    if (value <= 0) {
      throw errAt(start, "pGjkLV1dLw :: Period value must be positive");
    }

    skipSpaces();
    String unit = parseWordRequired("d9rUb0YBCg :: Expected period time unit").toLowerCase();

    long multiplier = periodMultiplierMs(unit);
    if (multiplier <= 0) {
      throw err("44iJ1Nmh5d :: Unknown period time unit: " + unit);
    }

    try {
      return Math.multiplyExact(value, multiplier);
    } catch (ArithmeticException e) {
      throw new SchedulerCompileErr("Period is too large: " + value + " " + unit, e);
    }
  }

  private long periodMultiplierMs(String unit) {
    if (unit.equals("ms") || unit.equals("millis") || unit.equals("millisecond") || unit.equals("milliseconds") || unit.equals("мс") ||
      unit.startsWith("миллисекунд")) {
      return 1;
    }
    if (unit.equals("s") || unit.equals("sec") || unit.equals("second") || unit.equals("seconds") || unit.equals("с") || unit.equals("сек") ||
      unit.startsWith("секунд")) {
      return 1_000L;
    }
    if (unit.equals("m") || unit.equals("min") || unit.equals("minute") || unit.equals("minutes") || unit.equals("м") ||
      unit.startsWith("минут")) {
      return 60_000L;
    }
    if (unit.equals("h") || unit.equals("hour") || unit.equals("hours") || unit.equals("ч") || unit.startsWith("час")) {
      return 3_600_000L;
    }
    if (unit.equals("d") || unit.equals("day") || unit.equals("days") || unit.equals("д") || unit.equals("дн") || unit.equals("дня") ||
      unit.startsWith("день") || unit.startsWith("дней")) {
      return 86_400_000L;
    }
    if (unit.equals("month") || unit.equals("months") || unit.equals("мес") || unit.startsWith("месяц")) {
      return 30L * 86_400_000L;
    }
    if (unit.equals("y") || unit.equals("year") || unit.equals("years") || unit.equals("г") || unit.equals("лет") || unit.equals("года")) {
      return 365L * 86_400_000L;
    }
    return -1;
  }

  private boolean takeRussianEvery() {
    skipSpaces();
    int    start = pos;
    String word  = parseWord();
    //noinspection SpellCheckingInspection
    if (word != null && word.toLowerCase().startsWith("кажд")) {
      return true;
    }
    pos = start;
    return false;
  }

  private boolean takeWords(String first, String second) {
    int start = pos;
    if (takeWord(first) && takeWord(second)) {
      return true;
    }
    pos = start;
    return false;
  }

  private boolean takeWord(String expected) {
    skipSpaces();
    int end = pos + expected.length();
    if (end > source.length()) {
      return false;
    }
    if (!sourceLower.regionMatches(pos, expected.toLowerCase(), 0, expected.length())) {
      return false;
    }
    if (isWordCharBefore(pos) || isWordCharAt(end)) {
      return false;
    }
    pos = end;
    return true;
  }

  private @Nullable String parseWord() {
    skipSpaces();
    int start = pos;
    while (!isEnd() && (Character.isLetter(source.charAt(pos)) || source.charAt(pos) == '.')) {
      pos++;
    }
    if (start == pos) {
      return null;
    }
    return source.substring(start, pos);
  }

  @SuppressWarnings("SameParameterValue")
  private @NonNull String parseWordRequired(String message) {
    String ret = parseWord();
    if (ret == null) {
      throw err(message);
    }
    return ret;
  }

  private @Nullable Integer parseUnsignedInt() {
    skipSpaces();
    int start = pos;
    int ret   = 0;
    while (!isEnd() && Character.isDigit(source.charAt(pos))) {
      ret = ret * 10 + source.charAt(pos) - '0';
      pos++;
    }
    if (start == pos) {
      return null;
    }
    return ret;
  }

  @SuppressWarnings("SameParameterValue")
  private int parseIntRequired(String message) {
    Integer ret = parseUnsignedInt();
    if (ret == null) {
      throw err(message);
    }
    return ret;
  }

  @SuppressWarnings("SameParameterValue")
  private long parseLongRequired(String message) {
    skipSpaces();
    int  start = pos;
    long ret   = 0;
    while (!isEnd() && Character.isDigit(source.charAt(pos))) {
      ret = ret * 10 + source.charAt(pos) - '0';
      pos++;
    }
    if (start == pos) {
      throw err(message);
    }
    return ret;
  }

  private int parseFixed2Int(String name) {
    if (pos + 2 > source.length() || !Character.isDigit(source.charAt(pos)) || !Character.isDigit(source.charAt(pos + 1))) {
      throw err("uWiC2NPbvA :: Expected two digits for " + name);
    }
    int ret = (source.charAt(pos) - '0') * 10 + source.charAt(pos + 1) - '0';
    pos += 2;
    return ret;
  }

  private boolean take(char c) {
    skipSpaces();
    if (!isEnd() && source.charAt(pos) == c) {
      pos++;
      return true;
    }
    return false;
  }

  @SuppressWarnings("SameParameterValue")
  private void expect(char c) {
    if (!take(c)) {
      throw err("dfS8E6hWRE :: Expected `" + c + "`");
    }
  }

  private void skipSpaces() {
    while (!isEnd() && Character.isWhitespace(source.charAt(pos))) {
      pos++;
    }
  }

  @SuppressWarnings("BooleanMethodIsAlwaysInverted")
  private boolean isEnd() {
    return pos >= source.length();
  }

  private boolean isWordCharBefore(int index) {
    return index > 0 && isWordCharAt(index - 1);
  }

  private boolean isWordCharAt(int index) {
    return index >= 0 && index < source.length() && (Character.isLetterOrDigit(source.charAt(index)) || source.charAt(index) == '_');
  }

  private @NonNull SchedulerCompileErr err(String message) {
    return errAt(pos, message);
  }

  private @NonNull SchedulerCompileErr errAt(int errPos, String message) {
    return new SchedulerCompileErr(message + "\n" + source + "\n" + " ".repeat(Math.max(0, errPos)) + "^");
  }

  private record Hms(int hour, int minute, int second) {}

  private record DayWord(@NonNull String word, @NonNull DayOfWeek dayOfWeek) {}

  private record MonthWord(@NonNull String word, int month) {}

  private static final DayWord[] DAY_WORDS = {
    new DayWord("Sunday".toLowerCase(), DayOfWeek.SUNDAY),
    new DayWord("sun", DayOfWeek.SUNDAY),
    new DayWord("воскресенье", DayOfWeek.SUNDAY),
    new DayWord("вос", DayOfWeek.SUNDAY),
    new DayWord("вс", DayOfWeek.SUNDAY),
    new DayWord("Monday".toLowerCase(), DayOfWeek.MONDAY),
    new DayWord("mon", DayOfWeek.MONDAY),
    new DayWord("понедельник", DayOfWeek.MONDAY),
    new DayWord("пон", DayOfWeek.MONDAY),
    new DayWord("пн", DayOfWeek.MONDAY),
    new DayWord("Tuesday".toLowerCase(), DayOfWeek.TUESDAY),
    new DayWord("tue", DayOfWeek.TUESDAY),
    new DayWord("вторник", DayOfWeek.TUESDAY),
    new DayWord("вто", DayOfWeek.TUESDAY),
    new DayWord("вт", DayOfWeek.TUESDAY),
    new DayWord("Wednesday".toLowerCase(), DayOfWeek.WEDNESDAY),
    new DayWord("wed", DayOfWeek.WEDNESDAY),
    new DayWord("среда", DayOfWeek.WEDNESDAY),
    new DayWord("сре", DayOfWeek.WEDNESDAY),
    new DayWord("ср", DayOfWeek.WEDNESDAY),
    new DayWord("Thursday".toLowerCase(), DayOfWeek.THURSDAY),
    new DayWord("thu", DayOfWeek.THURSDAY),
    new DayWord("четверг", DayOfWeek.THURSDAY),
    new DayWord("чет", DayOfWeek.THURSDAY),
    new DayWord("чт", DayOfWeek.THURSDAY),
    new DayWord("Friday".toLowerCase(), DayOfWeek.FRIDAY),
    new DayWord("fri", DayOfWeek.FRIDAY),
    new DayWord("пятница", DayOfWeek.FRIDAY),
    new DayWord("пят", DayOfWeek.FRIDAY),
    new DayWord("пт", DayOfWeek.FRIDAY),
    new DayWord("Saturday".toLowerCase(), DayOfWeek.SATURDAY),
    new DayWord("sat", DayOfWeek.SATURDAY),
    new DayWord("суббота", DayOfWeek.SATURDAY),
    new DayWord("суб", DayOfWeek.SATURDAY),
    new DayWord("сб", DayOfWeek.SATURDAY)
  };

  private static final MonthWord[] MONTH_WORDS = {
    new MonthWord("January".toLowerCase(), 1),
    new MonthWord("jan", 1),
    new MonthWord("январь", 1),
    new MonthWord("янв", 1),
    new MonthWord("February".toLowerCase(), 2),
    new MonthWord("feb", 2),
    new MonthWord("февраль", 2),
    new MonthWord("фев", 2),
    new MonthWord("march", 3),
    new MonthWord("mar", 3),
    new MonthWord("март", 3),
    new MonthWord("мар", 3),
    new MonthWord("April".toLowerCase(), 4),
    new MonthWord("apr", 4),
    new MonthWord("апрель", 4),
    new MonthWord("апр", 4),
    new MonthWord("may", 5),
    new MonthWord("май", 5),
    new MonthWord("June".toLowerCase(), 6),
    new MonthWord("jun", 6),
    new MonthWord("июнь", 6),
    new MonthWord("июн", 6),
    new MonthWord("July".toLowerCase(), 7),
    new MonthWord("jul", 7),
    new MonthWord("июль", 7),
    new MonthWord("июл", 7),
    new MonthWord("august", 8),
    new MonthWord("aug", 8),
    new MonthWord("август", 8),
    new MonthWord("авг", 8),
    new MonthWord("September".toLowerCase(), 9),
    new MonthWord("sep", 9),
    new MonthWord("сентябрь", 9),
    new MonthWord("сен", 9),
    new MonthWord("October".toLowerCase(), 10),
    new MonthWord("oct", 10),
    new MonthWord("октябрь", 10),
    new MonthWord("окт", 10),
    new MonthWord("November".toLowerCase(), 11),
    new MonthWord("nov", 11),
    new MonthWord("ноябрь", 11),
    new MonthWord("ноя", 11),
    new MonthWord("December".toLowerCase(), 12),
    new MonthWord("dec", 12),
    new MonthWord("декабрь", 12),
    new MonthWord("дек", 12)
  };
}
