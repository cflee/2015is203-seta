<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Error</title>
    </head>
    <body>
        <h1>Error!</h1>
        <p>
            <%= request.getAttribute("errorMessage")%>
        </p>
        <p>
            <a href="/">Back to root</a>
        </p>
    </body>
</html>
