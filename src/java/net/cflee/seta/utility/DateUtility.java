package net.cflee.seta.utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Provides convenience methods to manipulate java.util.Date objects.
 */
public class DateUtility {

    public static Date addDays(Date specifiedDate, int numOfDays) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(specifiedDate);
        cal.add(Calendar.DATE, numOfDays);
        return cal.getTime();
    }

    /**
     * Retrieve the Date that is x minutes from the specified Date object.
     *
     * @param specifiedDate specified, original date
     * @param numOfMinutes number of minutes to be added. may be negative if minutes should be subtracted.
     * @return date with added numOfMinutes
     */
    public static Date addMinutes(Date specifiedDate, int numOfMinutes) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(specifiedDate);
        cal.add(Calendar.MINUTE, numOfMinutes);
        return cal.getTime();
    }

    /**
     * Retrieve the date that is x seconds from the specified Date object.
     *
     * @param specifiedDate specified, original date
     * @param numOfSeconds number of seconds to be added. may be negative if seconds should be subtracted.
     * @return date with added numOfSeconds
     */
    public static Date addSeconds(Date specifiedDate, int numOfSeconds) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(specifiedDate);
        cal.add(Calendar.SECOND, numOfSeconds);
        return cal.getTime();
    }

    public static boolean isSameDay(Date date1, Date date2) {
        // get only the day component
        // this computes days assuming Dates are in UTC (so ignoring time zones)
        double day1 = Math.floor((float) date1.getTime() / 1000 / 60 / 60 / 24);
        double day2 = Math.floor((float) date2.getTime() / 1000 / 60 / 60 / 24);

        return day1 == day2;
    }

    public static boolean isBeforeOrSameDay(Date date1, Date date2) {
        // get only the day component
        long day1 = date1.getTime() / 1000 / 60 / 60 / 24;
        long day2 = date2.getTime() / 1000 / 60 / 60 / 24;

        return day1 <= day2;
    }

    public static Date parseDatetimeString(String dateString) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        // this may directly throw ParseException if string doesn't adhere to format
        Date date = sdf.parse(dateString);

        // check if string adheres to format but doesn't make sense/isn't canonical
        // e.g. 2015-13-32 24:60:60
        String formattedTimestamp = sdf.format(date);
        if (!(formattedTimestamp.equals(dateString))) {
            throw new ParseException("Date format is not canonical.", -1);
        }

        return date;
    }

    public static Date parseDateString(String dateString) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        // this may directly throw ParseException if string doesn't adhere to format
        Date date = sdf.parse(dateString);

        // check if string adheres to format but doesn't make sense/isn't canonical
        // e.g. 2015-13-32
        String formattedTimestamp = sdf.format(date);
        if (!(formattedTimestamp.equals(dateString))) {
            throw new ParseException("Date format is not canonical.", -1);
        }

        return date;
    }
}
