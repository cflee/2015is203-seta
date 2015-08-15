<%@page import="net.cflee.seta.entity.FileValidationError"%>
<%@page import="net.cflee.seta.entity.FileValidationResult"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>SMUA Admin Page</title>
    </head>
    <body>
        <h1>Admin Page</h1>
        <h2>Bootstrap</h2>
        <form action="/admin/bootstrap" method="post" enctype="multipart/form-data">
            <p>
                <input type="file" name="bootstrap-file">
                <input type="submit" value="Submit">
            </p>
        </form>
        <h2>Add Data</h2>
        <h2>Bootstrap Location Data</h2>
        <h2>Add Location Data</h2>
        <h2>Delete Location Data</h2>
        <%
            FileValidationResult demographicsFile = (FileValidationResult) request.
                    getAttribute("demographicsFile");
            FileValidationResult appLookupFile = (FileValidationResult) request.
                    getAttribute("appLookupFile");
            FileValidationResult appFile = (FileValidationResult) request.
                    getAttribute("appFile");

            if (demographicsFile != null) {
        %>
        <h2>Results</h2>
        <h3>Number of rows</h3>
        <p>
            demographics.csv: <%= demographicsFile.getNumOfValidRows()%><br>
            app-lookup.csv: <%= appLookupFile.getNumOfValidRows()%><br>
            app.csv: <%= appFile.getNumOfValidRows()%><br>
        </p>
        <table>
            <thead>
                <tr><th>Filename</th><th>Row</th><th>Error message</th></tr>
            </thead>
            <tbody>
                <% for (FileValidationError error : demographicsFile.
                            getErrors()) {%>
                    <tr>
                        <td><%= error.getFilename()%></td>
                        <td><%= error.getLineNumber()%></td>
                        <td>
                            <% for (String errorMessage : error.
                                    getMessages()) {%>
                            <%= errorMessage%> |
                            <% } %>
                        </td>
                    </tr>
                    <% } %>
                    <% for (FileValidationError error : appLookupFile.
                            getErrors()) {%>
                    <tr>
                        <td><%= error.getFilename()%></td>
                        <td><%= error.getLineNumber()%></td>
                        <td>
                            <% for (String errorMessage : error.
                                        getMessages()) {%>
                            <%= errorMessage%> |
                            <% } %>
                        </td>
                    </tr>
                    <% } %>
                    <% for (FileValidationError error : appFile.
                            getErrors()) {%>
                    <tr>
                        <td><%= error.getFilename()%></td>
                        <td><%= error.getLineNumber()%></td>
                        <td>
                            <% for (String errorMessage : error.
                                        getMessages()) {%>
                            <%= errorMessage%> |
                            <% } %>
                        </td>
                    </tr>
                    <% } %>
            </tbody>
        </table>
        <%
            }
        %>
    </body>
</html>
