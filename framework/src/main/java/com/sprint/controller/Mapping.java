package com.sprint.controller;

public class Mapping {
    private String className;
    private String methodName;
    private Class<?>[] parameterTypes;
    private String httpMethod; // "GET" or "POST"

    public Mapping(String className, String methodName, Class<?>[] parameterTypes, String httpMethod) {
        this.className = className;
        this.methodName = methodName;
        this.parameterTypes = parameterTypes;
        this.httpMethod = httpMethod;
    }

    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    @Override
    public String toString() {
        return "Mapping [className=" + className + ", methodName=" + methodName + ", httpMethod=" + httpMethod + "]";
    }
}
