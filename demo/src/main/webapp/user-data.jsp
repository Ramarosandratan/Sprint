<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>User Data</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f0f0f0;
            margin: 0;
            padding: 20px;
        }
        .container {
            max-width: 800px;
            margin: 0 auto;
            background-color: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        h1 {
            color: #333;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        .logout-btn {
            background-color: #dc3545;
            color: white;
            border: none;
            padding: 8px 16px;
            border-radius: 4px;
            cursor: pointer;
            font-size: 14px;
        }
        .logout-btn:hover {
            background-color: #bd2130;
        }
        .task-list {
            list-style-type: none;
            padding: 0;
        }
        .task-item {
            background-color: #f8f9fa;
            border: 1px solid #dee2e6;
            padding: 10px;
            margin-bottom: 8px;
            border-radius: 4px;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>
            Bonjour, <%= request.getAttribute("username") %>!
            <a href="${pageContext.request.contextPath}/logout">
                <button class="logout-btn">Déconnexion</button>
            </a>
        </h1>
        
        <h2>Vos tâches:</h2>
        <ul class="task-list">
            <% 
            java.util.List<String> tasks = (java.util.List<String>) request.getAttribute("tasks");
            for (String task : tasks) { 
            %>
                <li class="task-item"><%= task %></li>
            <% } %>
        </ul>
    </div>
</body>
</html>
