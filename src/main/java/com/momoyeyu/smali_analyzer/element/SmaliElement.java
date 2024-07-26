package com.momoyeyu.smali_analyzer.element;

import com.momoyeyu.smali_analyzer.utils.Logger;

public class SmaliElement {

    protected String signature;
    protected String name;

    protected String accessModifier;
    protected String staticModifier;
    protected String finalModifier;
    protected boolean synthetic;

    protected boolean translated;

    public SmaliElement(String signature) {
        this.signature = signature;
        accessModifier = "default";
        staticModifier = "default";
        finalModifier = "default";
        synthetic = false;
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
        this.finalModifier = finalModifier == null ? "default" : finalModifier;
    }

    public void setSynthetic(boolean synthetic) {
        this.synthetic = synthetic;
    }

    public void setStaticModifier(String staticModifier) {
        this.staticModifier = staticModifier == null ? "default" : staticModifier;
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
