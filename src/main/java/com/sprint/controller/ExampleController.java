package com.sprint.controller;

import com.sprint.annotation.AnnotationController;
import com.sprint.annotation.GET;
import jakarta.servlet.http.*;
import java.io.*;

@AnnotationController
public class ExampleController {
    @GET("/hello")
    public void sayHello(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("text/html;charset=UTF-8");
        PrintWriter out = res.getWriter();
        try {
            out.println("Hello from annotated method!");
        } finally {
            out.close();
        }
    }
}
