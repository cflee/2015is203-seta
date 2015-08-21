<%@page import="net.cflee.seta.entity.AppUsageTimeResult"%>
<%@page import="java.util.ArrayList"%>
<%@page import="net.cflee.seta.entity.BasicAppUsageTimeCategoryResult"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    // note: duration is in seconds
    ArrayList<AppUsageTimeResult> results = (ArrayList<AppUsageTimeResult>) request.getAttribute(
                "basicAppCategoryResults");
    int total = 0;
    for (AppUsageTimeResult result : results) {
        total += result.getDuration();
    }
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>SMUA</title>
    </head>
    <body>
        <h1>Basic App Usage Reports</h1>
        <h2>Breakdown by app category</h2>
        <table>
            <tr><th>App Category</th><th>Number of hours</th><th>Percentage</th></tr>
            <% for (AppUsageTimeResult result : results) {%>
            <tr><td><%= result.getName()%></td><td><%= (float) result.getDuration() / 60 / 60%></td><td><%= Math.round((float) result.getDuration() / total * 100)%>%</td></tr>
            <% }%>
        </table>
    </body>
</html>
