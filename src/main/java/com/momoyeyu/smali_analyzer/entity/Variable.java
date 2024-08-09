package com.momoyeyu.smali_analyzer.entity;

public class Variable {
    private String type;

    public Variable(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}