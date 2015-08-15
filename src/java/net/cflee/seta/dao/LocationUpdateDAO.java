package net.cflee.seta.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import net.cflee.seta.entity.HeatmapResult;
import net.cflee.seta.entity.LocationUpdate;
import net.cflee.seta.entity.TopKResult;
import net.cflee.seta.utility.ConnectionManager;
import net.cflee.seta.utility.DateUtility;

/**
 * DAO that is responsible for CRUD functions performed in the location update
 * table
 */
public class LocationUpdateDAO {

    private static final String INSERT = "INSERT INTO location_update "
            + "(mac_address, location_id, time_stamp, row_number) "
            + "VALUES (?, ?, ?, ?)";
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
    private static final String SELECT_ALL = "SELECT mac_address, location_id, time_stamp FROM location_update";
    private static final String HEATMAP_QUERY = "SELECT COUNT(location_update.mac_address) AS num_of_people, "
            + "location.semantic_place "
            + "FROM location_update "
            + "INNER JOIN ("
            + "SELECT mac_address, MAX(time_stamp) AS latest_timestamp "
            + "FROM location_update "
            + "WHERE time_stamp > ? AND time_stamp <= ?  "
            + "GROUP BY mac_address) AS T "
            + "ON location_update.mac_address = T.mac_address "
            + "AND location_update.time_stamp = T.latest_timestamp "
            + "RIGHT OUTER JOIN location "
            + "ON location_update.location_id = location.location_id "
            + "WHERE location.semantic_place LIKE ? "
            + "GROUP BY location.semantic_place "
            + "ORDER BY location.semantic_place ASC";
    private static final String TOP_K_POPULAR_PLACES_QUERY = "SELECT location.semantic_place, "
            + "COUNT(location_update.mac_address) AS num_of_people "
            + "FROM location_update, "
            + "location,"
            + "(SELECT mac_address, MAX(time_stamp) AS latest_timestamp "
            + "FROM location_update "
            + "WHERE time_stamp > ? AND time_stamp <= ?  "
            + "GROUP BY mac_address) AS T "
            + "WHERE location_update.location_id = location.location_id  "
            + "AND location_update.mac_address = T.mac_address "
            + "AND location_update.time_stamp = T.latest_timestamp "
            + "GROUP BY location.semantic_place "
            + "ORDER BY num_of_people DESC";
    private static final String TOP_K_NEXT_PLACES_BEFORE_QUERY_TIME = "SELECT LU.mac_address, LU.location_id, LU.time_stamp "
            + "FROM location_update LU "
            + "INNER JOIN (SELECT mac_address, "
            + "MAX(time_stamp) AS latest_timestamp "
            + "FROM location_update "
            + "WHERE time_stamp > ? AND time_stamp <= ? "
            + "GROUP BY mac_address) AS T "
            + "ON LU.mac_address = T.mac_address "
            + "AND LU.time_stamp = T.latest_timestamp "
            + "INNER JOIN location ON LU.location_id = location.location_id "
            + "WHERE location.semantic_place LIKE ?";
    private static final String TOP_K_NEXT_PLACES_AFTER_QUERY_TIME = "SELECT locationupdate.mac_address, locationupdate.location_id, locationupdate.time_stamp "
            + "FROM location_update locationupdate "
            + "WHERE locationupdate.time_stamp > ? "
            + "AND locationupdate.time_stamp <= ? "
            + "AND locationupdate.mac_address "
            + "IN (SELECT lu.mac_address "
            + "FROM location_update lu "
            + "INNER JOIN location l "
            + "ON l.location_id = lu.location_id "
            + "WHERE l.semantic_place = ? AND lu.time_stamp "
            + "> ? AND lu.time_stamp <= ? "
            + "GROUP BY lu.mac_address) "
            + "ORDER BY locationupdate.time_stamp DESC ";
    private static final String TOP_K_COMPANION_QUERY = "SELECT LU.mac_address, location_id, time_stamp "
            + "FROM location_update LU "
            + "LEFT OUTER JOIN user U ON U.mac_address = LU.mac_address "
            + "WHERE time_stamp > ? AND time_stamp <= ? "
            + "ORDER BY time_stamp ASC";
    private static final String SELECT_ALL_MACADDRESS = "SELECT DISTINCT mac_address"
            + " FROM location_update";
    private static final String SELECT_ALL_DISTINCT_MACADDRESS = "SELECT DISTINCT mac_address"
            + " FROM location_update"
            + " WHERE time_stamp <= ?"
            + " AND time_stamp > ?";
    private static final String SELECT_ALL_UPDATES_FOR_MACADDRESS = "SELECT time_stamp FROM location_update "
            + "WHERE mac_address = ? ";

    /**
     * Check if a matching LocationUpdate record exists. LocationUpdate records
     * are a match if the mac address and timestamp match an existing record in
     * the database.
     *
     * @param locationUpdate LocationUpdate object with macAddress and timestamp
     * to be checked for
     * @param conn connection to the database
     * @return positive integer of row number if there is an existing record and
     * it was from the current file, 0 if there is an existing record and not
     * from current file, and -1 if there is no existing record
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

    /**
     * Retrieve the list of location updates
     *
     * @param conn connection to the database
     * @return list of location updates
     * @throws java.sql.SQLException
     */
    public static ArrayList<LocationUpdate> retrieveAll(Connection conn) throws SQLException {

        PreparedStatement psmt = null;
        ResultSet rs = null;
        ArrayList<LocationUpdate> locationUpdateList = new ArrayList<LocationUpdate>();

        try {

            psmt = conn.prepareStatement(SELECT_ALL);
            rs = psmt.executeQuery();
            while (rs.next()) {
                String macAddress = rs.getString(1);
                Date timestamp = rs.getTimestamp(2);
                int locationId = rs.getInt(3);
                LocationUpdate locationUpdate = new LocationUpdate(macAddress, timestamp, locationId);
                locationUpdateList.add(locationUpdate);
            }
        } finally {
            ConnectionManager.close(null, psmt, rs);
        }
        return locationUpdateList;
    }

    /**
     * Retrieve the list of Heatmap result based on the floor and specified date
     *
     * @param floor user specified level
     * @param chosenDate user specified date
     * @param conn connection to the database
     * @return the list of heatmap results
     * @throws SQLException
     */
    public static ArrayList<HeatmapResult> getHeatmapResult(int floor, Date chosenDate, Connection conn) throws SQLException {
        Date startDate = DateUtility.addMinutes(chosenDate, -15);

        String level = null;

        if (floor == 0) {
            level = "SMUSISB1";
        } else {
            level = "SMUSISL" + floor;
        }

        PreparedStatement psmt = null;
        ResultSet rs = null;
        ArrayList<HeatmapResult> heatmapResultList = new ArrayList<HeatmapResult>();

        try {

            psmt = conn.prepareStatement(HEATMAP_QUERY);
            psmt.setTimestamp(1, new Timestamp(startDate.getTime()));
            psmt.setTimestamp(2, new Timestamp(chosenDate.getTime()));
            psmt.setString(3, level + "%");
            rs = psmt.executeQuery();

            while (rs.next()) {
                int numOfPeople = rs.getInt(1);
                String semanticPlace = rs.getString(2);
                HeatmapResult result = new HeatmapResult(semanticPlace, numOfPeople);
                heatmapResultList.add(result);
            }
        } finally {
            ConnectionManager.close(null, psmt, rs);
        }
        return heatmapResultList;
    }

    /**
     * Retrieve the top K popular places based on user's input k value and
     * datetime
     *
     * @param conn connection to the database
     * @param fifteenMinutesBack 15 mins before the user specified time
     * @param userSpecifiedDate user specified time
     * @return the arraylist of top k popular places
     * @throws java.sql.SQLException
     */
    public static ArrayList<TopKResult> getTopKPopularPlaces(Date userSpecifiedDate, Date fifteenMinutesBack, Connection conn) throws SQLException {

        PreparedStatement psmt = null;
        ResultSet rs = null;
        ArrayList<TopKResult> results = new ArrayList<TopKResult>();

        try {
            psmt = conn.prepareStatement(TOP_K_POPULAR_PLACES_QUERY);
            //Convert to java.util.date to java.sql.timestamp in a long format
            psmt.setTimestamp(1, new java.sql.Timestamp(fifteenMinutesBack.getTime()));
            psmt.setTimestamp(2, new java.sql.Timestamp(userSpecifiedDate.getTime()));
            rs = psmt.executeQuery();
            while (rs.next()) {
                results.add(new TopKResult(rs.getString(1), rs.getInt(2)));
            }
        } finally {
            ConnectionManager.close(null, psmt, rs);
        }
        return results;
    }

    /**
     * Retrieve the users in SIS building 15minutes before the query time
     *
     * @param conn connection to the database
     * @param userSpecifiedSemanticPlace user specified location
     * @param userSpecifiedDate user specified time
     * @return the list of users
     * @throws java.sql.SQLException
     */
    public static ArrayList<LocationUpdate> getUsersInSISBeforeQueryTime(Date userSpecifiedDate, String userSpecifiedSemanticPlace, Connection conn) throws SQLException {

        PreparedStatement psmt = null;
        ResultSet rs = null;
        ArrayList<LocationUpdate> previousFifteenMinutesUsers = new ArrayList<LocationUpdate>();
        Date previousFifteenDMinutes = DateUtility.addMinutes(userSpecifiedDate, -15);

        try {
            psmt = conn.prepareStatement(TOP_K_NEXT_PLACES_BEFORE_QUERY_TIME);
            psmt.setTimestamp(1, new java.sql.Timestamp(previousFifteenDMinutes.getTime()));
            psmt.setTimestamp(2, new java.sql.Timestamp(userSpecifiedDate.getTime()));
            psmt.setString(3, userSpecifiedSemanticPlace);
            rs = psmt.executeQuery();
            while (rs.next()) {
                previousFifteenMinutesUsers.add(new LocationUpdate(rs.getString(1), rs.getTimestamp(3), rs.getInt(2)));
            }
        } finally {
            ConnectionManager.close(null, psmt, rs);
        }
        return previousFifteenMinutesUsers;
    }

    /**
     * Retrieve users in SIS building 15mins after query time
     *
     * @param conn connection to database
     * @param userSpecifiedSemanticPlace user specified time
     * @param userSpecifiedDate user specified date
     * @return the list of users
     * @throws java.sql.SQLException
     */
    public static ArrayList<LocationUpdate> getUsersInSISAfterQueryTime(Date userSpecifiedDate, String userSpecifiedSemanticPlace, Connection conn) throws SQLException {

        PreparedStatement psmt = null;
        ResultSet rs = null;
        ArrayList<LocationUpdate> afterFifteenMinutesUsers = new ArrayList<LocationUpdate>();
        Date previousFifteenMinutes = DateUtility.addMinutes(userSpecifiedDate, -15);
        Date afterFifteenMinutes = DateUtility.addMinutes(userSpecifiedDate, 15);

        try {
            psmt = conn.prepareStatement(TOP_K_NEXT_PLACES_AFTER_QUERY_TIME);
            psmt.setTimestamp(1, new java.sql.Timestamp(userSpecifiedDate.getTime()));
            psmt.setTimestamp(2, new java.sql.Timestamp(afterFifteenMinutes.getTime()));
            psmt.setString(3, userSpecifiedSemanticPlace);
            psmt.setTimestamp(4, new java.sql.Timestamp(previousFifteenMinutes.getTime()));
            psmt.setTimestamp(5, new java.sql.Timestamp(userSpecifiedDate.getTime()));
            rs = psmt.executeQuery();
            while (rs.next()) {
                afterFifteenMinutesUsers.add(new LocationUpdate(rs.getString(1), rs.getTimestamp(3), rs.getInt(2)));
            }
        } finally {
            ConnectionManager.close(null, psmt, rs);
        }
        return afterFifteenMinutesUsers;
    }

    /**
     * Retrieve all the location updates from 15 mins before query time to query
     * time
     *
     * @param startDate
     * @param endDate
     * @param conn
     * @return users
     * @throws SQLException
     */
    public static ArrayList<LocationUpdate> retrieveUserList(Date startDate, Date endDate, Connection conn) throws SQLException {
        PreparedStatement psmt = null;
        ResultSet rs = null;
        ArrayList<LocationUpdate> users = new ArrayList<LocationUpdate>();

        try {
            psmt = conn.prepareStatement(TOP_K_COMPANION_QUERY);

            //Convert to java.util.date to java.sql.timestamp in a long format
            psmt.setTimestamp(1, new java.sql.Timestamp(startDate.getTime()));
            psmt.setTimestamp(2, new java.sql.Timestamp(endDate.getTime()));
            rs = psmt.executeQuery();
            while (rs.next()) {
                LocationUpdate locationUpdate = null;
                locationUpdate = new LocationUpdate(rs.getString(1), rs.getTimestamp(3), rs.getInt(2));
                users.add(locationUpdate);
            }
        } finally {
            ConnectionManager.close(null, psmt, rs);
        }
        return users;
    }

    /**
     * Retrieve list of distinct mac addresses
     *
     * @param conn connection to the database
     * @return list of mac addresses
     * @throws java.sql.SQLException
     */
    public static ArrayList<String> retrieveAllMacAddress(Connection conn) throws SQLException {
        PreparedStatement psmt = null;
        ResultSet rs = null;
        ArrayList<String> macAddresses = new ArrayList<String>();

        try {

            psmt = conn.prepareStatement(SELECT_ALL_MACADDRESS);
            rs = psmt.executeQuery();
            while (rs.next()) {
                String macAddress = rs.getString(1);
                macAddresses.add(macAddress);
            }
        } finally {
            ConnectionManager.close(null, psmt, rs);
        }
        return macAddresses;
    }

    /**
     * Retrieve list of distinct mac addresses based on start date and end date
     *
     * @param conn connection to the database
     * @param startDate fifteen minutes after user specified time
     * @param endDate user specified time
     * @return the list of mac addresses
     * @throws java.sql.SQLException
     */
    public static ArrayList<String> retrieveAllDistinctMacAddress(Date startDate, Date endDate, Connection conn) throws SQLException {
        PreparedStatement psmt = null;
        ResultSet rs = null;
        ArrayList<String> distinctMacAddresses = new ArrayList<String>();

        try {

            psmt = conn.prepareStatement(SELECT_ALL_DISTINCT_MACADDRESS);

            //Convert to java.util.date to java.sql.timestamp in a long format
            psmt.setTimestamp(2, new java.sql.Timestamp(startDate.getTime()));
            psmt.setTimestamp(1, new java.sql.Timestamp(endDate.getTime()));
            rs = psmt.executeQuery();

            while (rs.next()) {
                String macAddress = rs.getString(1);
                distinctMacAddresses.add(macAddress);
            }
        } finally {
            ConnectionManager.close(null, psmt, rs);
        }
        return distinctMacAddresses;
    }

    /**
     * Perform check to determine if there is an update for specified macAddress
     * is found
     *
     * @param macAddress
     * @param conn
     * @return true if an update for specified macAddress is found or false if
     * no update is found
     * @throws SQLException
     */
    public static boolean checkIfMacAddressHasAnyUpdates(String macAddress, Connection conn) throws SQLException {
        PreparedStatement psmt = null;
        ResultSet rs = null;

        try {
            psmt = conn.prepareStatement(SELECT_ALL_UPDATES_FOR_MACADDRESS);

            psmt.setString(1, macAddress);
            rs = psmt.executeQuery();

            if (rs.next()) {
                return true;
            }
        } finally {
            ConnectionManager.close(null, psmt, rs);
        }

        return false;
    }
}
