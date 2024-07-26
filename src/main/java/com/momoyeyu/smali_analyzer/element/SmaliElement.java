package com.momoyeyu.smali_analyzer.element;

import com.momoyeyu.smali_analyzer.utils.Logger;

public class SmaliElement {
    // properties
    protected String signature;
    protected String name;
    // modifiers
    protected String accessModifier;
    protected boolean staticModifier;
    protected boolean finalModifier;
    protected boolean syntheticModifier;
    // status
    protected boolean translated;

    public SmaliElement(String signature) {
        this.signature = signature;
        accessModifier = "default";
        staticModifier = false;
        finalModifier = false;
        syntheticModifier = false;
        translated = false;
    }

    @Override
    public String toString() {
        return signature;
    }

    public String getSignature() {
        return signature;
    }

    public String toJava() {
        return Logger.failToAnalyze("element", signature);
    }

    public boolean isTranslated() {
        return translated;
    }

    // setter
    public void setFinalModifier(String finalModifier) {
        this.finalModifier = finalModifier != null;
    }

    public void setFinalModifier(boolean finalModifier) {
        this.finalModifier = finalModifier;
    }

    public void setSyntheticModifier(boolean syntheticModifier) {
        this.syntheticModifier = syntheticModifier;
    }

    public void setStaticModifier(String staticModifier) {
        this.staticModifier = staticModifier != null;
    }

    public void setStaticFlag(boolean staticFlag) {
        this.staticModifier = staticFlag;
    }

    public void setAccessModifier(String accessModifier) {
        this.accessModifier = accessModifier == null ? "default" : accessModifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
