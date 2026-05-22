package kz.pompei.scheduler.core.run_checker;

import java.util.Calendar;

public enum DayOfWeek {
  SUNDAY(Calendar.SUNDAY),
  MONDAY(Calendar.MONDAY),
  TUESDAY(Calendar.TUESDAY),
  WEDNESDAY(Calendar.WEDNESDAY),
  THURSDAY(Calendar.THURSDAY),
  FRIDAY(Calendar.FRIDAY),
  SATURDAY(Calendar.SATURDAY);

  private final int calendarDayOfWeek;

  DayOfWeek(int calendarDayOfWeek) {
    this.calendarDayOfWeek = calendarDayOfWeek;
  }

  static DayOfWeek fromCalendarDayOfWeek(int calendarDayOfWeek) {
    for (DayOfWeek dayOfWeek : values()) {
      if (dayOfWeek.calendarDayOfWeek == calendarDayOfWeek) {
        return dayOfWeek;
      }
    }

    throw new IllegalArgumentException("Unknown calendar day of week: " + calendarDayOfWeek);
  }
}
