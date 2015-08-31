package net.cflee.seta.controller;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import net.cflee.seta.dao.AppUpdateDAO;
import net.cflee.seta.dao.LocationUpdateDAO;
import net.cflee.seta.entity.AppUpdateRecord;
import net.cflee.seta.entity.LocationUpdateRecord;
import net.cflee.seta.entity.SocialActivenessResult;
import net.cflee.seta.utility.AppUpdateRecordUtility;
import net.cflee.seta.utility.DateUtility;
import net.cflee.seta.utility.LocationUpdateRecordUtility;

public class SocialActivenessController {

    public static SocialActivenessResult computeReport(Date date, String email, Connection conn) throws SQLException {
        // retrieve all the updates for this user
        // compute a new endDate to be exclusive
        ArrayList<AppUpdateRecord> userAppRecords
                = AppUpdateDAO.retrieveAppUpdates(date, DateUtility.addDays(date, 1), null, null, null, email,
                        conn);

        // filter down to only Social category updates, compute category total usage
        ArrayList<AppUpdateRecord> userSocialRecords = new ArrayList<>();
        for (AppUpdateRecord record : userAppRecords) {
            if (record.getAppCategory().equals("Social")) {
                userSocialRecords.add(record);
            }
        }
        int totalSocialDuration = AppUpdateRecordUtility.sumDurations(userSocialRecords);

        // then group by apps to compute app-specific usage
        HashMap<AppUpdateRecord, Integer> appDurations = new HashMap<>();
        ArrayList<ArrayList<AppUpdateRecord>> userSocialRecordsByApp = AppUpdateRecordUtility.groupByApp(
                userSocialRecords);
        for (ArrayList<AppUpdateRecord> appRecords : userSocialRecordsByApp) {
            int duration = AppUpdateRecordUtility.sumDurations(appRecords);
            appDurations.put(appRecords.get(0), duration);
        }

        // compute alone time vs groups time
        int timeInGroups = 0;
        int timeAlone = 0;
        // obtain location updates
        ArrayList<LocationUpdateRecord> userLocationRecords
                = LocationUpdateDAO.retrieveUserUpdates(date, DateUtility.addDays(date, 1), email, conn);
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

        // each LocationUpdateRecord left now represents one continuous session.
        for (LocationUpdateRecord userSession : userLocationRecords) {
            // start and end period being considered for this session
            Date startDate = userSession.getTimestamp();
            Date endDate = DateUtility.addSeconds(userSession.getTimestamp(), userSession.getDuration());

            // keep track of which seconds in this session are spent with other users
            int[] groupSeconds = new int[userSession.getDuration()];

            // get updates of users with who were present in the semantic place at least 1 sec during this session
            // we need to look at records up to and including 4m 59s (299 sec) behind
            startDate = DateUtility.addMinutes(startDate, -5);
            startDate = DateUtility.addSeconds(startDate, 1);
            ArrayList<LocationUpdateRecord> semanticPlaceVisitorUpdates = LocationUpdateDAO
                    .retrieveSemanticPlaceVisitorUpdates(startDate, endDate, userSession.getSemanticPlace(), email, conn);

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

                    if (recordEndTime.compareTo(endDate) > 0) {
                        // ends after actual end
                        int durationDiff = DateUtility.differenceInSeconds(recordEndTime, endDate);
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
            int sessionTimeInGroups = 0;
            for (int num : groupSeconds) {
                if (num > 0) {
                    sessionTimeInGroups++;
                }
            }
            // rest of time must be alone
            int sessionTimeAlone = userSession.getDuration() - sessionTimeInGroups;

            // accumulate results from this session in total
            timeInGroups += sessionTimeInGroups;
            timeAlone += sessionTimeAlone;
        }

        // pass results back
        return new SocialActivenessResult(totalSocialDuration, appDurations, timeInGroups, timeAlone);
    }

}
