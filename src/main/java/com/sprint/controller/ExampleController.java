package com.sprint.controller;

import com.sprint.annotation.AnnotationController;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;

@AnnotationController
public class ExampleController extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse res) 
        throws ServletException, IOException {
        res.setContentType("text/html;charset=UTF-8");
        PrintWriter out = res.getWriter();
        try {
            out.println("Hello from ExampleController!");
        } finally {
            out.close();
        }
    }
}
