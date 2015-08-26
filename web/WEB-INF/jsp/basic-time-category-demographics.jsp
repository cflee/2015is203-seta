<%@page import="java.util.Map"%>
<%@page import="java.util.LinkedHashMap"%>
<%@page import="net.cflee.seta.entity.BasicAppUsageTimeCategoryResult"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    LinkedHashMap<String, BasicAppUsageTimeCategoryResult> results
            = (LinkedHashMap<String, BasicAppUsageTimeCategoryResult>) request.getAttribute(
                    "basicTimeCategoryDemographicsResults");

                        // compute grand total for percentage calculation base
                        int total = 0;
                        for (Map.Entry<String, BasicAppUsageTimeCategoryResult> entry : results.entrySet()) {
                            BasicAppUsageTimeCategoryResult result = entry.getValue();
                            total += result.getTotal();
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
        <h2>Breakdown by usage time category and demographics</h2>
        <table>
            <tr><th>Demographic</th><th>Mild User</th><th>Normal User</th><th>Intense User</th></tr>
                    <%
                        for (Map.Entry<String, BasicAppUsageTimeCategoryResult> entry : results.entrySet()) {
                            String demographic = entry.getKey();
                            BasicAppUsageTimeCategoryResult result = entry.getValue();
                    %>
            <tr>
                <td><%= demographic%></td>
                <td><%= result.getMild()%> (<%= Math.round((float) result.getMild() / total * 100)%>%)</td>
                <td><%= result.getNormal()%> (<%= Math.round((float) result.getNormal() / total * 100)%>%)</td>
                <td><%= result.getIntense()%> (<%= Math.round((float) result.getIntense() / total * 100)%>%)</td>
            </tr>
            <% }%>
        </table>
    </body>
</html>
