
package net.cflee.seta.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import net.cflee.seta.controller.BasicAppUsageController;
import net.cflee.seta.entity.BasicAppUsageTimeCategoryResult;
import net.cflee.seta.entity.User;
import net.cflee.seta.utility.ConnectionManager;
import net.cflee.seta.utility.DateUtility;

@WebServlet(name = "BasicTimeCategoryDemographicsServlet", urlPatterns = {"/report/basic-time-category-demographics"})
public class BasicTimeCategoryDemographicsServlet extends HttpServlet {

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
        List<String> validParams = Arrays.asList("year", "gender", "school");

        if (user == null || !user.getEmail().equals("admin")) {
            response.sendRedirect("/");
            return;
        }

        try {
            String startDateString = request.getParameter("startDate");
            String endDateString = request.getParameter("endDate");
            String param1 = request.getParameter("param1");
            String param2 = request.getParameter("param2");
            String param3 = request.getParameter("param3");
            List<String> enteredParams = Arrays.asList(param1, param2, param3);

            try {
                conn = ConnectionManager.getConnection();
                Date startDate = DateUtility.parseDateString(startDateString);
                Date endDate = DateUtility.parseDateString(endDateString);

                LinkedHashSet<String> params = new LinkedHashSet<>();
                for (String enteredParam : enteredParams) {
                    if (validParams.contains(enteredParam)) {
                        params.add(enteredParam);
                    }
                }

                LinkedHashMap<String, BasicAppUsageTimeCategoryResult> results = BasicAppUsageController
                        .computeTimeCategoryDemographics(startDate, endDate, new ArrayList<>(params), conn);

                request.setAttribute("basicTimeCategoryDemographicsResults", results);
                request.getRequestDispatcher("/WEB-INF/jsp/basic-time-category-demographics.jsp").forward(request,
                        response);
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
