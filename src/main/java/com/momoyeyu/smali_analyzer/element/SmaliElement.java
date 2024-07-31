package com.momoyeyu.smali_analyzer.element;

import com.momoyeyu.smali_analyzer.utils.Logger;
import com.momoyeyu.smali_analyzer.utils.TypeUtils;

import java.util.List;

public class SmaliElement {
    // properties
    protected String signature;
    protected String name;
    // modifiers
    protected String accessModifier;
    protected boolean staticModifier;
    protected boolean finalModifier;
    protected boolean syntheticModifier;
    // status
    protected boolean analyzed;

    public static void main(String[] args) {
        List<String> parametersList = TypeUtils.getJavaParameters("Ljava/io/OutputStream;Ljava/lang/String;");
        System.out.println(SmaliElement.listParameters(parametersList, false));
        System.out.println(SmaliElement.listParameters(parametersList, true));
    }

    public SmaliElement(String signature) {
        this.signature = signature;
        accessModifier = "default";
        staticModifier = false;
        finalModifier = false;
        syntheticModifier = false;
        analyzed = false;
    }

    /**
     * Get parameters list in Java style
     *
     * @test pass
     * @param parametersList a List of java parameters type
     * @return Java method signature
     */
    public String listParameters(List<String> parametersList) {
        return listParameters(parametersList, staticModifier);
    }

    /**
     * Get parameters list in Java style
     *
     * @test pass
     * @param parametersList a List of java parameters type
     * @return Java method signature
     */
    public static String listParameters(List<String> parametersList, boolean isStatic) {
        if (parametersList == null || parametersList.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder(TypeUtils.getNameFromJava(parametersList.getFirst()) + " p" + (isStatic ? 0 : 1));
        for (int i = 1; i < parametersList.size(); i++) {
            sb.append(String.format(", %s p%d", TypeUtils.getNameFromJava(parametersList.get(i)), isStatic ? i : i + 1));
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return signature;
    }

    public String toJava() {
        return Logger.logAnalysisFailure("element", signature);
    }

    // getter
    public boolean isAnalyzed() {
        return analyzed;
    }

    public String getName() {
        return name;
    }

    public String getSignature() {
        return signature;
    }

    public boolean isStaticModifier() {
        return staticModifier;
    }

    // setter
    public void analyze() {
        this.analyzed = true;
    }

    public void setFinalModifier(String finalModifier) {
        this.finalModifier = finalModifier != null;
    }

    public void setFinalModifier(boolean finalModifier) {
        this.finalModifier = finalModifier;
    }

    public void setSyntheticModifier(boolean syntheticModifier) {
        this.syntheticModifier = syntheticModifier;
    }

    public void setStaticModifier(String staticModifier) {
        this.staticModifier = staticModifier != null;
    }

    public void setAccessModifier(String accessModifier) {
        this.accessModifier = accessModifier == null ? "default" : accessModifier;
    }

    public void setName(String name) {
        this.name = name;
    }
}
