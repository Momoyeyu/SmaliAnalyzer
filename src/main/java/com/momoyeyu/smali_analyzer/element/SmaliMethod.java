package com.momoyeyu.smali_analyzer.element;

import com.momoyeyu.smali_analyzer.analyzers.MethodAnalyzer;
import com.momoyeyu.smali_analyzer.entity.VariablesTable;
import com.momoyeyu.smali_analyzer.utils.Logger;
import com.momoyeyu.smali_analyzer.utils.TypeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SmaliMethod extends SmaliElement {
    private final List<String> body;
    protected SmaliClass ownerClass;

    protected List<String> parametersList;
    private boolean abstractModifier;
    private boolean synchronizedModifier;
    private boolean nativeModifier;
    private String returnType;

    public SmaliMethod(String signature) {
        this(signature, null, new ArrayList<>());
    }

    public SmaliMethod(String signature, SmaliClass ownerClass, List<String> body) {
        super(signature);
        this.ownerClass = ownerClass;
        this.abstractModifier = false;
        this.synchronizedModifier = false;
        this.body = Objects.requireNonNullElseGet(body, ArrayList::new);
    }

    public List<String> getBody() {
        return body;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        try {
            sb.append(this.toJava()).append(" {\n");
        } catch (RuntimeException e) {
            Logger.logException(e.getMessage());
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
        if (!analyzed) {
            try {
                MethodAnalyzer.analyze(this);
            } catch (Exception e) {
                Logger.logException(e.getMessage());
                analyzed = true;
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
        if (finalModifier) {
            sb.append("final ");
        }
        if (synchronizedModifier) {
            sb.append("synchronizedModifier ");
        }
        if (abstractModifier) {
            sb.append("abstract ");
        }
        sb.append(TypeUtils.getNameFromJava(returnType));
        sb.append(" ").append(name).append("(");
        sb.append(listParameters(parametersList)).append(")");
        return sb.toString();
    }

    private String analyzeBody() {
        VariablesTable variablesTable = new VariablesTable(this);
        return null;
    }

    // getter
    /**
     * Return a list of method's parameters (type only)
     * @return parameters list
     */
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

    public void setNativeModifier(String nativeModifier) {
        this.nativeModifier = nativeModifier != null;
    }

    public void setNativeModifier(boolean nativeModifier) {
        this.nativeModifier = nativeModifier;
    }

    public void setAbstractModifier(String abstractModifier) {
        this.abstractModifier = abstractModifier != null;
    }

    public void setAbstractModifier(boolean abstractModifier) {
        this.abstractModifier = abstractModifier;
    }

    public void setSynchronizedModifier(String synchronizedModifier) {
        this.synchronizedModifier = synchronizedModifier != null;
    }

    public void setSynchronizedModifier(boolean synchronizedModifier) {
        this.synchronizedModifier = synchronizedModifier;
    }
}
