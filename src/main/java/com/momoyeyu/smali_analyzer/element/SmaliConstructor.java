package com.momoyeyu.smali_analyzer.element;

import com.momoyeyu.smali_analyzer.analyzers.ConstructorAnalyzer;
import com.momoyeyu.smali_analyzer.utils.Logger;

import java.util.ArrayList;
import java.util.List;

public class SmaliConstructor extends SmaliMethod {

    private String initType;

    public SmaliConstructor(String signature, SmaliClass ownerClass) {
        super(signature, ownerClass, new ArrayList<>());
    }

    public SmaliConstructor(String signature, SmaliClass ownerClass, List<String> body) {
        super(signature, ownerClass, body);
    }

    @Override
    public String toJava() {
        if (!analyzed) {
            try {
                ConstructorAnalyzer.analyze(this);
                analyzed = true;
            } catch (RuntimeException e) {
                Logger.logException(e.getMessage());
                return Logger.logAnalysisFailure("constructor", signature);
            }
        }
        if (!ownerClass.isAnalyzed()) {
            ownerClass.toJava();
        }
        StringBuilder sb = new StringBuilder();
        if (!accessModifier.equals("default")) {
            sb.append(accessModifier).append(" ");
        }
        if (staticModifier) {
            sb.append("static ");
        }
        if (finalModifier) {
            sb.append("final ");
        }
        sb.append(ownerClass.getName()).append("(");
        sb.append(listParameters(parametersList)).append(")");
        return sb.toString();
    }

    public String getInitType() {
        return initType;
    }

    public void setInitType(String initType) {
        this.initType = initType;
    }
}
