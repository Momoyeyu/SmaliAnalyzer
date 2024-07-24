package com.momoyeyu.smali_analyzer.entity;

import com.momoyeyu.smali_analyzer.analyzers.MethodAnalyzer;

import java.util.List;

public class SmaliConstructor extends SmaliMethod {

    private String initType;

    public SmaliConstructor(String signature) {
        super(signature);
    }

    public SmaliConstructor(String signature, SmaliClass ownerClass) {
        super(signature, ownerClass);
    }

    public SmaliConstructor(String signature, SmaliClass ownerClass, List<String> body) {
        super(signature, ownerClass, body);
    }

    @Override
    public String getJavaSignature() {
        StringBuilder sb = new StringBuilder();
        if (!(accessModifier == null) && !accessModifier.isEmpty() && !accessModifier.equals("default")) {
            sb.append(accessModifier).append(" ");
        }
        if (!(staticModifier == null) && !staticModifier.isEmpty() && !staticModifier.equals("instance")) {
            sb.append(staticModifier).append(" ");
        }
        sb.append(ownerClass.getClassName()).append("(");
        sb.append(MethodAnalyzer.listParameters(parametersList)).append(");");
        return sb.toString();
    }

    public void getAnalysis(String accessModifier, String staticMofidier, List<String> parametersList) {
        this.accessModifier = accessModifier;
        this.staticModifier = staticMofidier;
        this.parametersList = parametersList;
    }

    public String getInitType() {
        return initType;
    }

    public void setInitType(String initType) {
        this.initType = initType;
    }
}
