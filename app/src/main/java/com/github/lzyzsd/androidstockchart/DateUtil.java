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
}
