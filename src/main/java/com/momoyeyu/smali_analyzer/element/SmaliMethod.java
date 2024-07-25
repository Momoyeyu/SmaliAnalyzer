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

    @Override
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
        sb.append(name).append("(");
        sb.append(MethodAnalyzer.listParameters(parametersList)).append(");");
        return sb.toString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
