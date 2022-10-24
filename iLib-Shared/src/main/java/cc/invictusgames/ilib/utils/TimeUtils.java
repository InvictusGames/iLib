package cc.invictusgames.ilib.utils;

import org.apache.commons.lang3.time.DurationFormatUtils;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author langgezockt (langgezockt@gmail.com)
 * 11.06.2019 / 09:55
 * iUtils / cc.invictusgames.iutils.utils
 */

public class TimeUtils {

    public static final long MINUTE = TimeUnit.MINUTES.toSeconds(1);
    private static final ThreadLocal<StringBuilder> mmssBuilder = ThreadLocal.withInitial(StringBuilder::new);

    public static long parseTime(String input) {
        if (input.equals("0") || input.equals("") || input.equalsIgnoreCase("0s")) {
            return 0;
        }

        String[] lifeMatch = { "w", "d", "h", "m", "s" };
        long[] lifeInterval = {
                TimeUnit.DAYS.toMillis(7), //w
                TimeUnit.DAYS.toMillis(1), //d
                TimeUnit.HOURS.toMillis(1), //h
                TimeUnit.MINUTES.toMillis(1), //m
                TimeUnit.SECONDS.toMillis(1)  //s
        };
        long millis = -1;
        for (int i = 0; i < lifeMatch.length; ++i) {
            Matcher matcher = Pattern.compile("([0-9]+)" + lifeMatch[i]).matcher(input);
            while (matcher.find()) {
                long matched = Long.parseLong(matcher.group(1));
                if (matched < 0)
                    continue;

                if (millis == -1)
                    millis = 0;

                millis += matched * lifeInterval[i];
            }
        }
        return millis;
    }

    public static String formatDetailed(long input) {
        if (input == -1) {
            return "Permanent";
        }

        return formatDetailed(input, TimeUnit.MILLISECONDS);
    }

    public static String formatDetailed(long input, TimeUnit timeUnit) {
        if (input == -1) {
            return "Permanent";
        }

        long secs = timeUnit.toSeconds(input);

        if (secs == 0) {
            return "0 seconds";
        }

        long remainder = secs % 86400;
        long days = secs / 86400;
        long hours = remainder / 3600;
        long minutes = remainder / 60 - hours * 60;
        long seconds = remainder % 3600 - minutes * 60;
        String fDays = (days > 0) ? (" " + days + " day" + ((days > 1) ? "s" : "")) : "";
        String fHours = (hours > 0) ? (" " + hours + " hour" + ((hours > 1) ? "s" : "")) : "";
        String fMinutes = (minutes > 0) ? (" " + minutes + " minute" + ((minutes > 1) ? "s" : "")) : "";
        String fSeconds = (seconds > 0) ? (" " + seconds + " second" + ((seconds > 1) ? "s" : "")) : "";
        return  (fDays + fHours + fMinutes + fSeconds).trim();
    }

    public static String formatTimeAgo(long input) {
        return formatTimeAgo(input, TimeUnit.MILLISECONDS);
    }

    public static String formatTimeAgo(long input, TimeUnit timeUnit) {
        long time = System.currentTimeMillis() - timeUnit.toMillis(input);

        if (time >= TimeUnit.DAYS.toMillis(365)) {
            time = time / TimeUnit.DAYS.toMillis(365);
            return time + " year" + (time == 1 ? "" : "s") + " ago";
        }

        if (time >= TimeUnit.DAYS.toMillis(30)) {
            time = time / TimeUnit.DAYS.toMillis(30);
            return time + " month" + (time == 1 ? "" : "s") + " ago";
        }

        if (time >= TimeUnit.DAYS.toMillis(1)) {
            time = time / TimeUnit.DAYS.toMillis(1);
            return time + " day" + (time == 1 ? "" : "s") + " ago";
        }

        if (time >= TimeUnit.HOURS.toMillis(1)) {
            time = time / TimeUnit.HOURS.toMillis(1);
            return time + " hour" + (time == 1 ? "" : "s") + " ago";
        }

        if (time >= TimeUnit.MINUTES.toMillis(1)) {
            time = time / TimeUnit.MINUTES.toMillis(1);
            return time + " minute" + (time == 1 ? "" : "s") + " ago";
        }

        if (time >= TimeUnit.SECONDS.toMillis(1)) {
            time = time / TimeUnit.SECONDS.toMillis(1);
            return time + " second" + (time == 1 ? "" : "s") + " ago";
        }

        return "now";
    }

    public static String formatHHMMSS(long input) {
        return formatHHMMSS(input, false, TimeUnit.MILLISECONDS);
    }

    public static String formatHHMMSS(long input, TimeUnit timeUnit) {
        return formatHHMMSS(input, false, timeUnit);
    }

    public static String formatHHMMSS(long input, boolean millis) {
        return formatHHMMSS(input, millis, TimeUnit.MILLISECONDS);
    }

    public static String formatHHMMSS(long input, boolean displayMillis, TimeUnit timeUnit) {
        long secs = timeUnit.toSeconds(input);

        if (displayMillis && secs < MINUTE) {
            long millis = timeUnit.toMillis(input);

            long milliseconds = millis % 1000;
            millis -= milliseconds;

            long seconds = millis / 1000;
            return seconds + "." + (milliseconds / 100) + "s";
        }

        long seconds = secs % 60;
        secs -= seconds;

        long minutesCount = secs / 60;
        long minutes = minutesCount % 60L;
        minutesCount -= minutes;

        long hours = minutesCount / 60L;

        StringBuilder result = TimeUtils.mmssBuilder.get();
        result.setLength(0);

        if (hours > 0L) {
            if (hours < 10L)
                result.append("0");

            result.append(hours);
            result.append(":");
        }

        if (minutes < 10L)
            result.append("0");

        result.append(minutes);
        result.append(":");

        if (seconds < 10)
            result.append("0");

        result.append(seconds);
        return result.toString();
    }

    public static String formatTimeShort(long input) {
        if (input == -1) {
            return "Permanent";
        }

        return formatTimeShort(input, TimeUnit.MILLISECONDS);
    }

    public static String formatTimeShort(long input, TimeUnit timeUnit) {
        if (input == -1) {
            return "Permanent";
        }

        long secs = timeUnit.toSeconds(input);

        if (secs == 0) {
            return "0 seconds";
        }

        long remainder = secs % 86400;
        long days = secs / 86400;
        long hours = remainder / 3600;
        long minutes = remainder / 60 - hours * 60;
        long seconds = remainder % 3600 - minutes * 60;
        String fDays = (days > 0) ? (" " + days + "d") : "";
        String fHours = (hours > 0) ? (" " + hours + "h") : "";
        String fMinutes = (minutes > 0) ? (" " + minutes + "m") : "";
        String fSeconds = (seconds > 0) ? (" " + seconds + "s") : "";
        return  (fDays + fHours + fMinutes + fSeconds).trim();
    }

    public static String formatDate(long input) {
        return formatDate(input, true, Statics.TIME_ZONE);
    }

    public static String formatDate(long input, boolean time) {
        return formatDate(input, time, Statics.TIME_ZONE);
    }

    public static String formatDate(long input, TimeZone timeZone) {
        return formatDate(input, true, timeZone);
    }

    public static String formatDate(long input, boolean time, TimeZone timeZone) {
        if (input == -1) {
            return "Permanent";
        }
        DateFormat formatter = new SimpleDateFormat("MM/dd/yy" + (time ? " hh:mm:ss a" : "") + " z");
        formatter.setTimeZone(timeZone);
        return formatter.format(input);
    }

}
