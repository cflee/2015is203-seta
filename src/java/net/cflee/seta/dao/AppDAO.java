package net.cflee.seta.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import net.cflee.seta.entity.App;
import net.cflee.seta.utility.ConnectionManager;

/**
 * DAO that is responsible for CRUD functions performed in the location table
 */
public class AppDAO {

    private static final String TRUNCATE
            = "TRUNCATE TABLE app";
    private static final String INSERT
            = "INSERT INTO app VALUES(?,?,?)";
    private static final String RETRIEVE_ALL_APP_IDS
            = "SELECT app_id FROM app";
    private static final String RETRIEVE_ALL_APP_CATEGORIES
            = "SELECT DISTINCT app_category FROM app ORDER BY app_category ASC";

    /**
     * Retrieve all the location ids
     *
     * @param conn connection to the database
     * @return the list of location id
     * @throws java.sql.SQLException
     */
    public static ArrayList<Integer> getAllAppIds(Connection conn) throws SQLException {
        ArrayList<Integer> resultList = new ArrayList<Integer>();

        PreparedStatement psmt = null;
        ResultSet rs = null;
        try {
            psmt = conn.prepareStatement(RETRIEVE_ALL_APP_IDS);
            rs = psmt.executeQuery();
            while (rs.next()) {
                resultList.add(rs.getInt(1));
            }
        } finally {
            ConnectionManager.close(null, psmt, rs);
        }

        return resultList;
    }

    public static ArrayList<String> getAllAppCategories(Connection conn) throws SQLException {
        ArrayList<String> results = new ArrayList<>();

        PreparedStatement psmt = null;
        ResultSet rs = null;
        try {
            psmt = conn.prepareStatement(RETRIEVE_ALL_APP_CATEGORIES);
            rs = psmt.executeQuery();
            while (rs.next()) {
                results.add(rs.getString(1));
            }
        } finally {
            ConnectionManager.close(null, psmt, rs);
        }

        return results;
    }

    /**
     * Insert the list of location into the database
     *
     * @param appList specified list of location
     * @param conn connection to the database
     * @throws java.sql.SQLException
     */
    public static void insertApps(ArrayList<App> appList, Connection conn) throws SQLException {
        PreparedStatement psmt = null;
        try {

            psmt = conn.prepareStatement(INSERT);
            for (int i = 0; i < appList.size(); i++) {
                App app = appList.get(i);
                psmt.setInt(1, app.getAppId());
                psmt.setString(2, app.getName());
                psmt.setString(3, app.getCategory());
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

}
