package com.sprint.controller;

import java.io.*;
import java.util.*;
import java.lang.reflect.*;
import java.net.URL;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import com.sprint.annotation.AnnotationController;
import com.sprint.annotation.GET;
import com.sprint.annotation.Param;
import com.sprint.controller.Mapping;
import com.sprint.framework.ModelView;
import java.util.Map;

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
        System.out.println("[DEBUG] Scanning package: " + packageName);
        String path = packageName.replace('.', '/');
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL resource = classLoader.getResource(path);
        int mappingCount = 0;
        
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
                                            mappings.put(url, new Mapping(clazz.getName(), method.getName(), method.getParameterTypes()));
                                            System.out.println("[DEBUG] Mapped URL: " + url + " -> " + 
                                                             clazz.getSimpleName() + "." + method.getName());
                                            mappingCount++;
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
            // Truncate query parameters
            if (path.contains("?")) {
                path = path.substring(0, path.indexOf("?"));
            }
            // Normalize path: remove trailing slashes
            path = path.replaceAll("/+$", "");
            System.out.println("[DEBUG] Processing request for path: " + path);

            // Handle static resources using default servlet
            if (path.matches(".*\\.(html|css|js|png|jpg|jpeg|gif|ico)$")) {
                RequestDispatcher rd = request.getServletContext().getNamedDispatcher("default");
                rd.forward(request, response);
                return;
            }
            
            Mapping mapping = mappings.get(path);
            if (mapping != null) {
                try {
                    // Load class
                    Class<?> clazz = Class.forName(mapping.getClassName());
                    
                    // Create instance
                    Object instance = clazz.getDeclaredConstructor().newInstance();
                    
                    // Get method with parameter types
                    Method method = clazz.getDeclaredMethod(
                        mapping.getMethodName(), 
                        mapping.getParameterTypes()
                    );
                    
                    // Get method parameters
                    Parameter[] parameters = method.getParameters();
                    Object[] args = new Object[parameters.length];
                    
                    for (int i = 0; i < parameters.length; i++) {
                        Param paramAnnotation = parameters[i].getAnnotation(Param.class);
                        if (paramAnnotation != null) {
                            String paramName = paramAnnotation.name();
                            args[i] = request.getParameter(paramName);
                        } else {
                            throw new ServletException("Parameter " + i + " in method " + method.getName() + " is not annotated with @Param");
                        }
                    }
            
            // Invoke method with arguments
            Object result = method.invoke(instance, args);
                
                // Handle different return types
                if (result instanceof String) {
                    String resultStr = (String) result;
                    if (resultStr.startsWith("view:")) {
                        // View dispatch
                        String viewPath = resultStr.substring(5);
                        RequestDispatcher dispatcher = request.getRequestDispatcher(viewPath);
                        dispatcher.forward(request, response);
                    } else {
                        // Direct content
                        out.println(resultStr);
                    }
                } else if (result instanceof ModelView) {
                    // ModelView handling
                    ModelView mv = (ModelView) result;
                    for (Map.Entry<String, Object> entry : mv.getData().entrySet()) {
                        request.setAttribute(entry.getKey(), entry.getValue());
                    }
                    RequestDispatcher dispatcher = request.getRequestDispatcher(mv.getUrl());
                    dispatcher.forward(request, response);
                } else {
                    // Fallback to string representation
                    out.println(result);
                }
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
        } catch (ServletException e) {
            out.println("Erreur de servlet: " + e.getMessage());
        }
            } else {
                System.out.println("[DEBUG] No mapping found for path: " + path);
                out.println("Aucune méthode associée à l’URL");
            }
        } finally {
            if (!response.isCommitted()) {
                out.close();
            }
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
