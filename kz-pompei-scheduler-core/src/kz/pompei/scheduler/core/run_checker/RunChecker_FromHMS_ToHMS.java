package kz.pompei.scheduler.core.run_checker;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RunChecker_FromHMS_ToHMS implements RunChecker {

  private final @NonNull TimeZone timeZone;

  private final int hourFrom, minuteFrom, secondFrom;
  private final int hourTo, minuteTo, secondTo;

  @Override public boolean needRun(long timestampStartedAt, long timestampMsFrom, long timestampMsTo) {
    GregorianCalendar calendar = new GregorianCalendar(timeZone);

    {
      calendar.setTimeInMillis(timestampMsFrom);
      if (isInsideInterval(calendar, timestampMsFrom)) {
        return true;
      }
    }
    {
      calendar.setTimeInMillis(timestampMsTo);
      if (isInsideInterval(calendar, timestampMsTo)) {
        return true;
      }
    }

    return false;
  }

  private boolean isInsideInterval(GregorianCalendar calendar, long timestampMs) {
    long timestampMsFrom = timestamp(calendar, hourFrom, minuteFrom, secondFrom);
    long timestampMsTo   = timestamp(calendar, hourTo, minuteTo, secondTo);

    return timestampMsFrom < timestampMs && timestampMs < timestampMsTo;
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
