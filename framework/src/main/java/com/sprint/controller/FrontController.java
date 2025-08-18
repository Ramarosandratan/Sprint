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
import com.sprint.annotation.url;
import com.sprint.exception.MethodNotAllowedException;
import com.sprint.exception.NotFoundException;
import com.sprint.controller.Mapping;
import com.sprint.framework.ModelView;
import com.sprint.framework.MySession;
import java.util.Map;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@AnnotationController
public class FrontController extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(FrontController.class);
    private HashMap<String, Mapping> mappings = new HashMap<>();
    private HashMap<String, Object> controllerInstances = new HashMap<>();
    private boolean scanned = false;
    private Jsonb jsonb;

    public void init() {
        String controllerClasses = getInitParameter("controller-classes");
        if (controllerClasses != null && !controllerClasses.isEmpty() && !scanned) {
            // Chargement explicite des classes contrôleurs
            String[] classNames = controllerClasses.split(",");
            for (String className : classNames) {
                className = className.trim();
                try {
                    Class<?> clazz = Class.forName(className);
                    if (clazz.isAnnotationPresent(AnnotationController.class)) {
                        for (Method method : clazz.getDeclaredMethods()) {
                            boolean hasGet = method.isAnnotationPresent(GET.class);
                            boolean hasPost = method.isAnnotationPresent(POST.class);
                            boolean hasUrl = method.isAnnotationPresent(url.class);

                            if (!hasUrl) continue;
                            if (!hasGet && !hasPost) hasGet = true;
                            if (hasGet && hasPost) throw new RuntimeException("Method cannot have both @GET and @POST: " + method.getName());

                            String verb = hasGet ? "GET" : "POST";
                            String urlPath = method.getAnnotation(url.class).value();
                            mappings.put(verb + ":" + urlPath, new Mapping(clazz.getName(), method.getName(), method.getParameterTypes(), verb));
                        }
                    }
                } catch (ClassNotFoundException e) {
                    // Ignorer les classes non trouvées
                }
            }
            scanned = true;
        } else {
            String packageName = getInitParameter("controller-package");
            if (packageName != null && !scanned) {
                scanControllers(packageName);
                scanned = true;
            }
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
        logger.debug("Scanning package: {}", packageName);
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
                    boolean hasGet = method.isAnnotationPresent(GET.class);
                    boolean hasPost = method.isAnnotationPresent(POST.class);
                    boolean hasUrl = method.isAnnotationPresent(url.class);

                    // Skip methods without URL annotation
                    if (!hasUrl) continue;

                    // Handle methods without explicit annotation (default to GET)
                    if (!hasGet && !hasPost) {
                        hasGet = true;
                    }

                    if (hasGet && hasPost) {
                        throw new RuntimeException("Method cannot have both @GET and @POST: " + 
                                                  method.getName());
                    }

                    String verb = hasGet ? "GET" : "POST";
                    String urlPath = method.getAnnotation(url.class).value();

                    mappings.put(verb + ":" + urlPath, new Mapping(clazz.getName(), method.getName(), method.getParameterTypes(), verb));
                    logger.debug("Mapped {}: {} -> {}.{}", verb, urlPath, clazz.getSimpleName(), method.getName());
                    mappingCount++;
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
            String httpMethod = request.getMethod();
            String path = normalizePath(request.getServletPath());
            String key = httpMethod + ":" + path;
            logger.debug("Processing request: {}", key);

            // Gestion des ressources statiques
            if (isStaticResource(path)) {
                RequestDispatcher rd = request.getServletContext().getNamedDispatcher("default");
                rd.forward(request, response);
                return;
            }

            Mapping mapping = mappings.get(key);
            if (mapping != null) {
                if (!mapping.getHttpMethod().equals(httpMethod)) {
                    throw new MethodNotAllowedException("Method " + httpMethod + " not allowed for URL " + path);
                }
                try {
                    Object result = invokeController(mapping, request, response);
                    handleControllerResult(result, request, response, out);
                } catch (IllegalArgumentException e) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.println("Erreur de paramètre : " + e.getMessage());
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
                boolean pathExists = false;
                for (String mappingKey : mappings.keySet()) {
                    if (mappingKey.endsWith(path)) {
                        pathExists = true;
                        break;
                    }
                }
                if (pathExists) {
                    throw new MethodNotAllowedException("Method " + httpMethod + " not allowed for URL " + path);
                } else {
                    throw new NotFoundException("Resource " + path + " not found");
                }
            }
        } catch (MethodNotAllowedException e) {
            response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            response.getWriter().println(e.getMessage());
        } catch (NotFoundException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().println(e.getMessage());
        } finally {
            if (!response.isCommitted()) {
                out.close();
            }
        }
    }

    // Nouvelle méthode : normalisation du chemin
    private String normalizePath(String path) {
        if (path == null || path.isEmpty()) {
            path = "/";
        } else if (!path.startsWith("/")) {
            path = "/" + path;
        }
        if (path.contains("?")) {
            path = path.substring(0, path.indexOf("?"));
        }
        path = path.replaceAll("/+$", "");
        return path;
    }

    // Nouvelle méthode : détection ressource statique
    private boolean isStaticResource(String path) {
        return path.matches(".*\\.(html|css|js|png|jpg|jpeg|gif|ico)$");
    }

    // Nouvelle méthode : invocation du contrôleur
    private Object invokeController(Mapping mapping, HttpServletRequest request, HttpServletResponse response)
            throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, ServletException {
        Class<?> clazz = Class.forName(mapping.getClassName());
        Object instance = controllerInstances.get(clazz.getName());
        if (instance == null) {
            instance = clazz.getDeclaredConstructor().newInstance();
            controllerInstances.put(clazz.getName(), instance);
        }
        Method method = clazz.getDeclaredMethod(mapping.getMethodName(), mapping.getParameterTypes());
        Parameter[] parameters = method.getParameters();
        Object[] args = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].getType() == HttpServletRequest.class) {
                args[i] = request;
            } else if (parameters[i].getType() == MySession.class) {
                HttpSession httpSession = request.getSession();
                args[i] = new MySession(httpSession);
            } else if (parameters[i].isAnnotationPresent(ModelAttribute.class)) {
                Class<?> paramType = parameters[i].getType();
                Object obj = instantiateObject(paramType);
                populateObjectFromRequest(obj, request);
                args[i] = obj;
            } else {
                Param paramAnnotation = parameters[i].getAnnotation(Param.class);
                String paramName = (paramAnnotation != null) ? paramAnnotation.name() : parameters[i].getName();
                String value = request.getParameter(paramName);
                args[i] = convertValue(value, parameters[i].getType());
            }
        }
        return method.invoke(instance, args);
    }

    // Nouvelle méthode : gestion du résultat du contrôleur
    private void handleControllerResult(Object result, HttpServletRequest request, HttpServletResponse response, PrintWriter out)
            throws ServletException, IOException {
        if (result instanceof String) {
            String resultStr = (String) result;
            if (resultStr.startsWith("redirect:")) {
                String redirectUrl = resultStr.substring(9);
                response.sendRedirect(redirectUrl);
            } else if (resultStr.startsWith("view:")) {
                String viewPath = resultStr.substring(5);
                RequestDispatcher dispatcher = request.getRequestDispatcher(viewPath);
                dispatcher.forward(request, response);
            } else {
                out.println(resultStr);
            }
        } else if (result instanceof ModelView) {
            ModelView mv = (ModelView) result;
            for (Map.Entry<String, Object> entry : mv.getData().entrySet()) {
                request.setAttribute(entry.getKey(), entry.getValue());
            }
            RequestDispatcher dispatcher = request.getRequestDispatcher(mv.getUrl());
            dispatcher.forward(request, response);
        } else {
            response.setContentType("application/json");
            String json = jsonb.toJson(result);
            out.println(json);
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
            if (targetType.isPrimitive()) {
                if (targetType == int.class) return 0;
                if (targetType == double.class) return 0.0;
                if (targetType == boolean.class) return false;
                if (targetType == long.class) return 0L;
            }
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
            throw new IllegalArgumentException("Format invalide pour le type " + targetType.getSimpleName() + " : " + value);
        }
        throw new IllegalArgumentException("Type non supporté : " + targetType.getSimpleName());
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
