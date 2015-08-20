<%@page import="net.cflee.seta.entity.BasicAppUsageTimeCategoryResult"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    BasicAppUsageTimeCategoryResult results = (BasicAppUsageTimeCategoryResult) request.getAttribute(
                "basicTimeCategoryResults");
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>SMUA</title>
    </head>
    <body>
        <h1>Basic App Usage Reports</h1>
        <h2>Breakdown by usage time category</h2>
        <table>
            <tr><th>Category</th><th>Number of users</th><th>Percentage</th></tr>
            <tr><td>Mild User</td><td><%= results.getMild()%></td><td><%= Math.round((float) results.getMild() / results.getTotal() * 100)%></td></tr>
            <tr><td>Normal User</td><td><%= results.getNormal()%></td><td><%= Math.round((float) results.getNormal() / results.getTotal() * 100)%></td></tr>
            <tr><td>Intense User</td><td><%= results.getIntense()%></td><td><%= Math.round((float) results.getIntense() / results.getTotal() * 100)%></td></tr>
        </table>
    </body>
</html>
