<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Login</title>
    </head>
    <body>
        <h1>Login</h1>
        <% if (request.getAttribute("errorMessage") != null) {%>
        <p style="color: red;"><%= request.getAttribute("errorMessage")%></p>
        <% } %>
        <form action="/login" method="post">
            <label>Username: <input type="text" name="username" placeholder="cflee.2013"></label>
            <label>Password: <input type="password" name="password" placeholder="12345678"></label>
            <input type="submit" value="Login">
        </form>
    </body>
</html>
<% session.removeAttribute("username");%>
