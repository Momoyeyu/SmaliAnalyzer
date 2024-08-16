package com.momoyeyu.smali_analyzer.element;

import com.momoyeyu.smali_analyzer.analyzers.FieldAnalyzer;
import com.momoyeyu.smali_analyzer.utils.Logger;
import com.momoyeyu.smali_analyzer.utils.TypeUtils;

public class SmaliField extends SmaliElement {
    private String annotations;
    private String type;
    private String value;
    private boolean volatileModifier;
    private boolean transientModifier;

    // constructor
    public SmaliField(String signature) {
        this(signature, "");
    }

    public SmaliField(String signature, String annotations) {
        super(signature);
        this.volatileModifier = false;
        this.transientModifier = false;
        this.annotations = annotations.strip();
    }

    @Override
    public String toString() {
        return this.toJava();
    }

    @Override
    public String toJava() {
        if (!analyzed) {
            try {
                FieldAnalyzer.analyze(this);
                analyzed = true;
            } catch (RuntimeException e) {
                Logger.logException(e.getMessage());
                return Logger.logAnalysisFailure("field", signature + "\n" + annotations);
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
        if (volatileModifier) {
            sb.append("volatile ");
        }
        if (transientModifier) {
            sb.append("transient ");
        }
        String typeName = TypeUtils.getNameFromJava(type);
        sb.append(typeName).append(" ");
        sb.append(name);
        if (value != null) {
            sb.append(" = ");
            if (typeName.equals("boolean") || typeName.equals("Boolean")) {
                sb.append(switch (value) {
                    case "0x0" -> "false";
                    case "0x1" -> "true";
                    default -> value;
                });
            } else if (typeName.equals("int") || typeName.equals("Integer")
            || typeName.equals("long") || typeName.equals("Long")) {
                if (value.startsWith("0x")) {
                    sb.append(Integer.parseInt(value.substring(2), 16));
                }
            } else {
                sb.append(value);
            }
        }
        sb.append(";");
        return sb.toString();
    }

    // setter
    public void setType(String type) {
        this.type = type;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setTransientModifier(String transientModifier) {
        this.transientModifier = transientModifier != null;
    }

    public void setTransientModifier(boolean transientModifier) {
        this.transientModifier = transientModifier;
    }

    public void setVolatileModifier(String volatileModifier) {
        this.volatileModifier = volatileModifier != null;
    }

    public void setVolatileModifier(boolean volatileModifier) {
        this.volatileModifier = volatileModifier;
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
