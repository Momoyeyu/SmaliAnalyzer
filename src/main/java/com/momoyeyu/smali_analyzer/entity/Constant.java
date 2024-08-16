package com.momoyeyu.smali_analyzer.entity;

public class Constant extends Variable {
    private String value;

    public Constant(String value) {
        super("const");
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
