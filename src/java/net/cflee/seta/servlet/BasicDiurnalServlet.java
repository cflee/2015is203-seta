
package net.cflee.seta.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.LinkedHashMap;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import net.cflee.seta.controller.BasicAppUsageController;
import net.cflee.seta.entity.User;
import net.cflee.seta.utility.ConnectionManager;
import net.cflee.seta.utility.DateUtility;

@WebServlet(name = "BasicDiurnalServlet", urlPatterns = {"/report/basic-diurnal"})
public class BasicDiurnalServlet extends HttpServlet {

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
        response.sendRedirect("/");
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

        if (user == null) {
            response.sendRedirect("/");
            return;
        }

        try {
            String dateString = request.getParameter("date");
            String yearString = request.getParameter("year");
            String genderString = request.getParameter("gender");
            String school = request.getParameter("school");

            try {
                conn = ConnectionManager.getConnection();
                Date date = DateUtility.parseDateString(dateString);

                Integer year = null;
                if (!yearString.equals("-")) {
                    year = Integer.parseInt(yearString);
                }
                Character gender = null;
                if (!genderString.equals("-")) {
                    gender = genderString.charAt(0);
                }
                if (school.equals("-")) {
                    school = null;
                }

                // TODO: capture return result
                LinkedHashMap<Integer, Double> results
                        = BasicAppUsageController.computeDiurnal(date, year, gender, school, conn);

                request.setAttribute("basicDiurnalResults", results);
                request.getRequestDispatcher("/WEB-INF/jsp/basic-diurnal.jsp").forward(request, response);
            } catch (SQLException e) {
                request.setAttribute("errorMessage", "SQL connection error");
                request.getRequestDispatcher("/WEB-INF/jsp/errorPage.jsp").forward(request, response);
            } catch (ParseException ex) {
                // from DateUtility
                request.setAttribute("errorMessage", "Date format error");
                request.getRequestDispatcher("/WEB-INF/jsp/errorPage.jsp").forward(request, response);
            }
        } finally {
            ConnectionManager.close(conn, null, null);
        }
    }

}
