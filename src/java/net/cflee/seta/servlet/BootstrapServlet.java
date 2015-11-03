package net.cflee.seta.servlet;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import net.cflee.seta.controller.BootstrapController;
import net.cflee.seta.entity.DeleteFileValidationResult;
import net.cflee.seta.entity.FileValidationResult;
import net.cflee.seta.entity.User;
import net.cflee.seta.utility.ConnectionManager;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

@WebServlet(name = "BootstrapServlet", urlPatterns = {"/admin/bootstrap"})
public class BootstrapServlet extends HttpServlet {

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // don't bother checking for auth, just redirect
        response.sendRedirect("/admin");
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null || !user.getEmail().equals("admin")) {
            response.sendRedirect("/");
            return;
        }

        Connection conn = null;

        // Create a factory for disk-based file items
        DiskFileItemFactory factory = new DiskFileItemFactory();

        // Configure a repository (to ensure a secure temp location is used)
        ServletContext servletContext = this.getServletConfig().getServletContext();
        File repository = (File) servletContext.getAttribute("javax.servlet.context.tempdir");
        factory.setRepository(repository);

        // Create a new file upload handler
        ServletFileUpload upload = new ServletFileUpload(factory);

        // Parse the request
        try {
            List<FileItem> items = upload.parseRequest(request);
            Iterator<FileItem> iter = items.iterator();

            FileValidationResult demographicsResult = null;
            FileValidationResult appLookupResult = null;
            FileValidationResult appResult = null;
            FileValidationResult locationLookupResult = null;
            FileValidationResult locationResult = null;
            DeleteFileValidationResult locationDeleteResult = null;

            while (iter.hasNext()) {
                FileItem item = iter.next();
                if (!item.isFormField() && item.getFieldName().equals("bootstrap-file")) {
                    File tempFile = File.createTempFile("temp", null);
                    item.write(tempFile);

                    ZipFile zipFile = new ZipFile(tempFile);
                    ZipEntry demographics = zipFile.getEntry("demographics.csv");
                    ZipEntry appLookup = zipFile.getEntry("app-lookup.csv");
                    ZipEntry app = zipFile.getEntry("app.csv");
                    ZipEntry locationLookup = zipFile.getEntry("location-lookup.csv");
                    ZipEntry location = zipFile.getEntry("location.csv");
                    ZipEntry locationDelete = zipFile.getEntry("location-delete.csv");

                    try {
                        conn = ConnectionManager.getConnection();

                        // basic bootstrap
                        // but clear out location data as well
                        BootstrapController.resetAllBasicData(conn);
                        BootstrapController.resetAllLocationData(conn);
                        demographicsResult = BootstrapController.processDemographicsFile(zipFile.getInputStream(
                                demographics), "demographics.csv", conn);
                        appLookupResult = BootstrapController
                                .processAppLookupFile(zipFile.getInputStream(appLookup), "app-lookup.csv", conn);
                        appResult = BootstrapController.processAppFile(zipFile.getInputStream(app), "app.csv", conn);

                        // location bootstrap
                        if (locationLookup != null) {
                            locationLookupResult = BootstrapController.processLocationLookUpFile(zipFile.getInputStream(
                                    locationLookup), "location-lookup.csv", conn);
                            locationResult = BootstrapController.processLocationFile(zipFile.getInputStream(location),
                                    "location.csv", conn);

                            // process location-delete.csv if necessary
                            if (locationDelete != null) {
                                locationDeleteResult = BootstrapController.processLocationDeleteFile(
                                        zipFile.getInputStream(locationDelete), "location-delete.csv", conn);
                            }
                        }

                        request.setAttribute("displayResult", true);
                        request.setAttribute("demographicsFile", demographicsResult);
                        request.setAttribute("appLookupFile", appLookupResult);
                        request.setAttribute("appFile", appResult);
                        request.setAttribute("locationLookupFile", locationLookupResult);
                        request.setAttribute("locationFile", locationResult);
                        request.setAttribute("locationDeleteFile", locationDeleteResult);
                        request.getRequestDispatcher("/WEB-INF/jsp/admin-bootstrap.jsp").forward(request, response);

                    } catch (SQLException e) {
                        request.setAttribute("errorMessage", "SQL error: " + e.getMessage());
                        request.getRequestDispatcher("/WEB-INF/jsp/errorPage.jsp").forward(request, response);
                    }
                }
            }

        } catch (FileUploadException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Unable to upload file!");
            request.getRequestDispatcher("/WEB-INF/jsp/errorPage.jsp").forward(request, response);
        } catch (ZipException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "No zip file found!");
            request.getRequestDispatcher("/WEB-INF/jsp/errorPage.jsp").forward(request, response);
        } catch (IOException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "IO Error");
            request.getRequestDispatcher("/WEB-INF/jsp/errorPage.jsp").forward(request, response);
        } catch (Exception e) {
            // catch for file item.write() method
            e.printStackTrace();
            request.setAttribute("errorMessage", "Exception Error");
            request.getRequestDispatcher("/WEB-INF/jsp/errorPage.jsp").forward(request, response);
        } finally {
            ConnectionManager.close(conn, null, null);
        }
    }

}
