package net.cflee.seta.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import net.cflee.seta.entity.AppUpdate;
import net.cflee.seta.entity.AppUpdateRecord;
import net.cflee.seta.utility.ConnectionManager;

/**
 * DAO that is responsible for CRUD functions performed in the app update * table
 */
public class AppUpdateDAO {

    private static final String INSERT = "INSERT INTO app_update "
            + "(mac_address, app_id, time_stamp, row_number) "
            + "VALUES (?, ?, ?, ?)";
    private static final String TRUNCATE = "TRUNCATE TABLE app_update";
    private static final String CHECK_FOR_EXISTING_RECORD = "SELECT row_number FROM app_update "
            + "WHERE mac_address = ? "
            + "AND time_stamp = ?";
    private static final String UPDATE_APP_ID = "UPDATE app_update "
            + "SET app_id = ?, "
            + "row_number = ? "
            + "WHERE mac_address = ? "
            + "AND time_stamp = ? ";
    private static final String SELECT_JOIN_AND_FILTER = "SELECT "
            + "app_update.time_stamp, app_update.mac_address, `user`.name, `user`.gender, `user`.school, `user`.year, "
            + "`user`.email, app_update.app_id, app.app_name, app.app_category "
            + "FROM app_update "
            + "INNER JOIN app ON app_update.app_id = app.app_id "
            + "INNER JOIN `user` ON app_update.mac_address = `user`.mac_address "
            + "WHERE app_update.time_stamp >= ? "
            + "AND app_update.time_stamp < ? "
            + "AND `user`.gender LIKE ? "
            + "AND `user`.school LIKE ? "
            + "AND `user`.year LIKE ? ";
    private static final String CLEAR_ROW_NUMBERS = "UPDATE app_update SET row_number = 0";

    /**
     * Check if a matching AppUpdate record exists. AppUpdate records * are a match if the mac address and timestamp
     * match an existing record in the database.
     *
     * @param appUpdate AppUpdate object with macAddress and timestamp to be checked for
     * @param conn connection to the database
     * @return positive integer of row number if there is an existing record and it was from the current file, 0 if
     * there is an existing record and not from current file, and -1 if there is no existing record
     * @throws SQLException
     */
    public static int checkForExistingRecord(AppUpdate appUpdate, Connection conn) throws SQLException {
        PreparedStatement psmt = null;
        ResultSet rs = null;
        try {
            String macAddress = appUpdate.getMacAddress();
            Date date = appUpdate.getTimestamp();
            Timestamp timeStamp = new Timestamp(date.getTime());

            psmt = conn.prepareStatement(CHECK_FOR_EXISTING_RECORD);
            psmt.setString(1, macAddress);
            psmt.setTimestamp(2, timeStamp);
            rs = psmt.executeQuery();
            if (rs.next()) {
                int rowNo = rs.getInt(1);
                return rowNo;
            } else {
                return -1;
            }
        } finally {
            ConnectionManager.close(null, psmt, rs);
        }
    }

    /**
     * Insert the app update into the app update table
     *
     * @param appUpdate the app update object to be inserted
     * @param conn connection to the database
     * @throws java.sql.SQLException
     */
    public static void insert(AppUpdate appUpdate, Connection conn) throws SQLException {
        PreparedStatement psmt = null;

        try {
            psmt = conn.prepareStatement(INSERT);

            psmt.setString(1, appUpdate.getMacAddress());
            Date utilDate = appUpdate.getTimestamp();
            Timestamp timeStamp = new Timestamp(utilDate.getTime());
            psmt.setInt(2, appUpdate.getAppId());
            psmt.setTimestamp(3, timeStamp);
            psmt.setInt(4, appUpdate.getRowNo());
            psmt.executeUpdate();
        } finally {
            ConnectionManager.close(null, psmt, null);
        }
    }

    /**
     * Update the app ID and row number
     *
     * @param appUpdate
     * @param conn
     * @throws SQLException
     */
    public static void updateAppId(AppUpdate appUpdate, Connection conn) throws SQLException {
        PreparedStatement psmt = null;

        try {
            psmt = conn.prepareStatement(UPDATE_APP_ID);

            psmt.setInt(1, appUpdate.getAppId());
            psmt.setInt(2, appUpdate.getRowNo());
            psmt.setString(3, appUpdate.getMacAddress());
            psmt.setTimestamp(4, new Timestamp(appUpdate.getTimestamp().getTime()));
            psmt.executeUpdate();
        } finally {
            ConnectionManager.close(null, psmt, null);
        }
    }

    public static ArrayList<AppUpdateRecord> retrieveAppUpdates(Date startDate, Date endDate, Connection conn)
            throws SQLException {
        return retrieveAppUpdates(startDate, endDate, null, null, null, null, conn);
    }

    /**
     * Retrieve an ArrayList of AppUpdateRecords, which are joined representations of AppUpdates, but with all the
     * accompanying attributes of the User and the App, to facilitate in-app pivoting.
     *
     * Use the wrapper Integer and Character classes so that we can just use nulls to detect non-filtering-condition.
     *
     * Not allowed to filter by app name or app category because that will yield incorrect durations, as subsequent or
     * intervening use of other apps by the user in-between the filtered app name or category won't be included in the
     * SQL results.
     *
     * @param startDate start date/time, inclusive
     * @param endDate end date/time, exclusive
     * @param year optional, set to null to not-filter by year
     * @param gender optional, set to null to not-filter by year
     * @param school optional, set to null to not-filter by year
     * @param conn connection to the database
     * @return ArrayList of AppUpdateRecords
     * @throws SQLException
     */
    public static ArrayList<AppUpdateRecord> retrieveAppUpdates(Date startDate, Date endDate, Integer year,
            Character gender, String school, Connection conn) throws SQLException {
        return retrieveAppUpdates(startDate, endDate, year, gender, school, null, conn);
    }

    public static ArrayList<AppUpdateRecord> retrieveAppUpdates(Date startDate, Date endDate, Integer year,
            Character gender, String school, String username, Connection conn) throws SQLException {
        PreparedStatement psmt = null;
        ResultSet rs = null;
        ArrayList<AppUpdateRecord> results = new ArrayList<>();

        try {
            psmt = conn.prepareStatement(SELECT_JOIN_AND_FILTER);
            // mandatory parameters
            psmt.setTimestamp(1, new Timestamp(startDate.getTime()));
            psmt.setTimestamp(2, new Timestamp(endDate.getTime()));
            // optional filters. use % wildcard where necessary
            // TODO: defend against wildcards in the optional String parameters
            if (gender == null) {
                psmt.setString(3, "%");
            } else {
                psmt.setString(3, String.valueOf(gender));
            }
            if (school == null) {
                psmt.setString(4, "%");
            } else {
                psmt.setString(4, school);
            }
            if (year == null) {
                psmt.setString(5, "%");
            } else {
                psmt.setInt(5, year);
            }
            rs = psmt.executeQuery();

            // pack into AppUpdateRecords and insert into ArrayList
            while (rs.next()) {
                AppUpdateRecord record = new AppUpdateRecord(new Date(rs.getTimestamp(1).getTime()), rs.getString(2),
                        rs.getString(3), rs.getString(4).charAt(0), rs.getString(5), rs.getInt(6),
                        rs.getString(7), rs.getInt(8), rs.getString(9), rs.getString(10));
                results.add(record);
            }

            // compute the durations for each record, which are 10 seconds by default in their constructor
            // use old-fashioned for loop as we need to access the n+1 record
            for (int i = 0; i < results.size(); i++) {
                AppUpdateRecord current = results.get(i);

                // obtain the next record if it doesn't overrun
                // if there's no next record, then there's nothing to do
                if (i + 1 < results.size()) {
                    AppUpdateRecord next = results.get(i + 1);

                    // check if the next record still belongs to this user
                    // nothing to do if it isn't
                    if (current.getMacAddress().equals(next.getMacAddress())) {
                        // same user, now compute the difference in seconds
                        // to see if this next AppUpdate is in the same phone use session
                        // divide by 1000 as the timestamp is in milliseconds
                        int difference = (int) ((next.getTimestamp().getTime() - current.getTimestamp().getTime())
                                / 1000);
                        if (difference <= 120) {
                            // same phone use session, so update this record's duration
                            current.setDuration(difference);
                        }
                    }
                }
            }
        } finally {
            ConnectionManager.close(null, psmt, rs);
        }

        return results;
    }

    public static void clearRowNumberRecords(Connection conn) throws SQLException {
        PreparedStatement psmt = null;

        try {
            psmt = conn.prepareStatement(CLEAR_ROW_NUMBERS);
            psmt.executeUpdate();
        } finally {
            ConnectionManager.close(null, psmt, null);
        }
    }

    /**
     * Create all the data in app update table
     *
     * @param conn connection to the database
     * @throws java.sql.SQLException
     */
    public static void clear(Connection conn) throws SQLException {
        PreparedStatement psmt = null;
        try {
            psmt = conn.prepareStatement(TRUNCATE);
            psmt.executeUpdate();
        } finally {
            ConnectionManager.close(null, psmt, null);
        }
    }

}
