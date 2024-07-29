package com.momoyeyu.smali_analyzer.element;

import com.momoyeyu.smali_analyzer.analyzers.MethodAnalyzer;
import com.momoyeyu.smali_analyzer.utils.Logger;
import com.momoyeyu.smali_analyzer.utils.TypeTranslator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SmaliMethod extends SmaliElement {
    private List<String> body;
    protected SmaliClass ownerClass;

    protected List<String> parametersList;
    private boolean abstractModifier;
    private String returnType;

    public SmaliMethod(String signature) {
        this(signature, null, new ArrayList<>());
    }

    public SmaliMethod(String signature, SmaliClass ownerClass, List<String> body) {
        super(signature);
        this.ownerClass = ownerClass;
        this.body = Objects.requireNonNullElseGet(body, ArrayList::new);
    }

    public List<String> getBody() { return body; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        try {
            sb.append(this.toJava()).append(" {\n");
        } catch (RuntimeException e) {
            e.printStackTrace();
            return Logger.logAnalysisFailure("method", signature);
        }
        for (String line : this.body) {
            sb.append("\t").append(line).append("\n");
        }
        sb.append("}\n");
        return sb.toString();
    }

    @Override
    public String toJava() {
        if (!translated) {
            try {
                MethodAnalyzer.translate(this);
            } catch (Exception e) {
                e.printStackTrace();
                translated = true;
                return Logger.logAnalysisFailure("method", signature);
            }
        }
        StringBuilder sb = new StringBuilder();
        if (accessModifier.equals("default")) {
            sb.append(accessModifier).append(" ");
        }
        if (staticModifier) {
            sb.append("static ");
        }
        if (abstractModifier) {
            sb.append("abstract ");
        }
        sb.append(TypeTranslator.isBasicType(returnType) ? returnType : TypeTranslator.getJavaObjectName(returnType));
        sb.append(" ").append(name).append("(");
        sb.append(listParameters(parametersList)).append(")");
        return sb.toString();
    }

    // getter
    public List<String> getParametersList() {
        return parametersList;
    }

    // setter
    public void setParametersList(List<String> parametersList) {
        this.parametersList = parametersList;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public void setAbstractModifier(String abstractModifier) {
        this.abstractModifier = abstractModifier != null;
    }

    public void setAbstractModifier(boolean abstractModifier) {
        this.abstractModifier = abstractModifier;
    }
}
