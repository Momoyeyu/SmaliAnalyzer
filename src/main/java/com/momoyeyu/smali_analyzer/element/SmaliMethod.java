package com.momoyeyu.smali_analyzer.element;

import com.momoyeyu.smali_analyzer.analyzers.MethodAnalyzer;

import java.util.ArrayList;
import java.util.List;

public class SmaliMethod extends SmaliElement {
    private List<String> body;
    protected SmaliClass ownerClass;

    protected List<String> parametersList;
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
    public String toJava() {
        if (!translated) {
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
        if (!staticModifier.equals("default")) {
            sb.append(staticModifier).append(" ");
        }
        sb.append(returnType).append(" ");
        sb.append(name).append("(");
        sb.append(MethodAnalyzer.listParameters(parametersList)).append(");");
        return sb.toString();
    }

    public List<String> getParametersList() {
        return parametersList;
    }

    public void setParametersList(List<String> parametersList) {
        this.parametersList = parametersList;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public boolean isTranslated() {
        return translated;
    }
}
