package com.github.lzyzsd.androidstockchart.util;

import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DurationFieldType;

/**
 * Created by Bruce on 3/4/15.
 */
public class XAxisUtil {
    public static long getStartValue() {
        DateTime dateTime = DateTime.now();
        if (dateTime.getHourOfDay() < 6) {
            return dateTime
                    .withFieldAdded(DurationFieldType.days(), -1)
                    .withTimeAtStartOfDay()
                    .withField(DateTimeFieldType.hourOfDay(), 6)
                    .getMillis();
        } else {
            return dateTime
                    .withTimeAtStartOfDay()
                    .withField(DateTimeFieldType.hourOfDay(), 6)
                    .getMillis();
        }
    }

    public static long getEndValue() {
        DateTime dateTime = DateTime.now();
        if (dateTime.getHourOfDay() < 16) {
            return dateTime
                    .withTimeAtStartOfDay()
                    .withField(DateTimeFieldType.hourOfDay(), 16)
                    .getMillis();
        } else {
            return dateTime
                    .withFieldAdded(DurationFieldType.days(), 1)
                    .withTimeAtStartOfDay()
                    .withField(DateTimeFieldType.hourOfDay(), 6)
                    .getMillis();
        }
    }

    public static long getCellsNumber() {
        return 11;
    }

    public static long getCellSize() {
        return 7200_000;
    }
}
