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
    </body>
</html>
