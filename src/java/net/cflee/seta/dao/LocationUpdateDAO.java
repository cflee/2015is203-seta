package net.cflee.seta.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import net.cflee.seta.entity.LocationUpdate;
import net.cflee.seta.utility.ConnectionManager;

/**
 * DAO that is responsible for CRUD functions performed in the location update table
 */
public class LocationUpdateDAO {

    private static final String INSERT = "INSERT INTO location_update "
            + "(mac_address, location_id, time_stamp, row_number) "
            + "VALUES (?, ?, ?, ?)";
    private static final String DELETE = "DELETE FROM location_update "
            + "WHERE mac_address = ? "
            + "AND time_stamp = ? ";
    private static final String TRUNCATE = "TRUNCATE TABLE location_update";
    private static final String CHECK_FOR_EXISTING_RECORD = "SELECT row_number FROM location_update "
            + "WHERE mac_address = ? "
            + "AND time_stamp = ?";
    private static final String UPDATE_LOCATION_ID = "UPDATE location_update "
            + "SET location_id = ?, "
            + "row_number = ? "
            + "WHERE mac_address = ? "
            + "AND time_stamp = ? ";
    private static final String CLEAR_ROW_NUMBERS = "UPDATE location_update SET row_number = 0";

    /**
     * Check if a matching LocationUpdate record exists. LocationUpdate records are a match if the mac address and
     * timestamp match an existing record in the database.
     *
     * @param locationUpdate LocationUpdate object with macAddress and timestamp to be checked for
     * @param conn connection to the database
     * @return positive integer of row number if there is an existing record and it was from the current file, 0 if
     * there is an existing record and not from current file, and -1 if there is no existing record
     * @throws SQLException
     */
    public static int checkForExistingRecord(LocationUpdate locationUpdate, Connection conn) throws SQLException {
        PreparedStatement psmt = null;
        ResultSet rs = null;
        try {
            String macAddress = locationUpdate.getMacAddress();
            Date date = locationUpdate.getTimestamp();
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
     * Insert the location update into the location update table
     *
     * @param locationUpdate the location update object to be inserted
     * @param conn connection to the database
     * @throws java.sql.SQLException
     */
    public static void insert(LocationUpdate locationUpdate, Connection conn) throws SQLException {
        PreparedStatement psmt = null;

        try {
            psmt = conn.prepareStatement(INSERT);

            psmt.setString(1, locationUpdate.getMacAddress());
            Date utilDate = locationUpdate.getTimestamp();
            Timestamp timeStamp = new Timestamp(utilDate.getTime());
            psmt.setInt(2, locationUpdate.getLocationId());
            psmt.setTimestamp(3, timeStamp);
            psmt.setInt(4, locationUpdate.getRowNo());
            psmt.executeUpdate();
        } finally {
            ConnectionManager.close(null, psmt, null);
        }
    }

    /**
     * Delete the location update from the location update table
     *
     * @param locationUpdate the location update object to be inserted
     * @param conn connection to the database
     * @throws java.sql.SQLException
     */
    public static void delete(LocationUpdate locationUpdate, Connection conn) throws SQLException {
        PreparedStatement psmt = null;

        try {
            psmt = conn.prepareStatement(DELETE);

            psmt.setString(1, locationUpdate.getMacAddress());
            Date utilDate = locationUpdate.getTimestamp();
            Timestamp timeStamp = new Timestamp(utilDate.getTime());
            // TODO: check if need to factor this in for safety
            //psmt.setInt(2, locationUpdate.getLocationId());
            psmt.setTimestamp(2, timeStamp);
            psmt.executeUpdate();
        } finally {
            ConnectionManager.close(null, psmt, null);
        }
    }

    /**
     * Update the location ID and row number
     *
     * @param locationUpdate
     * @param conn
     * @throws SQLException
     */
    public static void updateLocationId(LocationUpdate locationUpdate, Connection conn) throws SQLException {
        PreparedStatement psmt = null;

        try {
            psmt = conn.prepareStatement(UPDATE_LOCATION_ID);

            psmt.setInt(1, locationUpdate.getLocationId());
            psmt.setInt(2, locationUpdate.getRowNo());
            psmt.setString(3, locationUpdate.getMacAddress());
            psmt.setTimestamp(4, new Timestamp(locationUpdate.getTimestamp().getTime()));
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
     * Create all the data in location update table
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
