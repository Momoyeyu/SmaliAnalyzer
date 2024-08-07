package com.momoyeyu.smali_analyzer.entity;

import java.util.HashMap;
import java.util.Map;

public class Variable {
    private String type;
    private String value;
    private Map<String, Variable> properties;

    public Variable(String type, String value) {
        this.value = value;
        properties = new HashMap<>();
    }

    // getter
    public String getValue() {
        return value;
    }

    public Variable getProperty(String key) {
        return properties.get(key);
    }

    // setter
    public void setValue(String value) {
        this.value = value;
    }

    public void setProperty(String key, Variable var) {
        properties.put(key, var);
    }
}