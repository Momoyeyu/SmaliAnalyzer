package com.momoyeyu.smali_analyzer.entity;

import java.util.List;

public class SmaliMethod {
    private String signature;
    private List<String> body;

    private String methodName;
    private String accessModifier;
    private String staticModifier;
    private List<String> parametersList;
    private String returnType;


    public SmaliMethod(String signature, List<String> body) {
        this.signature = signature;
        this.body = body;
    }

    public String getSignature() { return signature; }

    public List<String> getBody() { return body; }

}
