package net.cflee.seta.controller;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import net.cflee.seta.dao.AppUpdateDAO;
import net.cflee.seta.entity.AppUpdateRecord;
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

}
