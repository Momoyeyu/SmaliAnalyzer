package com.momoyeyu.smali_analyzer.element;

public class SmaliElement {

    protected String signature;
    protected String name;

    protected String accessModifier;
    protected String staticModifier;
    protected String finalModifier;

    protected boolean translated;

    @Override
    public String toString() {
        return signature;
    }

    public String toJava() {
        return "[ERROR] unknown element: " + this;
    }

    public boolean isTranslated() {
        return translated;
    }

    public String getFinalModifier() {
        return finalModifier;
    }

    public void setFinalModifier(String finalModifier) {
        this.finalModifier = finalModifier;
    }

    public String getStaticModifier() {
        return staticModifier;
    }

    public void setStaticModifier(String staticModifier) {
        this.staticModifier = staticModifier;
    }

    public String getAccessModifier() {
        return accessModifier;
    }

    public void setAccessModifier(String accessModifier) {
        this.accessModifier = accessModifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
