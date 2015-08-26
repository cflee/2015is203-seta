<%@page import="java.util.Map"%>
<%@page import="java.util.LinkedHashMap"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    LinkedHashMap<Integer, Double> results = (LinkedHashMap<Integer, Double>) request.getAttribute("basicDiurnalResults");
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>SMUA</title>
    </head>
    <body>
        <h1>Basic App Usage Reports</h1>
        <h2>Diurnal pattern of app usage time</h2>
        <table>
            <tr><th>Hour</th><th>Average app usage time per user (minutes)</th></tr>
            <% for (Map.Entry<Integer, Double> entry : results.entrySet()) {%>
            <tr><td><%= entry.getKey()%></td><td><%= entry.getValue()%></td></tr>
            <% }%>
        </table>
    </body>
</html>
