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
        <form action="/admin/add" method="post" enctype="multipart/form-data">
            <p>
                <input type="file" name="bootstrap-file">
                <input type="submit" value="Submit">
            </p>
        </form>
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
            FileValidationResult locationLookupFile = (FileValidationResult) request.
                    getAttribute("locationLookupFile");
            FileValidationResult locationFile = (FileValidationResult) request.
                    getAttribute("locationFile");

    if (request.getAttribute("displayResult") != null) {
        %>
        <h2>Results</h2>
        <h3>Number of rows</h3>
        <p>
            <% if (demographicsFile != null) {%>demographics.csv: <%= demographicsFile.getNumOfValidRows()%><br><% } %>
            <% if (appLookupFile != null) {%>app-lookup.csv: <%= appLookupFile.getNumOfValidRows()%><br><% } %>
            <% if (appFile != null) {%>app.csv: <%= appFile.getNumOfValidRows()%><br><% } %>
            <% if (locationLookupFile != null) {%>location-lookup.csv: <%= locationLookupFile.getNumOfValidRows()%><br><% } %>
            <% if (locationFile != null) {%>location.csv: <%= locationFile.getNumOfValidRows()%><br><% } %>
        </p>
        <table>
            <thead>
                <tr><th>Filename</th><th>Row</th><th>Error message</th></tr>
            </thead>
            <tbody>
                <% if (demographicsFile != null) {
                        for (FileValidationError error : demographicsFile.
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
                    <% }
                        } %>
                    <% if (appLookupFile != null) {
                            for (FileValidationError error : appLookupFile.
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
                    <% }
                        } %>
                    <% if (appFile != null) {
                            for (FileValidationError error : appFile.
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
                    <% }
                        } %>
                    <% if (locationLookupFile != null) {
                            for (FileValidationError error : locationLookupFile.
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
                    <% }
                        } %>
                    <% if (locationFile != null) {
                            for (FileValidationError error : locationFile.
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
                    <% }
                        } %>
            </tbody>
        </table>
        <%
            }
        %>
    </body>
</html>
