package com.paramount.bed.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateScoreUtil {
    public static String getDailyStartDate() {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());

        c = (Calendar) c.clone();
        c.setFirstDayOfWeek(Calendar.SUNDAY);
        // last week
        c.add(Calendar.WEEK_OF_YEAR, -1);
        // first day
        c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek());
        return getStringFromTime(c);
    }

    public static String getDailyEndDate() {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c = (Calendar) c.clone();
        // first day of this week
        c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek());
        // last day of previous week
        c.add(Calendar.WEEK_OF_YEAR, 1);
        return getStringFromTime(c);
    }

    public static String getWeeklyStartDate() {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c = (Calendar) c.clone();
        c.setFirstDayOfWeek(Calendar.SUNDAY);
        // last week
        c.add(Calendar.WEEK_OF_YEAR, -10);
        // first day
        c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek());
        return getStringFromTime(c);
    }

    public static String getWeeklyEndDate() {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c = (Calendar) c.clone();
        // first day of this week
        c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek());
        return getStringFromTime(c);
    }

    public static String getStringFromTime(Calendar calendar) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        return format.format(calendar.getTime());
    }
}
