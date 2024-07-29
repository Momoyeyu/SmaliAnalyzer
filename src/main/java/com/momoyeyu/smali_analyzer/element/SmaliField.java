package com.momoyeyu.smali_analyzer.element;

import com.momoyeyu.smali_analyzer.analyzers.FieldAnalyzer;
import com.momoyeyu.smali_analyzer.utils.Logger;
import com.momoyeyu.smali_analyzer.utils.TypeTranslator;

public class SmaliField extends SmaliElement {
    private String annotations;
    private String type;
    private Object value;

    // constructor
    public SmaliField(String signature) {
        this(signature, "");
    }

    public SmaliField(String signature, String annotations) {
        super(signature);
        this.annotations = annotations.strip();
    }

    @Override
    public String toString() {
        return this.toJava();
    }

    @Override
    public String toJava() {
        if (!translated) {
            try {
                FieldAnalyzer.translate(this);
                translated = true;
            } catch (RuntimeException e) {
                e.printStackTrace();
                return Logger.logAnalysisFailure("field", signature);
            }
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
        sb.append(TypeTranslator.isBasicType(type) ? type : TypeTranslator.getObjectName(type)).append(" ");
        sb.append(name);
        if (value != null) {
            sb.append(" = ").append(value);
        }
        sb.append(";");
        return sb.toString();
    }

    // setter
    public void setType(String type) {
        this.type = type;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    // getter
    public String getAnnotations() {
        return annotations;
    }

    public String getType() {
        return type;
    }

    // adder
    public void addAnnotation(String line) {
        if (annotations != null) {
            this.annotations += line.strip();
        }
        else {
            Logger.log("[WARN] Trying to add annotation '" + line + "' to incorrect field: '" + this.signature + "'");
        }
    }
}
