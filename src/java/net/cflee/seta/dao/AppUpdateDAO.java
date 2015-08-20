package net.cflee.seta.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import net.cflee.seta.entity.AppUpdate;
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
