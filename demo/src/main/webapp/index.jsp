<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Sprint Framework Demo</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 40px; }
        .container { max-width: 600px; margin: 0 auto; text-align: center; }
        .welcome { background: #f9f9f9; padding: 40px; border-radius: 10px; }
        .button { display: inline-block; padding: 10px 20px; margin: 10px; background: #007bff; color: white; text-decoration: none; border-radius: 5px; }
    </style>
</head>
<body>
    <div class="container">
        <div class="welcome">
            <h1>Welcome to Sprint Framework Demo</h1>
            <p>This is a demonstration of the multi-module Maven project structure with the Sprint MVC framework.</p>
            <a href="${pageContext.request.contextPath}/hello" class="button">Hello Demo</a>
            <a href="${pageContext.request.contextPath}/info" class="button">Framework Info</a>
        </div>
    </div>
</body>
</html>
