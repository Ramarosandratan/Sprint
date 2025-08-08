package com.sprint.controller;

import com.sprint.annotation.AnnotationController;
import com.sprint.annotation.GET;
import jakarta.servlet.http.*;
import java.io.*;
import com.sprint.framework.ModelView;

@AnnotationController
public class ExampleController {
    @GET("/hello")
    public String sayHello() {
        return "Hello from annotated method!";
    }

    @GET("/user")
    public ModelView getUser() {
        ModelView mv = new ModelView("user.jsp");
        mv.addObject("name", "Rina");
        mv.addObject("age", 25);
        return mv;
    }
    
    @GET("/debug")
    public String debugEndpoint() {
        return "Debug endpoint is working!";
    }
}
