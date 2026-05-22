package kz.pompei.scheduler.core.run_checker;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import static kz.pompei.scheduler.core.run_checker.RunCheckerUtil.setCalendarHMS;

@RequiredArgsConstructor
public class RunChecker_FromHMS_ToHMS_Every implements RunChecker {

  private final @NonNull TimeZone timeZone;

  private final int hourFrom, minuteFrom, secondFrom;
  private final int hourTo, minuteTo, secondTo;
  private final long everyMs;

  @Override public boolean needRun(long timestampStartedAt, long timestampMsFrom, long timestampMsTo) {
    if (timestampMsTo <= timestampMsFrom || everyMs <= 0) {
      return false;
    }

    long secFrom = hourFrom * 3600L + minuteFrom * 60L + secondFrom;
    long secTo   = hourTo * 3600L + minuteTo * 60L + secondTo;

    if (secFrom >= secTo) {
      return false;
    }

    Calendar calendar = new GregorianCalendar(timeZone);
    calendar.setTimeInMillis(timestampMsFrom);

    setCalendarHMS(calendar, hourFrom, minuteFrom, secondFrom);

    long runPeriodMsFrom = calendar.getTimeInMillis();

    setCalendarHMS(calendar, hourTo, minuteTo, secondTo);

    long runPeriodMsTo = calendar.getTimeInMillis();

    return needRunMs(timestampMsFrom, timestampMsTo, runPeriodMsFrom, runPeriodMsTo);
  }

  private boolean needRunMs(long timestampMsFrom, long timestampMsTo, long runPeriodMsFrom, long runPeriodMsTo) {
    throw new RuntimeException("2026-05-22 12:09 Created empty method body RunChecker_FromHMS_ToHMS_Every.needRunMs()");
  }

}
