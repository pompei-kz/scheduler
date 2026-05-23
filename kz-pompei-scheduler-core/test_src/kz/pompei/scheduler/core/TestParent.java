package kz.pompei.scheduler.core;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public abstract class TestParent {

  protected static final TimeZone UTC = TimeZone.getTimeZone("UTC");

  protected static long timestamp(TimeZone timeZone, int year, int month, int day, int hour, int minute, int second, int millisecond) {
    Calendar calendar = new GregorianCalendar(timeZone);
    calendar.clear();
    //noinspection MagicConstant
    calendar.set(year, month - 1, day, hour, minute, second);
    calendar.set(Calendar.MILLISECOND, millisecond);
    return calendar.getTimeInMillis();
  }
}
