<%@page import="java.util.ArrayList"%>
<%@page import="net.cflee.seta.entity.HeatmapResult"%>
<%@page import="net.cflee.seta.entity.User"%>
<% User user = (User) session.getAttribute("user"); %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>SMUA</title>
    </head>
    <body>
        <h1>Hello World!</h1>
        <ul>
            <% if (user == null) { %><li><a href="/login">Login</a></li><% } %>
            <% if (user != null) { %><li><a href="/logout">Logout</a></li><% } %>
            <% if (user != null && user.getEmail().equals("admin")) { %><li><a href="/admin">Admin Page</a></li><% }%>
        </ul>
        <% if (user != null) { %>
        <h2>Smartphone Usage Heatmap</h2>
        <form action="/report/smartphone-usage-heatmap" method="post">
            <p>
                <label>Date: <input type="text" name="date" placeholder="2015-08-01 12:15:00"></label><br>
                <label>Floor: <input type="text" name="floor" placeholder="0 to 5"></label><br>
                <input type="submit" value="Submit">
            </p>
        </form>
        <%
            if (request.getAttribute("heatmapResults") != null) {
                ArrayList<HeatmapResult> heatmapResults = (ArrayList<HeatmapResult>) request.getAttribute(
                "heatmapResults");
        %>
        <table>
            <tr><th>Semantic Place</th><th>Crowd Density</th></tr>
        <% for (HeatmapResult result : heatmapResults) {%>
            <tr><td><%= result.getPlaceName()%></td><td><%= result.getCrowdDensity()%></td></tr>
        <% } %>
        </table>
        <%
            }
        %>
        <% }%>
    </body>
</html>
