package com.sprint.controller;

import com.sprint.annotation.AnnotationController;
import com.sprint.annotation.GET;
import com.sprint.annotation.ModelAttribute;
import com.sprint.annotation.Param;
import com.sprint.annotation.url;
import com.sprint.framework.ModelView;
import com.sprint.model.User;
import jakarta.servlet.http.HttpServletRequest;

@AnnotationController
public class UserController {

    @GET
    @url("/saveUser")
    public ModelView saveUser(@ModelAttribute User user) {
        ModelView modelView = new ModelView("confirmation.jsp");
        modelView.addObject("user", user);
        return modelView;
    }

    // New endpoint for JSON API
    @GET
    @url("/api/saveUser")
    public User saveUserApi(@ModelAttribute User user) {
        return user;
    }
}
