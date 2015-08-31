<%@page import="java.util.Map"%>
<%@page import="net.cflee.seta.entity.AppUpdateRecord"%>
<%@page import="net.cflee.seta.entity.SocialActivenessResult"%>
<%@page import="java.util.ArrayList"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    SocialActivenessResult result = (SocialActivenessResult) request.getAttribute("socialActivenessResult");
        int totalLocationTime = result.getTimeAlone() + result.getTimeInGroups();
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>SMUA</title>
    </head>
    <body>
        <h1>Social Activeness Report</h1>
        <p>
            Total use time in Social category (seconds): <%= result.getSocialDuration()%>
        </p>
        <table>
            <tr><th>App name</th></tr>
            <% for (Map.Entry<AppUpdateRecord, Integer> entry : result.getSocialAppDurations().entrySet()) {%>
            <tr><td><%= entry.getKey().getAppName()%> (<%= Math.round((float) entry.getValue() / result.getSocialDuration() * 100)%>%)</td></tr>
            <% }%>
        </table>
        <p>
            Total time in SIS building (seconds): <%= totalLocationTime%><br>
            Time spent in groups: <%= Math.round((float) result.getTimeInGroups() / totalLocationTime * 100)%>% <%= result.getTimeInGroups()%><br>
            Time spent alone: <%= Math.round((float) result.getTimeAlone() / totalLocationTime * 100)%>% <%= result.getTimeAlone()%><br>
        </p>
    </body>
</html>
