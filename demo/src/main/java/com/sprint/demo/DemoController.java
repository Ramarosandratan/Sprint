package com.sprint.demo;

import com.sprint.annotation.GET;
import com.sprint.annotation.url;
import com.sprint.annotation.AnnotationController;
import com.sprint.framework.ModelView;

@AnnotationController
public class DemoController {
    
    @url("/hello")
    @GET
    public ModelView hello() {
        ModelView mv = new ModelView("hello.jsp");
        mv.addObject("message", "Hello from demo module! This controller uses the Sprint Framework.");
        mv.addObject("timestamp", new java.util.Date());
        return mv;
    }
    
    @url("/info")
    @GET
    public ModelView info() {
        ModelView mv = new ModelView("info.jsp");
        mv.addObject("title", "Sprint Framework Demo");
        mv.addObject("description", "This is a demonstration of the multi-module Maven project structure");
        return mv;
    }
    
    @url("/")
    @GET
    public ModelView home() {
        return new ModelView("index.jsp");
    }
}
