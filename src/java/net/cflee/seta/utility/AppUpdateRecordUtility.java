package net.cflee.seta.utility;

import java.util.ArrayList;
import java.util.Date;
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
        results.add(currentUserRecords);

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
        ArrayList<AppUpdateRecord> currentDayRecords;
        int i = 0;

        for (Date d = new Date(startDate.getTime()); DateUtility.isBeforeOrSameDay(d, endDate); d = DateUtility
                .addDays(d, 1)) {
            currentDayRecords = new ArrayList<>();

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
