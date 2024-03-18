package night.app.utils;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;

public class TimeUtils {
    public static int DayInSeconds = 24 * 60 * 60;

    public static int dayAdd(int date, int day) {
        return date + day * DayInSeconds;
    }

    public static int getToday() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);

        return (int) (calendar.getTimeInMillis() / 1000);
    }

    public static int timeDiff(int earlier, int later) {
        if (earlier > later) {
            return later + (24*60 - earlier);
        }
        return later - earlier;
    }

    public static String toTimeNotation(int seconds) {
        int hours = (int) Math.floor(seconds/3600f) ;
        int minutes = seconds / 60 - hours * 60;

        return LocalTime.of(hours, minutes).toString();
    }

    public static String toHrMinString(int seconds) {
        int hours = seconds / 3600;
        int minutes = seconds / 60 - hours * 60;

        if (hours <= 0) {
            return minutes + "m";
        }

        if (hours >0 && minutes <= 0) {
            return hours + "h ";
        }
        return hours + "h " + minutes + "m";
    }

    public static String toDateString(long timestamp, boolean year) {
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime dateTime = ZonedDateTime.ofInstant(Instant.ofEpochSecond(timestamp), zoneId);

        String pattern = year ? "dd MMM yyyy" : "dd MMM";
        return DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH).format(dateTime);
    }

    public static String toDateString(long startTimestamp, long endTimestamp) {
        return toDateString(startTimestamp, false) + " ~ " + toDateString(endTimestamp, false);
    }
}
