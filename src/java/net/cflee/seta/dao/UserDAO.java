
package net.cflee.seta.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import net.cflee.seta.entity.User;
import net.cflee.seta.utility.ConnectionManager;

public class UserDAO {

    private static final String TRUNCATE = "TRUNCATE TABLE user";
    private static final String SELECT_ALL = "SELECT * FROM user";
    private static final String RETRIEVE = "SELECT * FROM user WHERE email LIKE ?";
    private static final String INSERT = "INSERT INTO user "
            + "(mac_address, name, password, email, gender, school, year) "
            + "values(?,?,?,?,?,?,?)";

    /**
     * Retrieve the user based on the username
     *
     * @param username username of the user
     * @param conn connection to the database
     * @return the user based on the username
     * @throws java.sql.SQLException
     */
    public static User retrieve(String username, Connection conn) throws SQLException {

        PreparedStatement psmt = null;
        ResultSet rs = null;
        try {

            psmt = conn.prepareStatement(RETRIEVE);
            psmt.setString(1, username + "@%");
            rs = psmt.executeQuery();
            if (rs.next()) {
                String macAddress = rs.getString(1);
                String name = rs.getString(2);
                String password = rs.getString(3);
                String userEmail = rs.getString(4);
                String gend = rs.getString(5);
                char gender = gend.charAt(0);
                String school = rs.getString(6);
                int year = rs.getInt(7);
                return new User(macAddress, name, password, userEmail, gender,
                        school, year);
            }
        } finally {
            ConnectionManager.close(null, psmt, rs);
        }
        return null;
    }

    /**
     * Insert list of users into the database
     *
     * @param userList list of users
     * @param conn connection to the database
     * @throws java.sql.SQLException
     */
    public static void insertUsers(ArrayList<User> userList, Connection conn) throws SQLException {

        PreparedStatement psmt = null;
        try {

            psmt = conn.prepareStatement(INSERT);
            for (int i = 0; i < userList.size(); i++) {
                User user = userList.get(i);
                psmt.setString(1, user.getMacAddress());
                psmt.setString(2, user.getName());
                psmt.setString(3, user.getPassword());
                psmt.setString(4, user.getEmail());
                psmt.setString(5, user.getGender() + "");
                psmt.setString(6, user.getSchool());
                psmt.setInt(7, user.getYear());
                psmt.executeUpdate();
            }
        } finally {
            ConnectionManager.close(null, psmt, null);
        }
    }

    /**
     * Retrieve all users from the database
     *
     * @param conn connection to the database
     * @return list of users
     * @throws java.sql.SQLException
     */
    public static ArrayList<User> retrieveAll(Connection conn) throws SQLException {
        PreparedStatement psmt = null;
        ResultSet rs = null;
        ArrayList<User> userList = new ArrayList<User>();

        try {

            psmt = conn.prepareStatement(SELECT_ALL);
            rs = psmt.executeQuery();
            while (rs.next()) {
                String macAddress = rs.getString(1);
                String name = rs.getString(2);
                String password = rs.getString(3);
                String email = rs.getString(4);
                char gender = rs.getString(5).charAt(0);
                String school = rs.getString(6);
                int year = rs.getInt(7);
                User user = new User(macAddress, name, password, email, gender,
                        school, year);
                userList.add(user);
            }
        } finally {
            ConnectionManager.close(null, psmt, rs);
        }
        return userList;
    }

    /**
     * Drop user table in the database
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

    public static ArrayList<String> getAllMacAddresses(Connection conn) throws SQLException {
        PreparedStatement psmt = null;
        ResultSet rs = null;
        ArrayList<String> macAddressList = new ArrayList<String>();

        try {

            psmt = conn.prepareStatement(SELECT_ALL);
            rs = psmt.executeQuery();
            while (rs.next()) {
                String macAddress = rs.getString(1);
                macAddressList.add(macAddress);
            }
        } finally {
            ConnectionManager.close(null, psmt, rs);
        }
        return macAddressList;
    }

}
