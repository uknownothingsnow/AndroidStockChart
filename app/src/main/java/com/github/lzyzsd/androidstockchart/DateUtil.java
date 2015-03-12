package com.github.lzyzsd.androidstockchart;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.TimeZone;

/**
 * Created by Bruce on 2/28/15.
 */
public class DateUtil {
    public static DateTime parse_yyyyddMM_hhmmss(String dateStr) {
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyyMMdd HH:mm:ss");
        fmt.withZone(DateTimeZone.forTimeZone(TimeZone.getTimeZone("asia/shanghai")));
        return DateTime.parse(dateStr, fmt);
    }

    public static DateTime parse_yyyyddMM(String dateStr) {
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyyMMdd");
        fmt.withZone(DateTimeZone.forTimeZone(TimeZone.getTimeZone("asia/shanghai")));
        return DateTime.parse(dateStr, fmt);
    }

    public static String format_hh_mm(DateTime dateTime) {
        return dateTime.toString("HH:mm");
    }

    public static String format_hh_mm(long value) {
        return format_hh_mm(new DateTime(value));
    }

    public static int getTradeStartMinuteFromBondCategory(String bondCategory) {
        String[] ranges = bondCategory.split(";");
        return Integer.parseInt(ranges[0].split("-")[0]);
    }

    public static int getTradeEndMinuteFromBondCategory(String bondCategory) {
        String[] ranges = bondCategory.split(";");
        return Integer.parseInt(ranges[ranges.length - 1].split("-")[1]);
    }

    public static int getPoints4OneTradeDayFromBondCategory(String bondCategory) {
        String[] ranges = bondCategory.split(";");
        int value = 0;
        for (String range : ranges) {
            String[] temp = range.split("-");
            value += (Integer.parseInt(temp[1]) - Integer.parseInt(temp[0]));
        }

        return value;
    }

    public static String getStartLabelFromBondCategory(String bondCategory) {
        int value = getTradeStartMinuteFromBondCategory(bondCategory);
        int hour = value / 60;
        int minute = value % 60;

        DateTime  dateTime = DateTime.now().withTimeAtStartOfDay().withHourOfDay(hour).withMinuteOfHour(minute);
        return dateTime.toString("HH:mm");
    }

    public static String getEndLabelFromBondCategory(String bondCategory) {
        int value = getTradeEndMinuteFromBondCategory(bondCategory);
        int hour = value / 60;
        int minute = value % 60;

        DateTime  dateTime = DateTime.now().withTimeAtStartOfDay().withHourOfDay(hour).withMinuteOfHour(minute);
        return dateTime.toString("HH:mm");
    }
}
