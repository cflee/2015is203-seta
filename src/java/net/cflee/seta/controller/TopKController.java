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
                = AppUpdateDAO.retrieveAppUpdates(startDate, DateUtility.addDays(endDate, 1), null, null, school, null,
                        null, conn);

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

        TopKUtility.sortRankFilter(results, k);

        return results;
    }

}
