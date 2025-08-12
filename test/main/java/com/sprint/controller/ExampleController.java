package com.sprint.controller;

import com.sprint.annotation.AnnotationController;
import com.sprint.annotation.GET;
import com.sprint.annotation.url;
import jakarta.servlet.http.*;
import java.io.*;
import com.sprint.framework.ModelView;
import com.sprint.annotation.Param;

@AnnotationController
public class ExampleController {
    @GET
    @url("/hello")
    public String sayHello() {
        return "Hello from annotated method!";
    }

    @GET
    @url("/user")
    public ModelView getUser() {
        ModelView mv = new ModelView("user.jsp");
        mv.addObject("name", "Rina");
        mv.addObject("age", 25);
        return mv;
    }
    
    @GET
    @url("/debug")
    public String debugEndpoint() {
        return "Debug endpoint is working!";
    }

    @GET
    @url("/submitName")
    public ModelView submitName(@Param(name = "nom") String nom) {
        ModelView mv = new ModelView("confirmation.jsp");
        mv.addObject("nomRecupere", nom);
        return mv;
    }
}
