package night.app.utils;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DatetimeUtils {
    public static long dayAdd(long date, int day) {
        return date + TimeUnit.DAYS.toMillis(day);
    }

    public static long getTodayAtMidNight() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTimeInMillis();
    }

    public static long getMsOfToday() {
        return System.currentTimeMillis() - getTodayAtMidNight();
    }

    public static long getClosestDateTime(long msOfDay) {
        Calendar current = Calendar.getInstance();

        if (getMsOfToday() > msOfDay) {
            current.add(Calendar.DATE, 1);
        }

        int targetHour = (int) TimeUnit.MILLISECONDS.toHours(msOfDay);

        // TimeUnits will return total minutes including hours
        int targetMin = (int) TimeUnit.MILLISECONDS.toMinutes(msOfDay) - targetHour * 60;

        current.set(Calendar.HOUR_OF_DAY, targetHour);
        current.set(Calendar.MINUTE, targetMin);
        current.set(Calendar.SECOND, 0);
        current.set(Calendar.MILLISECOND, 0);

        return current.getTimeInMillis();
    }

    public static String toTimeNotation(Calendar calendar) {
        return toTimeNotation(
                calendar.get(Calendar.HOUR) * 60 * 60
                + (calendar.get(Calendar.AM_PM) == Calendar.PM ? 12 : 0) * 60 * 60
                + calendar.get(Calendar.MINUTE) * 60
        );
    }

    public static String toTimeNotation(int seconds) {
        int hours = (int) Math.floor(seconds/3600f) ;
        int minutes = seconds / 60 - hours * 60;

        return LocalTime.of(hours, minutes).toString();
    }

    public static String toHrMinString(long ms) {
        long hours = TimeUnit.MILLISECONDS.toHours(ms);
        long minutes = TimeUnit.MINUTES.toMinutes(ms) - hours * 60;

        if (hours > 0 && minutes <= 0) return hours + "h ";
        return hours + "h " + minutes + "m";
    }

    public static String toHrMinString(int seconds) {
        int hours = seconds / 3600;
        int minutes = seconds / 60 - hours * 60;

        if (hours <= 0) return minutes + "m";
        if (hours > 0 && minutes <= 0) return hours + "h ";

        return hours + "h " + minutes + "m";
    }

    public static String toHrMinSec(int seconds) {
        int hours = seconds / 3600;
        int minutes = seconds / 60 - hours * 60;

        if (hours <= 0) {
            if (minutes == 0) {
                return seconds - hours*3600 + "s";
            }
            return minutes + "m " + (seconds - hours*3600 - minutes*60) + "s";
        }

        if (hours > 0 && minutes <= 0) {
            return hours + "h ";
        }
        return hours + "h " + minutes + "m " + (seconds - hours*3600 - minutes*60) + "s";
    }

    public static String toDateString(long timestamp, boolean year) {
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime dateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(timestamp), zoneId);

        String pattern = year ? "dd MMM yyyy" : "dd MMM";
        return DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH).format(dateTime);
    }

    public static String toDateString(long startTimestamp, long endTimestamp) {
        return toDateString(startTimestamp, false) + " ~ " + toDateString(endTimestamp, false);
    }
}
