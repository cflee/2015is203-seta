package net.cflee.seta.controller;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
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
                = AppUpdateDAO.retrieveAppUpdates(startDate, DateUtility.addDays(endDate, 1), conn);

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
                = AppUpdateDAO.retrieveAppUpdates(startDate, DateUtility.addDays(endDate, 1), conn);
        // sort by app category ascending
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

    /**
     *
     * @param startDate start date, inclusive
     * @param endDate end date, inclusive
     * @param params ArrayList of up to 3 Strings, one of (year, gender, school), in breakdown order
     * @param conn
     * @return
     * @throws SQLException
     */
    public static LinkedHashMap<String, BasicAppUsageTimeCategoryResult> computeTimeCategoryDemographics(Date startDate,
            Date endDate, ArrayList<String> params, Connection conn) throws SQLException {
        // end state: list of BasicAppUsageTimeCategoryResult with indicated demographics-combination
        LinkedHashMap<String, BasicAppUsageTimeCategoryResult> results = new LinkedHashMap<>();

        // using arrays because they're easier to initialise in one line
        // also because Arrays.asList only returns a List, not an ArrayList, so unordered
        int[] validYears = {2011, 2012, 2013, 2014, 2015};
        char[] validGenders = {'F', 'M'};
        String[] validSchools = {"accountancy", "business", "economics", "law", "sis", "socsc"};

        // retrieve all the updates with the filtering
        // compute a new endDate to be exclusive
        ArrayList<AppUpdateRecord> records
                = AppUpdateDAO.retrieveAppUpdates(startDate, DateUtility.addDays(endDate, 1), conn);

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

        HashMap<AppUpdateRecord, Double> userDailyAverages = new HashMap<>();
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

            // compute daily average
            double dailyAverage = dayDuration / numOfDays;
            userDailyAverages.put(userRecords.get(0), dailyAverage);
        }

        // group computed results by each param, until all params are done
        // NOTE: for this section, use LinkedHashMaps as we will later initialise buckets in the correct ascending order
        // and subsequently directly display in that order. order is defined earlier in the valid* arrays.
        Iterator<String> paramsIter = params.iterator();
        // map of params-so-far
        HashMap<String, HashMap<AppUpdateRecord, Double>> groupedParams = new LinkedHashMap<>();
        groupedParams.put("", userDailyAverages);
        while (paramsIter.hasNext()) {
            String param = paramsIter.next();

            // for each prev-param to user-daily average pair, group by next-param
            HashMap<String, HashMap<AppUpdateRecord, Double>> newGroupedParams = new LinkedHashMap<>();
            for (Map.Entry<String, HashMap<AppUpdateRecord, Double>> entry : groupedParams.entrySet()) {
                // group by next-param
                HashMap<String, HashMap<AppUpdateRecord, Double>> groupedMap = new LinkedHashMap<>();

                String prefix = entry.getKey();
                if (prefix.length() != 0) {
                    // it's not the special case of first round ""
                    prefix = prefix + "-";
                }

                // initialise all buckets, to ensure that they get reflected as empty later
                if (param.equals("year")) {
                    for (Integer year : validYears) {
                        groupedMap.put(prefix + year, new HashMap<AppUpdateRecord, Double>());
                    }
                } else if (param.equals("gender")) {
                    for (Character gender : validGenders) {
                        groupedMap.put(prefix + gender, new HashMap<AppUpdateRecord, Double>());
                    }
                } else { // school
                    for (String school : validSchools) {
                        groupedMap.put(prefix + school, new HashMap<AppUpdateRecord, Double>());
                    }
                }

                for (Map.Entry<AppUpdateRecord, Double> userDailyAverage : entry.getValue().entrySet()) {
                    // figure out the paramKey
                    AppUpdateRecord record = userDailyAverage.getKey();
                    String paramKey;
                    if (param.equals("year")) {
                        paramKey = String.valueOf(record.getUserYear());
                    } else if (param.equals("gender")) {
                        paramKey = String.valueOf(record.getUserGender());
                    } else { // school
                        paramKey = record.getUserSchool();
                    }
                    // attach the prefix if this is not the first param
                    paramKey = prefix + paramKey;

                    // add to the resulting map
                    groupedMap.get(paramKey).put(userDailyAverage.getKey(), userDailyAverage.getValue());
                }

                newGroupedParams.putAll(groupedMap);
            }
            groupedParams = newGroupedParams;

            // end if no more params
            if (!paramsIter.hasNext()) {
                for (Map.Entry<String, HashMap<AppUpdateRecord, Double>> entry : newGroupedParams.entrySet()) {
                    String key = entry.getKey();
                    BasicAppUsageTimeCategoryResult result = new BasicAppUsageTimeCategoryResult();
                    for (Map.Entry<AppUpdateRecord, Double> user : entry.getValue().entrySet()) {
                        result.addAppUsageTime(user.getValue());
                    }
                    results.put(key, result);
                }
                return results;
            }
        }

        return results;
    }

    public static LinkedHashMap<Integer, Double> computeDiurnal(Date date, Integer year, Character gender,
            String school, Connection conn) throws SQLException {
        LinkedHashMap<Integer, Double> results = new LinkedHashMap<>();

        // retrieve all the updates with the filtering
        // compute a new endDate to be exclusive
        ArrayList<AppUpdateRecord> records
                = AppUpdateDAO.retrieveAppUpdates(date, DateUtility.addDays(date, 1), year, gender, school, conn);

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

        // number of users who have at least one app update throughout the entire day
        int totalNumOfUsers = recordsPerUser.size();

        // sort by timestamp ascending, then group by hour
        Collections.sort(records, new Comparator<AppUpdateRecord>() {
            @Override
            public int compare(AppUpdateRecord o1, AppUpdateRecord o2) {
                return o1.getTimestamp().compareTo(o2.getTimestamp());
            }
        });
        ArrayList<ArrayList<AppUpdateRecord>> recordsPerHour = AppUpdateRecordUtility.groupByHour(records);

        // compute average app usage time (in minutes) per-user for each hour
        for (int h = 0; h < 24; h++) {
            // average app usage time across all users, in minutes
            double averageHourlyDuration = (double) AppUpdateRecordUtility.sumDurations(recordsPerHour.get(h))
                    / totalNumOfUsers / 60;
            results.put(h, averageHourlyDuration);
        }

        return results;
    }

}
