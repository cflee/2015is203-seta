
package net.cflee.seta.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import net.cflee.seta.controller.LoginController;
import net.cflee.seta.entity.User;
import net.cflee.seta.utility.ConnectionManager;

@WebServlet(name = "LoginServlet", urlPatterns = { "/login" })
public class LoginServlet extends HttpServlet {

    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user != null) {
            response.sendRedirect("/");
            return;
        }

        request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request,
                response);
    } 

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        Connection conn = null;

        if (user != null) {
            response.sendRedirect("/");
            return;
        }

        try {
            String username = request.getParameter("username");
            String password = request.getParameter("password");

            try {
                conn = ConnectionManager.getConnection();
                user = LoginController.login(username, password, conn);
            } catch (SQLException e) {
                request.setAttribute("errorMessage", "SQL connection error");
                request.getRequestDispatcher("/WEB-INF/jsp/errorPage.jsp")
                        .forward(request, response);
            }

            if (user != null) {
                session.setAttribute("user", user);
                if (username.equalsIgnoreCase("admin")) {
                    response.sendRedirect("/admin");
                    return;
                }
                response.sendRedirect("/");
            } else {
                request.setAttribute("username", username);
                request.setAttribute("errorMessage", "Invalid username/password");
                request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(
                        request, response);
            }
        } finally {
            ConnectionManager.close(conn, null, null);
        }
    }

}
