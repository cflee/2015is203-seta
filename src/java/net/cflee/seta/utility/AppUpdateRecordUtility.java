package net.cflee.seta.utility;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import net.cflee.seta.entity.AppUpdateRecord;

public class AppUpdateRecordUtility {

    /**
     *
     * @param records AppUpdateRecords sorted in mac-address order
     * @return ArrayList of ArrayLists of updates for each user (as identified by mac-address)
     */
    public static ArrayList<ArrayList<AppUpdateRecord>> groupByUser(ArrayList<AppUpdateRecord> records) {
        ArrayList<ArrayList<AppUpdateRecord>> results = new ArrayList<>();
        ArrayList<AppUpdateRecord> currentUserRecords = new ArrayList<>();
        String previousUser = null;

        for (AppUpdateRecord record : records) {
            // is this still the same user as previous record
            if (previousUser != null && !previousUser.equals(record.getMacAddress())) {
                // different user
                results.add(currentUserRecords);
                currentUserRecords = new ArrayList<>();
            }

            currentUserRecords.add(record);
            previousUser = record.getMacAddress();
        }

        // final sub-list
        if (previousUser != null) {
            results.add(currentUserRecords);
        }

        return results;
    }

    public static ArrayList<ArrayList<AppUpdateRecord>> groupByApp(ArrayList<AppUpdateRecord> records) {
        ArrayList<ArrayList<AppUpdateRecord>> results = new ArrayList<>();
        ArrayList<AppUpdateRecord> currentAppRecords = new ArrayList<>();
        int previousAppId = -1;

        for (AppUpdateRecord record : records) {
            // is this still the same app as previous record
            if (previousAppId != -1 && previousAppId != record.getAppId()) {
                results.add(currentAppRecords);
                currentAppRecords = new ArrayList<>();
            }

            currentAppRecords.add(record);
            previousAppId = record.getAppId();
        }

        // final sub-list
        if (previousAppId != -1) {
            results.add(currentAppRecords);
        }

        return results;
    }

    public static ArrayList<ArrayList<AppUpdateRecord>> groupBySchool(ArrayList<AppUpdateRecord> records) {
        ArrayList<ArrayList<AppUpdateRecord>> results = new ArrayList<>();
        ArrayList<AppUpdateRecord> currentSchoolRecords = new ArrayList<>();
        String previousSchool = null;

        for (AppUpdateRecord record : records) {
            // is this still the same app as previous record
            if (previousSchool != null && !previousSchool.equals(record.getUserSchool())) {
                results.add(currentSchoolRecords);
                currentSchoolRecords = new ArrayList<>();
            }

            currentSchoolRecords.add(record);
            previousSchool = record.getUserSchool();
        }

        // final sub-list
        if (previousSchool != null) {
            results.add(currentSchoolRecords);
        }

        return results;
    }

    /**
     *
     * @param startDate start date, inclusive
     * @param endDate end date, inclusive
     * @param records AppUpdateRecords in date ascending order
     * @return ArrayList of ArrayLists of updates for each day, with empty ArrayLists if none match
     */
    public static ArrayList<ArrayList<AppUpdateRecord>> groupByDay(Date startDate, Date endDate,
            ArrayList<AppUpdateRecord> records) {
        ArrayList<ArrayList<AppUpdateRecord>> results = new ArrayList<>();
        int i = 0;

        for (Date d = new Date(startDate.getTime()); DateUtility.isBeforeOrSameDay(d, endDate); d = DateUtility
                .addDays(d, 1)) {
            ArrayList<AppUpdateRecord> currentDayRecords = new ArrayList<>();

            for (; i < records.size(); i++) {
                AppUpdateRecord current = records.get(i);
                if (!DateUtility.isSameDay(d, current.getTimestamp())) {
                    // different day, break this for loop
                    break;
                }
                // same day, add to today's list
                currentDayRecords.add(current);
            }

            results.add(currentDayRecords);
        }

        return results;
    }

    public static ArrayList<ArrayList<AppUpdateRecord>> groupByHour(ArrayList<AppUpdateRecord> records) {
        ArrayList<ArrayList<AppUpdateRecord>> results = new ArrayList<>();
        int i = 0;

        for (int h = 0; h < 24; h++) {
            ArrayList<AppUpdateRecord> currentHourRecords = new ArrayList<>();

            for (; i < records.size(); i++) {
                AppUpdateRecord current = records.get(i);
                Calendar cal = GregorianCalendar.getInstance();
                cal.setTime(current.getTimestamp());
                int currentHour = cal.get(Calendar.HOUR_OF_DAY);

                if (currentHour != h) {
                    // different hour, break this for loop to move on to next hour
                    break;
                }
                // same hour, add to this hour's list

                currentHourRecords.add(current);
            }

            results.add(currentHourRecords);
        }

        return results;
    }

    /**
     *
     * @param records AppUpdateRecords in app category ascending order
     * @param categories categories in ascending order
     * @return
     */
    public static HashMap<String, ArrayList<AppUpdateRecord>> groupByAppCategory(ArrayList<AppUpdateRecord> records,
            ArrayList<String> categories) {
        HashMap<String, ArrayList<AppUpdateRecord>> results = new HashMap<>();
        int i = 0;

        for (String category : categories) {
            ArrayList<AppUpdateRecord> currentCategoryRecords = new ArrayList<>();
            String currentCategory = category;

            for (; i < records.size(); i++) {
                AppUpdateRecord currentRecord = records.get(i);
                // category has changed, move on
                if (!currentCategory.equals(currentRecord.getAppCategory())) {
                    break;
                }
                // same category
                currentCategoryRecords.add(currentRecord);
            }
            results.put(category, currentCategoryRecords);
        }

        return results;
    }

    public static int sumDurations(ArrayList<AppUpdateRecord> records) {
        int durationSum = 0;

        for (AppUpdateRecord record : records) {
            durationSum += record.getDuration();
        }

        return durationSum;
    }

}
