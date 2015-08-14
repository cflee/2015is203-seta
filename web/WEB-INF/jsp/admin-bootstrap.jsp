<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>SMUA Admin Page</title>
    </head>
    <body>
        <h1>Admin Page</h1>
        <h2>Bootstrap</h2>
        <form action="/admin/bootstrap" method="post" enctype="multipart/form-data">
            <p>
                <input type="file" name="bootstrap-file">
                <input type="submit" value="Submit">
            </p>
        </form>
        <h2>Add Data</h2>
        <h2>Bootstrap Location Data</h2>
        <h2>Add Location Data</h2>
        <h2>Delete Location Data</h2>
    </body>
</html>
