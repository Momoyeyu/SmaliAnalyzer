package com.momoyeyu.smali_analyzer.element;

import com.momoyeyu.smali_analyzer.analyzers.ConstructorAnalyzer;

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
            translated = true;
            try {
                ConstructorAnalyzer.translate(this);
            } catch (RuntimeException e) {
                e.printStackTrace();
                translated = false;
                return "[ERROR] invalid constructor" + signature;
            }
        }
        StringBuilder sb = new StringBuilder();
        if (!(accessModifier == null) && !accessModifier.isEmpty() && !accessModifier.equals("default")) {
            sb.append(accessModifier).append(" ");
        }
        if (!(staticModifier == null) && !staticModifier.isEmpty() && !staticModifier.equals("instance")) {
            sb.append(staticModifier).append(" ");
        }
        sb.append(ownerClass.getName()).append("(");
        sb.append(ConstructorAnalyzer.listParameters(parametersList)).append(");");
        return sb.toString();
    }

    public String getInitType() {
        return initType;
    }

    public void setInitType(String initType) {
        this.initType = initType;
    }
}
