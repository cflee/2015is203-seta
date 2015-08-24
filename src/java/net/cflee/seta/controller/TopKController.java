package net.cflee.seta.controller;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import net.cflee.seta.dao.AppUpdateDAO;
import net.cflee.seta.entity.AppUpdateRecord;
import net.cflee.seta.entity.TopKResult;
import net.cflee.seta.utility.AppUpdateRecordUtility;
import net.cflee.seta.utility.DateUtility;
import net.cflee.seta.utility.TopKUtility;

public class TopKController {

    public static ArrayList<TopKResult> computeApps(Date startDate, Date endDate, String school, int k, Connection conn)
            throws SQLException {
        ArrayList<TopKResult> results = new ArrayList<>();

        // retrieve all the updates with the filtering
        // compute a new endDate to be exclusive
        ArrayList<AppUpdateRecord> records
                = AppUpdateDAO.retrieveAppUpdates(startDate, DateUtility.addDays(endDate, 1), null, null, school, conn);

        // sort by app ID ascending, then group by app ID
        Collections.sort(records, new Comparator<AppUpdateRecord>() {
            @Override
            public int compare(AppUpdateRecord o1, AppUpdateRecord o2) {
                return Integer.compare(o1.getAppId(), o2.getAppId());
            }
        });
        ArrayList<ArrayList<AppUpdateRecord>> groupedByApp = AppUpdateRecordUtility.groupByApp(records);

        for (ArrayList<AppUpdateRecord> appUpdates : groupedByApp) {
            int duration = AppUpdateRecordUtility.sumDurations(appUpdates);
            if (duration > 0) {
                results.add(new TopKResult(appUpdates.get(0).getAppName(),
                        duration));
            }
        }

        TopKUtility.sortRankFilter(results, k);

        return results;
    }

    public static ArrayList<TopKResult> computeStudents(Date startDate, Date endDate, String appCategory, int k,
            Connection conn) throws SQLException {
        ArrayList<TopKResult> results = new ArrayList<>();

        // retrieve all the updates with the filtering
        // compute a new endDate to be exclusive
        ArrayList<AppUpdateRecord> records
                = AppUpdateDAO.retrieveAppUpdates(startDate, DateUtility.addDays(endDate, 1), null, null, null, conn);

        // sort by mac address ascending, timestamp ascending, then group by user
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
        ArrayList<ArrayList<AppUpdateRecord>> recordsPerUser = AppUpdateRecordUtility.groupByUser(records);

        // for each user, sum only the relevant app category's durations
        for (ArrayList<AppUpdateRecord> userRecords : recordsPerUser) {
            int duration = 0;

            for (AppUpdateRecord record : userRecords) {
                if (record.getAppCategory().equals(appCategory)) {
                    duration += record.getDuration();
                }
            }

            if (duration > 0) {
                results.add(new TopKResult(userRecords.get(0).getUserName(), duration));
            }
        }

        TopKUtility.sortRankFilter(results, k);

        return results;
    }

    public static ArrayList<TopKResult> computeSchools(Date startDate, Date endDate, String appCategory, int k,
            Connection conn) throws SQLException {
        ArrayList<TopKResult> results = new ArrayList<>();

        // retrieve all the updates with the filtering
        // compute a new endDate to be exclusive
        ArrayList<AppUpdateRecord> records
                = AppUpdateDAO.retrieveAppUpdates(startDate, DateUtility.addDays(endDate, 1), null, null, null, conn);

        // sort by school ascending, then group by school
        Collections.sort(records, new Comparator<AppUpdateRecord>() {
            @Override
            public int compare(AppUpdateRecord o1, AppUpdateRecord o2) {
                return o1.getUserSchool().compareTo(o2.getUserSchool());
            }
        });
        ArrayList<ArrayList<AppUpdateRecord>> recordsPerSchool = AppUpdateRecordUtility.groupBySchool(records);

        // for each school, sum only the relevant app category's durations
        for (ArrayList<AppUpdateRecord> schoolRecords : recordsPerSchool) {
            int duration = 0;

            for (AppUpdateRecord record : schoolRecords) {
                if (record.getAppCategory().equals(appCategory)) {
                    duration += record.getDuration();
                }
            }

            if (duration > 0) {
                results.add(new TopKResult(schoolRecords.get(0).getUserSchool(), duration));
            }
        }

        TopKUtility.sortRankFilter(results, k);

        return results;
    }

}
