package com.sprint.controller;

import com.sprint.annotation.AnnotationController;
import com.sprint.annotation.GET;
import com.sprint.annotation.POST;
import com.sprint.annotation.url;
import com.sprint.framework.ModelView;
import com.sprint.framework.MySession;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

@AnnotationController
public class AuthController {
    // Hardcoded user credentials (username -> password)
    private static final Map<String, String> users = Map.of(
        "admin", "admin123",
        "user", "password123"
    );

    @GET
    @url("/login")
    public String showLogin() {
        return "view:/login.jsp";
    }

    @POST
    @url("/login")
    public Object login(HttpServletRequest request, String username, String password, MySession session) {
        System.out.println("[AuthController] Login attempt - username: " + username + ", password: " + password);
        
        if (users.containsKey(username) && users.get(username).equals(password)) {
            System.out.println("[AuthController] Authentication successful for: " + username);
            // Store username in session
            session.add("user", username);
            System.out.println("[AuthController] Stored user in session: " + username);
            
            // Redirect to user data page with context path
            String contextPath = request.getContextPath();
            return "redirect:" + contextPath + "/user-data";
        } else {
            System.out.println("[AuthController] Authentication failed for: " + username);
            String contextPath = request.getContextPath();
            return "redirect:" + contextPath + "/login?error=1";
        }
    }

    @GET
    @url("/user-data")
    public ModelView userData(MySession session) {
        String username = (String) session.get("user");
        if (username == null) {
            // Not logged in, redirect to login
            ModelView modelView = new ModelView();
            modelView.setUrl("/login");
            return modelView;
        }

        // Mock user-specific data
        HashMap<String, Object> data = new HashMap<>();
        data.put("username", username);
        data.put("tasks", List.of("Task 1", "Task 2", "Task 3"));
        
        ModelView modelView = new ModelView();
        modelView.setUrl("/user-data.jsp");
        modelView.setData(data);
        return modelView;
    }

    @GET
    @url("/logout")
    public ModelView logout(MySession session) {
        // Remove user from session
        session.delete("user");
        
        // Redirect to login page
        ModelView modelView = new ModelView();
        modelView.setUrl("/login");
        return modelView;
    }
}
