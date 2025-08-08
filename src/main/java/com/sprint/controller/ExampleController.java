package com.sprint.controller;

import com.sprint.annotation.AnnotationController;
import com.sprint.annotation.GET;
import jakarta.servlet.http.*;
import java.io.*;

@AnnotationController
public class ExampleController {
    @GET("/hello")
    public String sayHello() {
        return "Hello from annotated method!";
    }
}
