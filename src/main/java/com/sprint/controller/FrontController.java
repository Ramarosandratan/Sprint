package com.sprint.controller;

import java.io.*;
import java.util.*;
import java.lang.reflect.*;
import java.net.URL;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import com.sprint.annotation.AnnotationController;
import com.sprint.annotation.GET;
import com.sprint.annotation.POST;
import com.sprint.annotation.Param;
import com.sprint.annotation.ModelAttribute;
import com.sprint.annotation.ParamField;
import com.sprint.controller.Mapping;
import com.sprint.framework.ModelView;
import com.sprint.framework.MySession;
import java.util.Map;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;

@AnnotationController
public class FrontController extends HttpServlet {
    private HashMap<String, Mapping> mappings = new HashMap<>();
    private boolean scanned = false;
    private Jsonb jsonb;

    public void init() {
        String packageName = getInitParameter("controller-package");
        if (packageName != null && !scanned) {
            scanControllers(packageName);
            scanned = true;
        }
        try {
            jsonb = JsonbBuilder.create();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create JSON-B instance", e);
        }
    }
    
    @Override
    public void destroy() {
        try {
            if (jsonb != null) {
                jsonb.close();
            }
        } catch (Exception e) {
            // Log error
        }
        super.destroy();
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
                                    mappings.put("GET:" + url, new Mapping(clazz.getName(), method.getName(), method.getParameterTypes(), "GET"));
                                    System.out.println("[DEBUG] Mapped GET: " + url + " -> " + 
                                                     clazz.getSimpleName() + "." + method.getName());
                                    mappingCount++;
                                }
                                if (method.isAnnotationPresent(POST.class)) {
                                    POST postAnnotation = method.getAnnotation(POST.class);
                                    String url = postAnnotation.value();
                                    mappings.put("POST:" + url, new Mapping(clazz.getName(), method.getName(), method.getParameterTypes(), "POST"));
                                    System.out.println("[DEBUG] Mapped POST: " + url + " -> " + 
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
            String httpMethod = request.getMethod(); // GET, POST, etc.
            String path = request.getServletPath();
            if (path == null || path.isEmpty()) {
                path = "/";
            } else if (!path.startsWith("/")) {
                path = "/" + path;
            }
            // Truncate query parameters
            if (path.contains("?")) {
                path = path.substring(0, path.indexOf("?"));
            }
            // Normalize path: remove trailing slashes
            path = path.replaceAll("/+$", "");
            String key = httpMethod + ":" + path;
            System.out.println("[DEBUG] Processing request: " + key);

            // Handle static resources using default servlet
            if (path.matches(".*\\.(html|css|js|png|jpg|jpeg|gif|ico)$")) {
                RequestDispatcher rd = request.getServletContext().getNamedDispatcher("default");
                rd.forward(request, response);
                return;
            }
            
            Mapping mapping = mappings.get(key);
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
                        if (parameters[i].getType() == HttpServletRequest.class) {
                            // Handle HttpServletRequest parameter
                            args[i] = request;
                        } else if (parameters[i].getType() == MySession.class) {
                            // Handle MySession parameter
                            HttpSession httpSession = request.getSession();
                            args[i] = new MySession(httpSession);
                        } else if (parameters[i].isAnnotationPresent(ModelAttribute.class)) {
                            // Handle object parameter
                            Class<?> paramType = parameters[i].getType();
                            Object obj = instantiateObject(paramType);
                            populateObjectFromRequest(obj, request);
                            args[i] = obj;
                        } else {
                            // Handle simple parameter
                            Param paramAnnotation = parameters[i].getAnnotation(Param.class);
                            String paramName;
                            if (paramAnnotation != null) {
                                paramName = paramAnnotation.name();
                            } else {
                                paramName = parameters[i].getName();
                            }
                            String value = request.getParameter(paramName);
                            args[i] = convertValue(value, parameters[i].getType());
                        }
                    }
            
            // Invoke method with arguments
            Object result = method.invoke(instance, args);
                
            // Handle different return types
            if (result instanceof String) {
                String resultStr = (String) result;
                if (resultStr.startsWith("redirect:")) {
                    // Redirect to URL
                    String redirectUrl = resultStr.substring(9);
                    response.sendRedirect(redirectUrl);
                } else if (resultStr.startsWith("view:")) {
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
                    // JSON response handling
                    response.setContentType("application/json");
                    String json = jsonb.toJson(result);
                    out.println(json);
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

    // Helper method to instantiate object
    private Object instantiateObject(Class<?> clazz) throws ServletException {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new ServletException("Failed to instantiate object: " + clazz.getName(), e);
        }
    }

    // Helper method to populate object fields from request
    private void populateObjectFromRequest(Object obj, HttpServletRequest request) {
        Class<?> clazz = obj.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            ParamField paramField = field.getAnnotation(ParamField.class);
            String paramName = (paramField != null && !paramField.value().isEmpty()) ? 
                                paramField.value() : field.getName();
            String value = request.getParameter(paramName);
            if (value != null) {
                try {
                    field.setAccessible(true);
                    field.set(obj, convertValue(value, field.getType()));
                } catch (IllegalAccessException e) {
                    // Log or handle exception
                }
            }
        }
    }

    // Helper method for type conversion
    private Object convertValue(String value, Class<?> targetType) {
        if (value == null || value.isEmpty()) {
            // Return default values for primitive types
            if (targetType == int.class) return 0;
            if (targetType == double.class) return 0.0;
            if (targetType == boolean.class) return false;
            if (targetType == long.class) return 0L;
            return null;
        }
        
        try {
            if (targetType == String.class) {
                return value;
            } else if (targetType == int.class || targetType == Integer.class) {
                return Integer.parseInt(value);
            } else if (targetType == double.class || targetType == Double.class) {
                return Double.parseDouble(value);
            } else if (targetType == boolean.class || targetType == Boolean.class) {
                return Boolean.parseBoolean(value);
            } else if (targetType == long.class || targetType == Long.class) {
                return Long.parseLong(value);
            }
        } catch (NumberFormatException e) {
            // Return default values on conversion error
            if (targetType == int.class) return 0;
            if (targetType == double.class) return 0.0;
            if (targetType == boolean.class) return false;
            if (targetType == long.class) return 0L;
        }
        return value;
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
