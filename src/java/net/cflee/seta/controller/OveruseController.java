
package net.cflee.seta.controller;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import net.cflee.seta.dao.AppUpdateDAO;
import net.cflee.seta.entity.AppUpdateRecord;
import net.cflee.seta.entity.OveruseResult;
import net.cflee.seta.utility.AppUpdateRecordUtility;
import net.cflee.seta.utility.DateUtility;

public class OveruseController {

    public static OveruseResult computeBasicReport(Date startDate, Date endDate, String email, Connection conn) throws
            SQLException {
        // retrieve all the updates with the filtering
        // compute a new endDate to be exclusive
        ArrayList<AppUpdateRecord> userRecords
                = AppUpdateDAO.retrieveAppUpdates(startDate, DateUtility.addDays(endDate, 1), null, null, null, email,
                        conn);

        // group by day
        ArrayList<ArrayList<AppUpdateRecord>> recordsPerDay = AppUpdateRecordUtility.groupByDay(startDate, endDate,
                userRecords);

        // average daily smartphone usage duration
        int numOfDays = 0;
        int appDuration = 0;
        for (ArrayList<AppUpdateRecord> dayRecords : recordsPerDay) {
            appDuration += AppUpdateRecordUtility.sumDurations(dayRecords);
            numOfDays++;
        }
        double averageDailyUsage = (double) appDuration / numOfDays; // seconds

        // average daily gaming duration
        int gameDuration = 0;
        for (ArrayList<AppUpdateRecord> dayRecords : recordsPerDay) {
            for (AppUpdateRecord record : dayRecords) {
                if (record.getAppCategory().equals("Games")) {
                    gameDuration += record.getDuration();
                }
            }
        }
        double averageGameUsage = (double) gameDuration / numOfDays;

        // average phone access frequency
        int accessFrequency = 0;
        for (ArrayList<AppUpdateRecord> dayRecords : recordsPerDay) {
            Collections.sort(dayRecords, new Comparator<AppUpdateRecord>() {
                @Override
                public int compare(AppUpdateRecord o1, AppUpdateRecord o2) {
                    // timestamp ascending
                    return o1.getTimestamp().compareTo(o2.getTimestamp());
                }
            });
            ArrayList<ArrayList<AppUpdateRecord>> recordsPerHour = AppUpdateRecordUtility.groupByHour(dayRecords);

            // compute number of sessions per hour
            for (int h = 0; h < 24; h++) {
                ArrayList<AppUpdateRecord> recordsThisHour = recordsPerHour.get(h);
                int sessionsThisHour = 0;

                for (int i = 1; i < recordsThisHour.size(); i++) {
                    AppUpdateRecord cur = recordsThisHour.get(i);
                    AppUpdateRecord prev = recordsThisHour.get(i - 1);

                    if ((cur.getTimestamp().getTime() - prev.getTimestamp().getTime()) > 120 * 1000) {
                        // NOT a continuous session (more than 120 sec)
                        // i.e. a previous session just ended
                        sessionsThisHour++;
                    }
                }

                // count the last session's ending if there are any sessions at all
                if (sessionsThisHour > 0) {
                    sessionsThisHour++;
                }

                accessFrequency += sessionsThisHour;
            }

        }
        double averageAccessFrequency = (double) accessFrequency / (numOfDays * 24);

        // compute individual categories
        int averageDailyUsageCategory = 0;
        int averageGameUsageCategory = 0;
        int averageAccessFrequencyCategory = 0;

        if (averageDailyUsage < 3 * 60 * 60) {
            averageDailyUsageCategory = 1;
        } else if (averageDailyUsage < 5 * 60 * 60) {
            averageDailyUsageCategory = 2;
        } else {
            averageDailyUsageCategory = 3;
        }

        if (averageGameUsage < 1 * 60 * 60) {
            averageGameUsageCategory = 1;
        } else if (averageGameUsage < 2 * 60 * 60) {
            averageGameUsageCategory = 2;
        } else {
            averageGameUsageCategory = 3;
        }

        if (averageAccessFrequency <= 3) {
            averageAccessFrequencyCategory = 1;
        } else if (averageAccessFrequency <= 5) {
            averageAccessFrequencyCategory = 2;
        } else {
            averageAccessFrequencyCategory = 3;
        }

        int overallIndex = 0;
        if (averageDailyUsageCategory == 3 || averageGameUsageCategory == 3 || averageAccessFrequencyCategory == 3) {
            overallIndex = 3;
        } else if (averageDailyUsageCategory == 1 && averageGameUsageCategory == 1 && averageAccessFrequencyCategory
                == 13) {
            overallIndex = 1;
        } else {
            overallIndex = 2;
        }

        return new OveruseResult(averageDailyUsage, averageGameUsage, averageAccessFrequency, overallIndex);
    }

}
