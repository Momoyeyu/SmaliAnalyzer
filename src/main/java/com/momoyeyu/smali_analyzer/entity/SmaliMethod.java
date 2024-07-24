package com.momoyeyu.smali_analyzer.entity;

import com.momoyeyu.smali_analyzer.analyzers.MethodAnalyzer;

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

    public SmaliMethod(String signature) {
        this.signature = signature;
    }

    public SmaliMethod(String signature, SmaliClass ownerClass) {
        this(signature);
        this.ownerClass = ownerClass;
    }

    public SmaliMethod(String signature, SmaliClass ownerClass, List<String> body) {
        this(signature, ownerClass);
        this.body = body;
    }

    public String getSignature() { return signature; }

    public List<String> getBody() { return body; }

    public String getJavaSignature() {
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

    public void getAnalysis(String methodName, String accessModifier,
                            String staticMofidier, List<String> parametersList, String returnType) {
        this.methodName = methodName;
        this.accessModifier = accessModifier;
        this.staticModifier = staticMofidier;
        this.parametersList = parametersList;
        this.returnType = returnType;
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
}
