<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Sprint Demo - Hello</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 40px; }
        .container { max-width: 600px; margin: 0 auto; }
        .message { background: #f0f0f0; padding: 20px; border-radius: 5px; }
    </style>
</head>
<body>
    <div class="container">
        <h1>Sprint Framework Demo</h1>
        <div class="message">
            <h2>${message}</h2>
            <p>Generated at: ${timestamp}</p>
        </div>
        <p>
            <a href="${pageContext.request.contextPath}/info">View Framework Info</a>
        </p>
    </div>
</body>
</html>
