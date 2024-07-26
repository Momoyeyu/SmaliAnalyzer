package com.momoyeyu.smali_analyzer.element;

import com.momoyeyu.smali_analyzer.analyzers.FieldAnalyzer;
import com.momoyeyu.smali_analyzer.utils.Logger;
import com.momoyeyu.smali_analyzer.utils.TypeTranslator;

import java.util.LinkedList;
import java.util.List;

public class SmaliField extends SmaliElement {
    private List<String> annotations;
    private String type;
    private Object value;

    // constructor
    public SmaliField(String signature) {
        this(signature, new LinkedList<>());
    }

    public SmaliField(String signature, List<String> annotations) {
        super(signature);
        this.annotations = annotations;
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

    // adder
    public void addAnnotation(String line) {
        if (annotations != null) {
            this.annotations.add(line);
        }
        else {
            Logger.log("[WARN] Trying to add annotation to incorrect field: " + this.signature);
        }
    }
}
