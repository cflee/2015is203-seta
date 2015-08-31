<%@page import="net.cflee.seta.entity.AdvancedOveruseResult"%>
<%@page import="java.util.ArrayList"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    AdvancedOveruseResult result = (AdvancedOveruseResult) request.getAttribute("advancedOveruseResult");
    String[] indexStrings = {"", "Normal", "ToBeCautious", "Overusing"};
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>SMUA</title>
    </head>
    <body>
        <h1>Advanced Smartphone Overuse Report</h1>
        <table>
            <tr><td>Average daily total class time</td><td><%= result.getClassDuration() / result.getNumOfDays()%></td></tr>
            <tr><td>Average daily total small group time</td><td><%= result.getGroupDuration() / result.getNumOfDays()%></td></tr>
            <tr><td>Average daily total distracting smartphone usage time</td><td><%= result.getSmartphoneUsageDuration() / result.getNumOfDays()%></td></tr>
            <tr><td>Overuse index</td><td><%= indexStrings[result.getOverallIndex()]%></td></tr>
        </table>
    </body>
</html>
