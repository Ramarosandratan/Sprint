package com.sprint.framework;

import java.util.HashMap;

public class ModelView {
    private String url;
    private HashMap<String, Object> data = new HashMap<>();

    public ModelView() {}

    public ModelView(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public HashMap<String, Object> getData() {
        return data;
    }

    public void setData(HashMap<String, Object> data) {
        this.data = data;
    }

    public void addObject(String name, Object value) {
        data.put(name, value);
    }
}
