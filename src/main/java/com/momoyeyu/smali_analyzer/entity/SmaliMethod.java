package com.momoyeyu.smali_analyzer.entity;

import com.momoyeyu.smali_analyzer.analyzers.MethodAnalyzer;

import java.util.ArrayList;
import java.util.List;

public class SmaliMethod {
    protected String signature;
    private List<String> body;
    protected SmaliClass ownerClass;

    private String methodName;
    protected String accessModifier;
    protected String staticModifier;
    protected List<String> parametersList;
    private String returnType;
    protected boolean translated;

    public SmaliMethod(String signature) {
        this(signature, null, new ArrayList<>());
    }

    public SmaliMethod(String signature, SmaliClass ownerClass) {
        this(signature, ownerClass, new ArrayList<>());
    }

    public SmaliMethod(String signature, SmaliClass ownerClass, List<String> body) {
        this.signature = signature;
        this.ownerClass = ownerClass;
        if (body != null) {
            this.body = body;
        } else {
            this.body = new ArrayList<>();
        }
    }

    public String getSignature() { return signature; }

    public List<String> getBody() { return body; }

    public String toJava() {
        if (!isTranslated()) {
            translated = true;
            try {
                MethodAnalyzer.translate(this);
            } catch (Exception e) {
                e.printStackTrace();
                translated = false;
                return "[ERROR] unable to translate method: " + signature;
            }
        }
        StringBuilder sb = new StringBuilder();
        if (!accessModifier.equals("default")) {
            sb.append(accessModifier).append(" ");
        }
        if (!staticModifier.equals("instance")) {
            sb.append(staticModifier).append(" ");
        }
        sb.append(returnType).append(" ");
        sb.append(methodName).append("(");
        sb.append(MethodAnalyzer.listParameters(parametersList)).append(");");
        return sb.toString();
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
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

    public List<String> getParametersList() {
        return parametersList;
    }

    public void setParametersList(List<String> parametersList) {
        this.parametersList = parametersList;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public boolean isTranslated() {
        return translated;
    }
}
