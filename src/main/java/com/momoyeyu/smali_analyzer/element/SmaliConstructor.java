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
        if (!translated) {
            try {
                ConstructorAnalyzer.translate(this);
                translated = true;
            } catch (RuntimeException e) {
                e.printStackTrace();
                return Logger.failToAnalyze("constructor", signature);
            }
        }
        if (!ownerClass.isTranslated()) {
            ownerClass.toJava();
        }
        StringBuilder sb = new StringBuilder();
        if (!accessModifier.equals("default")) {
            sb.append(accessModifier).append(" ");
        }
        if (staticModifier) {
            sb.append("static ");
        }
        sb.append(ownerClass.getName()).append("(");
        sb.append(ConstructorAnalyzer.listParameters(parametersList)).append(")");
        return sb.toString();
    }

    public String getInitType() {
        return initType;
    }

    public void setInitType(String initType) {
        this.initType = initType;
    }
}
