
package net.cflee.seta.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import net.cflee.seta.entity.User;

@WebServlet(name = "AdminServlet", urlPatterns = { "/admin" })
public class AdminServlet extends HttpServlet {

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

        if (!user.getEmail().equals("admin")) {
            response.sendRedirect("/");
            return;
        }

        request.getRequestDispatcher("/WEB-INF/jsp/admin-bootstrap.jsp").
                forward(request, response);
    }

}
