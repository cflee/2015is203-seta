package net.cflee.seta.controller;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import net.cflee.seta.dao.AppDAO;
import net.cflee.seta.dao.AppUpdateDAO;
import net.cflee.seta.entity.AppUpdateRecord;
import net.cflee.seta.entity.AppUsageTimeResult;
import net.cflee.seta.entity.BasicAppUsageTimeCategoryResult;
import net.cflee.seta.utility.AppUpdateRecordUtility;
import net.cflee.seta.utility.DateUtility;

public class BasicAppUsageController {

    /**
     *
     * @param startDate start date, inclusive
     * @param endDate end date, inclusive
     * @param conn
     * @return
     * @throws SQLException
     */
    public static BasicAppUsageTimeCategoryResult computeTimeCategory(Date startDate, Date endDate, Connection conn)
            throws
            SQLException {
        // end state: average daily smartphone usage time
        BasicAppUsageTimeCategoryResult results = new BasicAppUsageTimeCategoryResult();

        // retrieve all the updates with the filtering
        // compute a new endDate to be exclusive
        ArrayList<AppUpdateRecord> records
                = AppUpdateDAO.retrieveAppUpdates(startDate, DateUtility.addDays(endDate, 1), null, null, null, null,
                        null, conn);

        // sort by mac address ascending, timestamp ascending
        Collections.sort(records, new Comparator<AppUpdateRecord>() {
            @Override
            public int compare(AppUpdateRecord o1, AppUpdateRecord o2) {
                // mac address ascending
                int compare = o1.getMacAddress().compareTo(o2.getMacAddress());
                if (compare != 0) {
                    return compare;
                }

                // break ties with timestamp ascending
                return o1.getTimestamp().compareTo(o2.getTimestamp());
            }
        });

        // group by user
        ArrayList<ArrayList<AppUpdateRecord>> recordsPerUser = AppUpdateRecordUtility.groupByUser(records);

        // for each user, group by day
        for (ArrayList<AppUpdateRecord> userRecords : recordsPerUser) {
            ArrayList<ArrayList<AppUpdateRecord>> recordsPerDay = AppUpdateRecordUtility.groupByDay(startDate, endDate,
                    userRecords);
            // for each day, sum duration
            int numOfDays = 0;
            int dayDuration = 0;
            for (ArrayList<AppUpdateRecord> dayRecords : recordsPerDay) {
                dayDuration += AppUpdateRecordUtility.sumDurations(dayRecords);
                numOfDays++;
            }

            // compute average daily
            double averageDaily = dayDuration / numOfDays;
            results.addAppUsageTime(averageDaily);
        }

        return results;
    }

    public static ArrayList<AppUsageTimeResult> computeAppCategory(Date startDate, Date endDate, Connection conn)
            throws SQLException {
        // end state: total app usage time per app category
        ArrayList<AppUsageTimeResult> results = new ArrayList<>();

        // retrieve all the updates with the filtering
        // compute a new endDate to be exclusive
        ArrayList<AppUpdateRecord> records
                = AppUpdateDAO.retrieveAppUpdates(startDate, DateUtility.addDays(endDate, 1), null, null, null, null,
                        null, conn);
        // sort by app category ascending
        // sort by mac address ascending, timestamp ascending
        Collections.sort(records, new Comparator<AppUpdateRecord>() {
            @Override
            public int compare(AppUpdateRecord o1, AppUpdateRecord o2) {
                // app category ascending
                return o1.getAppCategory().compareTo(o2.getAppCategory());
            }
        });

        // retrieve all app categories
        ArrayList<String> allAppCategories = AppDAO.getAllAppCategories(conn);

        // group by app category
        HashMap<String, ArrayList<AppUpdateRecord>> recordsPerAppCategory = AppUpdateRecordUtility.groupByAppCategory(
                records, allAppCategories);
        // compute duration for each app category
        for (Map.Entry<String, ArrayList<AppUpdateRecord>> entry : recordsPerAppCategory.entrySet()) {
            results.add(new AppUsageTimeResult(entry.getKey(), AppUpdateRecordUtility.sumDurations(entry.getValue())));
        }

        return results;
    }

}
