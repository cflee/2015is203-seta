package net.cflee.seta.controller;

import java.sql.Connection;
import java.sql.SQLException;
import net.cflee.seta.dao.UserDAO;
import net.cflee.seta.entity.User;

public class LoginController {

    public static User login(String username, String password, Connection conn) throws SQLException {
        User user = null;

        if (username.equalsIgnoreCase("admin")) {
            user = new User(null, null, "performMagic", "admin", '\u0000', null,
                    0);
        } else {
            // Ignore username containing % as it is a SQL wildcard
            if (!username.contains("%")) {
                user = UserDAO.retrieve(username, conn);
            }
        }

        if (user != null && user.getPassword().equals(password)) {
            return user;
        } else {
            return null;
        }
    }

}
