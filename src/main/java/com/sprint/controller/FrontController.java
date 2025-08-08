package com.sprint.controller;

import java.io.*;
import java.util.*;
import java.lang.reflect.*;
import java.net.URL;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import com.sprint.annotation.AnnotationController;

@AnnotationController
public class FrontController extends HttpServlet {
    private List<String> controllers = new ArrayList<>();
    private boolean scanned = false;

    public void init() {
        String packageName = getInitParameter("controller-package");
        if (packageName != null && !scanned) {
            scanControllers(packageName);
            scanned = true;
        }
    }

    private void scanControllers(String packageName) {
        String path = packageName.replace('.', '/');
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL resource = classLoader.getResource(path);
        
        if (resource != null) {
            File directory = new File(resource.getFile());
            if (directory.exists()) {
                File[] files = directory.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (file.isFile() && file.getName().endsWith(".class")) {
                            String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                            try {
                                Class<?> clazz = Class.forName(className);
                                if (clazz.isAnnotationPresent(AnnotationController.class)) {
                                    controllers.add(clazz.getSimpleName());
                                }
                            } catch (ClassNotFoundException e) {
                                // Ignorer les classes non trouvées
                            }
                        }
                    }
                }
            }
        }
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            out.println("Liste des contrôleurs annotés :<br>");
            for (String controller : controllers) {
                out.println("- " + controller + "<br>");
            }
        } finally {
            out.close();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
