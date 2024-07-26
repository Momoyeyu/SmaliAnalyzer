package com.momoyeyu.smali_analyzer.element;

import com.momoyeyu.smali_analyzer.analyzers.MethodAnalyzer;
import com.momoyeyu.smali_analyzer.utils.Logger;

import java.util.ArrayList;
import java.util.List;

public class SmaliMethod extends SmaliElement {
    private List<String> body;
    protected SmaliClass ownerClass;

    protected List<String> parametersList;
    private String abstractModifier;
    private String returnType;

    public SmaliMethod(String signature) {
        this(signature, null, new ArrayList<>());
    }

    public SmaliMethod(String signature, SmaliClass ownerClass, List<String> body) {
        super(signature);
        this.ownerClass = ownerClass;
        if (body != null) {
            this.body = body;
        } else {
            this.body = new ArrayList<>();
        }
    }

    public List<String> getBody() { return body; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        try {
            sb.append(this.toJava()).append(" {\n");
        } catch (RuntimeException e) {
            e.printStackTrace();
            return Logger.failToAnalyze("method", signature);
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
                return Logger.failToAnalyze("method", signature);
            }
        }
        StringBuilder sb = new StringBuilder();
        if (!accessModifier.equals("default")) {
            sb.append(accessModifier).append(" ");
        }
        if (!staticModifier.equals("default")) {
            sb.append(staticModifier).append(" ");
        }
        if (!abstractModifier.equals("default")) {
            sb.append(abstractModifier).append(" ");
        }
        if (!returnType.isBlank()) {
            sb.append(returnType).append(" ");
        } else {
            System.out.println(this.signature);
            throw new RuntimeException("[ERROR] returnType is blank");
        }
        sb.append(name).append("(");
        sb.append(MethodAnalyzer.listParameters(parametersList)).append(")");
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
        this.abstractModifier = abstractModifier == null ? "default" : abstractModifier;
    }
}
