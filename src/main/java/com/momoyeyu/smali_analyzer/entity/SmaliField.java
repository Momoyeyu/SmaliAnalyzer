package com.momoyeyu.smali_analyzer.entity;

public class SmaliField {
    private String signature;
    private String accessModifier;
    private String staticModifier;
    private String finalModifier;
    private boolean isArray;
    private String type;

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getAccessModifier() {
        return accessModifier;
    }

    public void setAccessModifier(String accessModifier) {
        this.accessModifier = accessModifier;
    }

    public String getStaticModifier() {
        return staticModifier;
    }

    public void setStaticModifier(String staticModifier) {
        this.staticModifier = staticModifier;
    }

    public String getFinalModifier() {
        return finalModifier;
    }

    public void setFinalModifier(String finalModifier) {
        this.finalModifier = finalModifier;
    }

    public boolean isArray() {
        return isArray;
    }

    public void setArray(boolean array) {
        isArray = array;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
