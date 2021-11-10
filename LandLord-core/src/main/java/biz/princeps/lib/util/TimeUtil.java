package biz.princeps.lib.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static java.time.temporal.ChronoUnit.SECONDS;

/**
 * Created by spatium on 11.06.17.
 */
public class TimeUtil {

    public static String secToMin(int i) {
        int ms = i / 60;
        int ss = i % 60;
        String m = (ms < 10 ? "0" : "") + ms;
        String s = (ss < 10 ? "0" : "") + ss;
        return m + ":" + s;
    }

    public static LocalDateTime stringToTime(String s) {
        if (s == null || s.isEmpty() || s.equals("null"))
            return null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return LocalDateTime.parse(s, formatter);
    }

    public static String timeToString(LocalDateTime time) {
        if (time == null)
            return "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return time.format(formatter);
    }

    public static String formatSeconds(long seconds) {
        Duration dura = Duration.of(seconds, SECONDS);
        return dura.toHours() + ":" + dura.minusHours(dura.toHours()).toMinutes() + " Stunden";
    }

}
