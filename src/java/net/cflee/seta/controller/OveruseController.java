
package net.cflee.seta.controller;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import net.cflee.seta.dao.AppUpdateDAO;
import net.cflee.seta.dao.LocationUpdateDAO;
import net.cflee.seta.entity.AdvancedOveruseResult;
import net.cflee.seta.entity.AppUpdateRecord;
import net.cflee.seta.entity.LocationUpdateRecord;
import net.cflee.seta.entity.OveruseResult;
import net.cflee.seta.utility.AppUpdateRecordUtility;
import net.cflee.seta.utility.DateUtility;
import net.cflee.seta.utility.LocationUpdateRecordUtility;

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

    public static AdvancedOveruseResult computeAdvancedReport(Date startDate, Date endDate, String email,
            Connection conn) throws SQLException {
        // retrieve all the app updates with the filtering
        // compute a new endDate to be exclusive
        ArrayList<AppUpdateRecord> userRecords
                = AppUpdateDAO.retrieveAppUpdates(startDate, DateUtility.addDays(endDate, 1), null, null, null, email,
                        conn);
        // group by day
        ArrayList<ArrayList<AppUpdateRecord>> recordsPerDay = AppUpdateRecordUtility.groupByDay(startDate, endDate,
                userRecords);

        // obtain user's location updates
        ArrayList<LocationUpdateRecord> userLocationRecords
                = LocationUpdateDAO.retrieveUserUpdates(startDate, DateUtility.addDays(endDate, 1), email, conn);
        // sort timestamp ascending
        Collections.sort(userLocationRecords, new Comparator<LocationUpdateRecord>() {
            @Override
            public int compare(LocationUpdateRecord o1, LocationUpdateRecord o2) {
                // timestamp ascending
                return o1.getTimestamp().compareTo(o2.getTimestamp());
            }
        });
        // compress updates into continuous sessions
        // use old-fashioned for loop as we need to access the n+1 record
        LocationUpdateRecordUtility.compressRecords(userLocationRecords);

        int numOfDays = recordsPerDay.size();
        int numOfSeconds = numOfDays * 24 * 60 * 60;

        // ===== =====
        // ===== compute total class time =====
        int[] classTime = new int[numOfSeconds];

        // each LocationUpdateRecord left now represents one continuous session.
        for (LocationUpdateRecord userSession : userLocationRecords) {
            // start and end period being considered for this session
            Date sessionStartDate = userSession.getTimestamp();

            // check if this continuous session is considered class time
            // TODO: how to correctly determine it's a seminar room?
            if (userSession.getDuration() >= 60 * 60 && userSession.getSemanticPlace().contains("SR")) {
                // if yes, increase classTime for that period
                int sessionTimestampOffset = DateUtility.differenceInSeconds(sessionStartDate, startDate);
                for (int s = sessionTimestampOffset; s >= 0 && s < sessionTimestampOffset + userSession.getDuration()
                        && s < classTime.length; s++) {
                    classTime[s] = 1;
                }
            }
        }

        int totalClassTime = 0;
        for (int i = 0; i < numOfSeconds; i++) {
            if (classTime[i] == 1) {
                totalClassTime++;
            }
        }

        // ===== compute total group time =====
        int[] groupTime = new int[numOfSeconds];

        // each LocationUpdateRecord left now represents one continuous session.
        for (LocationUpdateRecord userSession : userLocationRecords) {
            // start and end period being considered for this session
            Date sessionStartDate = userSession.getTimestamp();
            Date sessionEndDate = DateUtility.addSeconds(userSession.getTimestamp(), userSession.getDuration());

            // keep track of which seconds in this session are spent with how many other users
            int[] groupSeconds = new int[userSession.getDuration()];

            // get updates of users with who were present in the semantic place at least 1 sec during this session
            // we need to look at records up to and including 4m 59s (299 sec) behind
            sessionStartDate = DateUtility.addMinutes(sessionStartDate, -5);
            sessionStartDate = DateUtility.addSeconds(sessionStartDate, 1);
            ArrayList<LocationUpdateRecord> semanticPlaceVisitorUpdates = LocationUpdateDAO
                    .retrieveSemanticPlaceVisitorUpdates(sessionStartDate, sessionEndDate, userSession
                            .getSemanticPlace(), email, conn);

            // sort by users and timestamp, group by user
            Collections.sort(semanticPlaceVisitorUpdates, new Comparator<LocationUpdateRecord>() {
                @Override
                public int compare(LocationUpdateRecord o1, LocationUpdateRecord o2) {
                    int compare = o1.getMacAddress().compareTo(o2.getMacAddress());
                    if (compare != 0) {
                        return compare;
                    }

                    return o1.getTimestamp().compareTo(o2.getTimestamp());
                }
            });
            ArrayList<ArrayList<LocationUpdateRecord>> semanticPlaceVisitorUpdatesByUser
                    = LocationUpdateRecordUtility.groupByUser(semanticPlaceVisitorUpdates);

            // compress location updates for each user
            for (ArrayList<LocationUpdateRecord> records : semanticPlaceVisitorUpdatesByUser) {
                // adjust updates to factor in the actual start datetime of userSession.getTimestamp()
                // discard if each update's expiry is actually before actual start
                // increase each update's start timestamp to actual start if it crosses
                //
                // limit end (as represented by duration) to the actual end
                for (int i = 0; i < records.size(); i++) {
                    LocationUpdateRecord record = records.get(i);
                    Date recordEndTime = DateUtility.addSeconds(record.getTimestamp(), record.getDuration());

                    if (recordEndTime.compareTo(userSession.getTimestamp()) <= 0) {
                        // ends on or before actual start, delete
                        records.remove(i);
                        i--;
                    } else if (record.getTimestamp().compareTo(userSession.getTimestamp()) < 0) {
                        // ends after actual start, but starts before actual start
                        int durationDiff = DateUtility.differenceInSeconds(userSession.getTimestamp(), record
                                .getTimestamp());
                        record.setTimestamp(userSession.getTimestamp());
                        record.setDuration(record.getDuration() - durationDiff);
                    }

                    if (recordEndTime.compareTo(sessionEndDate) > 0) {
                        // ends after actual end
                        int durationDiff = DateUtility.differenceInSeconds(recordEndTime, sessionEndDate);
                        record.setDuration(record.getDuration() - durationDiff);
                    }
                }

                // compress remaining updates
                LocationUpdateRecordUtility.compressRecords(records);
            }

            // check for >= 5 min continuously
            for (ArrayList<LocationUpdateRecord> records : semanticPlaceVisitorUpdatesByUser) {
                for (LocationUpdateRecord record : records) {
                    if (record.getDuration() >= 300) {
                        // if yes, increase groupSeconds for that period
                        int startSecond = DateUtility.differenceInSeconds(record.getTimestamp(),
                                userSession.getTimestamp());
                        for (int s = startSecond; s >= 0 && s < startSecond + record.getDuration()
                                && s < groupSeconds.length; s++) {
                            groupSeconds[s]++;
                        }
                    }
                }
            }

            // count how many seconds in groups
            int sessionTimestampOffset = DateUtility.differenceInSeconds(sessionStartDate, startDate);
            for (int s = 0; s < groupSeconds.length; s++) {
                int num = groupSeconds[s];
                if (num > 0 && num < 3) {
                    groupTime[sessionTimestampOffset + s] = 1;
                }
            }
        }

        int totalGroupTime = 0;
        for (int i = 0; i < numOfSeconds; i++) {
            if (groupTime[i] == 1) {
                totalGroupTime++;
            }
        }

        // ===== compute total non-productive smartphone usage time =====
        int[] smartphoneTime = new int[numOfSeconds];

        for (AppUpdateRecord record : userRecords) {
            if (!record.getAppCategory().equals("Information") && !record.getAppCategory().equals("Education")) {
                // unproductive smartphone usage time
                // if yes, increase classTime for that period
                int sessionTimestampOffset = DateUtility.differenceInSeconds(record.getTimestamp(), startDate);
                for (int s = sessionTimestampOffset; s >= 0 && s < sessionTimestampOffset + record.getDuration()
                        && s < classTime.length; s++) {
                    smartphoneTime[s] = 1;
                }
            }
        }

        int totalSmartphoneTime = 0;
        for (int i = 0; i < numOfSeconds; i++) {
            if (smartphoneTime[i] == 1) {
                totalSmartphoneTime++;
            }
        }

        // ===== =====

        // compute number of seconds that's the union of class and group time
        int classAndGroupTime = 0;
        for (int i = 0; i < numOfSeconds; i++) {
            if (classTime[i] == 1 || groupTime[i] == 1) {
                classAndGroupTime++;
            }
        }

        // compute number of seconds that's intersect of (non-productive smartphone usage) and (class union group)
        int distractingClassAndGroupTime = 0;
        for (int i = 0; i < numOfSeconds; i++) {
            if (smartphoneTime[i] == 1 && (classTime[i] == 1 || groupTime[i] == 1)) {
                distractingClassAndGroupTime++;
            }
        }

        // return results
        return new AdvancedOveruseResult(totalClassTime, totalGroupTime, totalSmartphoneTime, classAndGroupTime,
                distractingClassAndGroupTime, numOfDays);
    }

}
