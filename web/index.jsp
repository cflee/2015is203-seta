<%@page import="java.sql.SQLException"%>
<%@page import="net.cflee.seta.utility.ConnectionManager"%>
<%@page import="java.sql.Connection"%>
<%@page import="net.cflee.seta.dao.AppDAO"%>
<%@page import="java.util.ArrayList"%>
<%@page import="net.cflee.seta.entity.HeatmapResult"%>
<%@page import="net.cflee.seta.entity.User"%>
<% User user = (User) session.getAttribute("user"); %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    int[] validYears = {2011, 2012, 2013, 2014, 2015};
    char[] validGenders = {'F', 'M'};
    String[] validSchools = {"accountancy", "business", "economics", "law", "sis", "socsc"};

    Connection conn;
    ArrayList<String> validAppCategories = new ArrayList<>();
    try {
        conn = ConnectionManager.getConnection();
        validAppCategories = AppDAO.getAllAppCategories(conn);
    } catch (SQLException e) {

    }
%>
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
        <h2>Basic App Usage Reports</h2>
        <h3>Breakdown by usage time category</h3>
        <form action="/report/basic-time-category" method="post">
            <p>
                <label>Start date: <input type="text" name="startDate" placeholder="2015-08-01"></label><br>
                <label>End date: <input type="text" name="endDate" placeholder="2015-08-02"></label><br>
                <input type="submit" value="Submit">
            </p>
        </form>
        <h3>Breakdown by usage time category and demographics</h3>
        <form action="/report/basic-time-category-demographics" method="post">
            <p>
                <label>Start date: <input type="text" name="startDate" placeholder="2015-08-01"></label><br>
                <label>End date: <input type="text" name="endDate" placeholder="2015-08-02"></label><br>
                <label>
                    Param1:
                    <select name="param1">
                        <option value="year">Year</option>
                        <option value="gender">Gender</option>
                        <option value="school">School</option>
                    </select>
                </label>
                <label>
                    Param2:
                    <select name="param2">
                        <option value="-">-none-</option>
                        <option value="year">Year</option>
                        <option value="gender">Gender</option>
                        <option value="school">School</option>
                    </select>
                </label>
                <label>
                    Param3:
                    <select name="param3">
                        <option value="-">-none-</option>
                        <option value="year">Year</option>
                        <option value="gender">Gender</option>
                        <option value="school">School</option>
                    </select>
                </label><br>
                <input type="submit" value="Submit">
            </p>
        </form>
        <h3>Breakdown by app category</h3>
        <form action="/report/basic-app-category" method="post">
            <p>
                <label>Start date: <input type="text" name="startDate" placeholder="2015-08-01"></label><br>
                <label>End date: <input type="text" name="endDate" placeholder="2015-08-02"></label><br>
                <input type="submit" value="Submit">
            </p>
        </form>
        <h3>Diurnal pattern of app usage time</h3>
        <form action="/report/basic-diurnal" method="post">
            <p>
                <label>Date: <input type="text" name="date" placeholder="2015-08-01"></label><br>
                <label>
                    Year:
                    <select name="year">
                        <option value="-">-all-</option>
                        <%
                            for (int year : validYears) {
                                out.println("<option value=\"" + year + "\">" + year + "</option>");
                            }
                        %>
                    </select>
                </label>
                <label>
                    Gender:
                    <select name="gender">
                        <option value="-">-all-</option>
                        <%
                            for (char gender : validGenders) {
                                out.println("<option value=\"" + gender + "\">" + gender + "</option>");
                            }
                        %>
                    </select>
                </label>
                <label>
                    School:
                    <select name="school">
                        <option value="-">-all-</option>
                        <%
                            for (String school : validSchools) {
                                out.println("<option value=\"" + school + "\">" + school + "</option>");
                            }
                        %>
                    </select>
                </label><br>
                <input type="submit" value="Submit">
            </p>
        </form>
        <h2>Top-k App Usage Report</h2>
        <h3>Top-k most used apps</h3>
        <form action="/report/topk-apps" method="post">
            <p>
                <label>Start date: <input type="text" name="startDate" placeholder="2015-08-01"></label><br>
                <label>End date: <input type="text" name="endDate" placeholder="2015-08-02"></label><br>
                <label>
                    School:
                    <select name="school">
                        <%
                            for (String school : validSchools) {
                                out.println("<option value=\"" + school + "\">" + school + "</option>");
                            }
                        %>
                    </select>
                </label><br>
                <input type="submit" value="Submit">
            </p>
        </form>
        <h3>Top-k students with most app usage</h3>
        <form action="/report/topk-students" method="post">
            <p>
                <label>Start date: <input type="text" name="startDate" placeholder="2015-08-01"></label><br>
                <label>End date: <input type="text" name="endDate" placeholder="2015-08-02"></label><br>
                <label>
                    School:
                    <select name="appCategory">
                        <%
                            for (String appCategory : validAppCategories) {
                                out.println("<option value=\"" + appCategory + "\">" + appCategory
                                        + "</option>");
                            }
                        %>
                    </select>
                </label><br>
                <input type="submit" value="Submit">
            </p>
        </form>
        <h3>Top-k schools with most app usage</h3>
        <form action="/report/topk-schools" method="post">
            <p>
                <label>Start date: <input type="text" name="startDate" placeholder="2015-08-01"></label><br>
                <label>End date: <input type="text" name="endDate" placeholder="2015-08-02"></label><br>
                <label>
                    School:
                    <select name="appCategory">
                        <%
                            for (String appCategory : validAppCategories) {
                                out.println("<option value=\"" + appCategory + "\">"
                                        + appCategory
                                        + "</option>");
                            }
                        %>
                    </select>
                </label><br>
                <input type="submit" value="Submit">
            </p>
        </form>
        <h2>Smartphone Overuse Report</h2>
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
        <h2>Social Activeness Report</h2>
        <h2>Advanced Smartphone Overuse Report</h2>
        <% }%>
    </body>
</html>
