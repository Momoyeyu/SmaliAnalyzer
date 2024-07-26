package com.momoyeyu.smali_analyzer.element;

import com.momoyeyu.smali_analyzer.analyzers.FieldAnalyzer;
import com.momoyeyu.smali_analyzer.utils.TypeTranslator;

import java.util.ArrayList;
import java.util.List;

public class SmaliField extends SmaliElement {
    private List<String> annotations;
    private boolean arrayFlag;
    private String type;
    private Object value;

    // constructor
    public SmaliField(String signature) {
        this(signature, null);
    }

    public SmaliField(String signature, List<String> annotations) {
        super(signature);
        this.annotations = annotations == null ? new ArrayList<>() : annotations;
    }

    @Override
    public String toJava() {
        if (!translated) {
            try {
                FieldAnalyzer.translate(this);
                translated = true;
            } catch (RuntimeException e) {
                e.printStackTrace();
                return "// [ERROR] Unable to translate field: " + signature;
            }
        }
        StringBuilder sb = new StringBuilder();
        if (!accessModifier.equals("default")) {
            sb.append(accessModifier).append(" ");
        }
        if (!staticModifier.equals("default")) {
            sb.append(staticModifier).append(" ");
        }
        if (!finalModifier.equals("default")) {
            sb.append(finalModifier).append(" ");
        }
        sb.append(TypeTranslator.getType(type));
        sb.append(arrayFlag ? "[] " : " ");
        sb.append(name);
        if (value != null) {
            sb.append(" = ").append(value.toString());
        }
        sb.append(";");
        return sb.toString();
    }

    // setter
    public void setArrayFlag(boolean arrayFlag) {
        this.arrayFlag = arrayFlag;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    // adder
    public void addAnnotation(String line) {
        this.annotations.add(line);
    }
}
