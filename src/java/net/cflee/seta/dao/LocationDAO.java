package net.cflee.seta.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import net.cflee.seta.entity.Location;
import net.cflee.seta.utility.ConnectionManager;

/**
 * DAO that is responsible for CRUD functions performed in the location table
 */
public class LocationDAO {

    private static final String TRUNCATE
            = "TRUNCATE TABLE location";
    private static final String INSERT
            = "INSERT INTO location VALUES(?,?)";
    private static final String RETRIEVE
            = "SELECT * FROM location "
            + "WHERE location_id = ?";
    private static final String RETRIEVE_ALL_LOCATION_IDS
            = "SELECT location_id FROM location";
    private static final String SELECT_ALL
            = "SELECT location_id, semantic_place FROM location";

    /**
     * To check if the location id is valid
     *
     * @param locationId the specified location id
     * @param conn connection to the database
     * @return if true if the location id is valid, else return false
     * @throws java.sql.SQLException
     */
    public static boolean isValidLocationId(int locationId, Connection conn) throws SQLException {

        PreparedStatement psmt = null;
        ResultSet rs = null;
        try {
            psmt = conn.prepareStatement(RETRIEVE);
            psmt.setInt(1, locationId);
            rs = psmt.executeQuery();
            while (rs.next()) {
                //assume that any data populated would mean that the validation has passed
                return true;
            }
        } finally {
            ConnectionManager.close(null, psmt, rs);
        }
        return false;
    }

    /**
     * Retrieve all the location ids
     *
     * @param conn connection to the database
     * @return the list of location id
     * @throws java.sql.SQLException
     */
    public static ArrayList<Integer> getAllLocationIds(Connection conn) throws SQLException {
        ArrayList<Integer> resultList = new ArrayList<Integer>();

        PreparedStatement psmt = null;
        ResultSet rs = null;
        try {
            psmt = conn.prepareStatement(RETRIEVE_ALL_LOCATION_IDS);
            rs = psmt.executeQuery();
            while (rs.next()) {
                resultList.add(rs.getInt(1));
            }
        } finally {
            ConnectionManager.close(null, psmt, rs);
        }

        return resultList;
    }

    /**
     * Insert the list of location into the database
     *
     * @param locationList specified list of location
     * @param conn connection to the database
     * @throws java.sql.SQLException
     */
    public static void insertLocations(ArrayList<Location> locationList, Connection conn) throws SQLException {

        PreparedStatement psmt = null;
        try {

            psmt = conn.prepareStatement(INSERT);
            for (int i = 0; i < locationList.size(); i++) {
                Location location = locationList.get(i);
                psmt.setInt(1, location.getLocationId());
                psmt.setString(2, location.getSemanticPlace());
                psmt.executeUpdate();
            }
        } finally {
            ConnectionManager.close(null, psmt, null);
        }

    }

    /**
     * Create all the data in location table
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
     * Retrieve the list of locations
     *
     * @param conn connection to the database
     * @return the list of locations
     * @throws java.sql.SQLException
     */
    public static HashMap<Integer, String> retrieveAll(Connection conn) throws SQLException {

        PreparedStatement psmt = null;
        ResultSet rs = null;
        HashMap<Integer, String> resultMap = new HashMap<Integer, String>();

        try {

            psmt = conn.prepareStatement(SELECT_ALL);
            rs = psmt.executeQuery();
            while (rs.next()) {
                int locationId = rs.getInt(1);
                String semanticPlace = rs.getString(2);
                resultMap.put(locationId, semanticPlace);
            }
        } finally {
            ConnectionManager.close(null, psmt, rs);
        }

        return resultMap;
    }

}
