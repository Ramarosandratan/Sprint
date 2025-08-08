package com.sprint.controller;

public class Mapping {
    private String className;
    private String methodName;
    private Class<?>[] parameterTypes;

    public Mapping(String className, String methodName, Class<?>[] parameterTypes) {
        this.className = className;
        this.methodName = methodName;
        this.parameterTypes = parameterTypes;
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

    @Override
    public String toString() {
        return "Mapping [className=" + className + ", methodName=" + methodName + "]";
    }
}
