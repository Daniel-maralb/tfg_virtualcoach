package com.example.virtualcoach.app.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

//NOTE starting on LOLLIPOP use locale.forLanguageTag(es-ES)
public class DateTimeUtils {
    private static final SimpleDateFormat LOG_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm", new Locale("es", "ES"));

    public static Date parseTimestamp(long timestamp) {
        GregorianCalendar cal = (GregorianCalendar) GregorianCalendar.getInstance();
        cal.setTimeInMillis(timestamp * 1000L); // make sure it's converted to long
        return cal.getTime();
    }

    public static String formatForLog(final Date date) {
        return LOG_FORMAT.format(date);
    }

    public static String formatForLog(final long timestamp) {
        return LOG_FORMAT.format(parseTimestamp(timestamp));
    }
}
