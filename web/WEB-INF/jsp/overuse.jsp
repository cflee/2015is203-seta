<%@page import="net.cflee.seta.entity.OveruseResult"%>
<%@page import="java.util.ArrayList"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    OveruseResult result = (OveruseResult) request.getAttribute("overuseResult");
        String[] indexStrings = {"", "Normal", "ToBeCautious", "Overusing"};
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>SMUA</title>
    </head>
    <body>
        <h1>Smartphone Overuse Report</h1>
        <table>
            <tr><td>Smartphone usage duration</td><td><%= result.getDailyUsageDuration() / 60 / 60%> hours</td></tr>
            <tr><td>Gaming duration</td><td><%= result.getGameUsageDuration() / 60 / 60%> hours</td></tr>
            <tr><td>Phone access frequency</td><td><%= result.getAccessFrequency()%> per hour</td></tr>
            <tr><td>Overuse index</td><td><%= indexStrings[result.getOverallIndex()]%></td></tr>
        </table>
    </body>
</html>
