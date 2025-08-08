package com.sprint.controller;

import com.sprint.annotation.AnnotationController;
import com.sprint.annotation.GET;
import com.sprint.annotation.ModelAttribute;
import com.sprint.framework.ModelView;
import com.sprint.model.User;

@AnnotationController
public class UserController {
    
    @GET("/saveUser")
    public ModelView saveUser(@ModelAttribute User user) {
        ModelView mv = new ModelView("user.jsp");
        mv.addObject("user", user);
        return mv;
    }
}
