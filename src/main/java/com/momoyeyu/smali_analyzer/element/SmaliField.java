package com.momoyeyu.smali_analyzer.element;

public class SmaliField extends SmaliElement {
    private boolean array;
    private String type;

    public SmaliField(String signature) {
        super(signature);
    }

    public boolean isArray() {
        return array;
    }
}
