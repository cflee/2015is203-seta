package net.cflee.seta.utility;

import java.util.Calendar;
import java.util.Date;

/**
 * Provides convenience methods to manipulate java.util.Date objects.
 */
public class DateUtility {

    /**
     * Retrieve the Date that is x minutes from the specified Date object.
     *
     * @param specifiedDate specified, original date
     * @param numOfMinutes number of minutes to be added. may be negative if
     * minutes should be subtracted.
     * @return date with added numOfMinutes
     */
    public static Date addMinutes(Date specifiedDate, int numOfMinutes) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(specifiedDate);
        cal.add(Calendar.MINUTE, numOfMinutes);
        return cal.getTime();
    }

    /**
     * Retrieve the date differences
     *
     * @param specifiedDate user specified time
     * @param queriedDate time retrieved in query
     * @return the differences in minutes
     */
    public static long minimumTime(Date specifiedDate, Date queriedDate) {
        long returnDate = (((specifiedDate.getTime() - queriedDate.getTime()) / 1000));
        return returnDate;
    }

    /**
     * Retrieve the date that is x seconds from the specified Date object.
     *
     * @param specifiedDate specified, original date
     * @param numOfSeconds number of seconds to be added. may be negative if
     * seconds should be subtracted.
     * @return date with added numOfSeconds
     */
    public static Date addSeconds(Date specifiedDate, int numOfSeconds) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(specifiedDate);
        cal.add(Calendar.SECOND, numOfSeconds);
        return cal.getTime();
    }
}
