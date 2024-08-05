package com.momoyeyu.smali_analyzer.entity;

import java.util.HashMap;
import java.util.Map;

public class Variable {
    private String type;
    private String value;
    private Map<String, String> properties;

    public Variable(String type, String value) {
        this.type = type;
        this.value = value;
        properties = new HashMap<>();
    }

    // getter
    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public String getProperty(String key) {
        return properties.get(key);
    }

    // setter
    public void setValue(String value) {
        this.value = value;
    }

    public void setProperty(String key, String value) {
        properties.put(key, value);
    }
}