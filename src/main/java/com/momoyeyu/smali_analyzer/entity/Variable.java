package com.momoyeyu.smali_analyzer.entity;

public class Variable {
    private final String type;
    private int referenceCount;

    public Variable(String type) {
        this.type = type;
        referenceCount = 0;
    }

    public String getType() {
        return type;
    }

    public int getReferenceCount() {
        return referenceCount;
    }

    public void addReferenceCount() {
        referenceCount++;
    }
}