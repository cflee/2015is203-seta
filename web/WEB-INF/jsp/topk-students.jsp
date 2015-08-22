<%@page import="net.cflee.seta.entity.TopKResult"%>
<%@page import="java.util.ArrayList"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    ArrayList<TopKResult> results = (ArrayList<TopKResult>) request.getAttribute("topKStudentResults");
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>SMUA</title>
    </head>
    <body>
        <h1>Top-k App Usage Report</h1>
        <h2>Top-k students with most app usage</h2>
        <table>
            <tr><th>Rank</th><th>Student name</th></tr>
            <% for (TopKResult result : results) {%>
            <tr><td><%= result.getRank()%></td><td><%= result.getName()%></td></tr>
            <% }%>
        </table>
    </body>
</html>
