
package net.cflee.seta.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import net.cflee.seta.controller.OveruseController;
import net.cflee.seta.entity.AdvancedOveruseResult;
import net.cflee.seta.entity.User;
import net.cflee.seta.utility.ConnectionManager;
import net.cflee.seta.utility.DateUtility;

@WebServlet(name = "OveruseAdvancedServlet", urlPatterns = {"/report/overuse-adv"})
public class OveruseAdvancedServlet extends HttpServlet {

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
            String startDateString = request.getParameter("startDate");
            String endDateString = request.getParameter("endDate");

            try {
                conn = ConnectionManager.getConnection();
                Date startDate = DateUtility.parseDateString(startDateString);
                Date endDate = DateUtility.parseDateString(endDateString);

                // invoke controller
                AdvancedOveruseResult advancedOveruseResult = OveruseController
                        .computeAdvancedReport(startDate, endDate, user.getEmail(), conn);

                request.setAttribute("advancedOveruseResult", advancedOveruseResult);
                request.getRequestDispatcher("/WEB-INF/jsp/overuse-advanced.jsp").forward(request, response);
            } catch (SQLException e) {
                request.setAttribute("errorMessage", "SQL connection error");
                request.getRequestDispatcher("/WEB-INF/jsp/errorPage.jsp").forward(request, response);
            } catch (ParseException ex) {
                // from DateUtility
                request.setAttribute("errorMessage", "Date format error");
                request.getRequestDispatcher("/WEB-INF/jsp/errorPage.jsp").forward(request, response);
            } catch (NumberFormatException ex) {
                // from Integer
                request.setAttribute("errorMessage", "Floor format error");
                request.getRequestDispatcher("/WEB-INF/jsp/errorPage.jsp").forward(request, response);
            }

        } finally {
            ConnectionManager.close(conn, null, null);
        }
    }

}
