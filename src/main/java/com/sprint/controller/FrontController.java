package com.sprint.controller;

import java.io.*;
import java.util.*;
import java.lang.reflect.*;
import java.net.URL;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import com.sprint.annotation.AnnotationController;
import com.sprint.annotation.GET;
import com.sprint.controller.Mapping;

@AnnotationController
public class FrontController extends HttpServlet {
    private HashMap<String, Mapping> mappings = new HashMap<>();
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
                                    for (Method method : clazz.getDeclaredMethods()) {
                                        if (method.isAnnotationPresent(GET.class)) {
                                            GET getAnnotation = method.getAnnotation(GET.class);
                                            String url = getAnnotation.value();
                                            mappings.put(url, new Mapping(clazz.getName(), method.getName()));
                                        }
                                    }
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
            String path = request.getServletPath();
            if (path == null || path.isEmpty()) {
                path = "/";
            }
            
            Mapping mapping = mappings.get(path);
            if (mapping != null) {
                try {
                    // Load class
                    Class<?> clazz = Class.forName(mapping.getClassName());
                    
                    // Create instance
                    Object instance = clazz.getDeclaredConstructor().newInstance();
                    
                    // Get method
                    Method method = clazz.getDeclaredMethod(mapping.getMethodName());
                    
                    // Invoke method and get result
                    Object result = method.invoke(instance);
                    
                    // Write result to response
                    out.println(result);
                } catch (ClassNotFoundException e) {
                    out.println("Classe non trouvée: " + e.getMessage());
                } catch (NoSuchMethodException e) {
                    out.println("Méthode non trouvée: " + e.getMessage());
                } catch (IllegalAccessException e) {
                    out.println("Accès non autorisé: " + e.getMessage());
                } catch (InvocationTargetException e) {
                    out.println("Erreur dans la méthode: " + e.getCause().getMessage());
                } catch (InstantiationException e) {
                    out.println("Erreur d'instanciation: " + e.getMessage());
                }
            } else {
                out.println("Aucune méthode associée à l’URL");
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
