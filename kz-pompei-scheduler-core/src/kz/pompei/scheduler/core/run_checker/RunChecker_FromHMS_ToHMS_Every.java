package kz.pompei.scheduler.core.run_checker;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

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

    GregorianCalendar calendar = new GregorianCalendar(timeZone);
    calendar.setTimeInMillis(timestampMsFrom);
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);

    while (calendar.getTimeInMillis() < timestampMsTo) {
      if (needRunInDay(calendar, timestampMsFrom, timestampMsTo)) {
        return true;
      }
      calendar.add(Calendar.DAY_OF_MONTH, 1);
    }

    return false;
  }

  private boolean needRunInDay(GregorianCalendar calendar, long timestampMsFrom, long timestampMsTo) {
    long periodFrom = timestamp(calendar, hourFrom, minuteFrom, secondFrom);
    long periodTo   = timestamp(calendar, hourTo, minuteTo, secondTo);
    if (periodTo <= periodFrom) {
      return false;
    }

    long fromMs = Math.max(periodFrom, timestampMsFrom);
    long toMs   = Math.min(periodTo, timestampMsTo);
    if (toMs <= fromMs) {
      return false;
    }

    long deltaMs = fromMs - periodFrom;
    long periods = deltaMs / everyMs;
    if (deltaMs % everyMs != 0) {
      periods++;
    }

    long timeMs = periodFrom + everyMs * periods;

    return fromMs <= timeMs && timeMs < toMs;
  }

  private static long timestamp(GregorianCalendar source, int hour, int minute, int second) {
    GregorianCalendar calendar = (GregorianCalendar) source.clone();
    calendar.set(Calendar.HOUR_OF_DAY, hour);
    calendar.set(Calendar.MINUTE, minute);
    calendar.set(Calendar.SECOND, second);
    calendar.set(Calendar.MILLISECOND, 0);
    return calendar.getTimeInMillis();
  }
}
