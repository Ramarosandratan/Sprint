<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Sprint Demo - Info</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 40px; }
        .container { max-width: 800px; margin: 0 auto; }
        .info-box { background: #e8f4f8; padding: 20px; border-radius: 5px; margin: 10px 0; }
    </style>
</head>
<body>
    <div class="container">
        <h1>${title}</h1>
        <div class="info-box">
            <h2>Project Structure</h2>
            <p>${description}</p>
        </div>
        <div class="info-box">
            <h2>Features Demonstrated</h2>
            <ul>
                <li>Multi-module Maven project structure</li>
                <li>Custom MVC framework (Sprint)</li>
                <li>Annotation-based routing</li>
                <li>JSP view rendering</li>
                <li>Model-View-Controller pattern</li>
            </ul>
        </div>
        <p>
            <a href="${pageContext.request.contextPath}/hello">Back to Hello</a>
        </p>
    </div>
</body>
</html>
