package com.momoyeyu.smali_analyzer.element.instructions;

import com.momoyeyu.smali_analyzer.element.SmaliMethod;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Label extends Instruction {

    private static final Pattern labelPattern = Pattern.compile(":(\\S+)");

    private String label;

    public static void main(String[] args) {
        System.out.println(new Label(":cond_1"));
    }

    private Label(String label) {
        this(label, null);
    }

    public Label(String signature, SmaliMethod smaliMethod) {
        super(signature, smaliMethod);
        this.analyze();
    }

    @Override
    protected void analyze() {
        Matcher matcher = labelPattern.matcher(signature);
        if (matcher.matches()) {
            label = matcher.group();
            super.analyze();
        }
    }

    @Override
    public INSTRUCTION_TYPE getTYPE() {
        return INSTRUCTION_TYPE.LABEL;
    }

    @Override
    public void updateTable() {
        parentMethod.labelTable.addLabel(label);
    }

    @Override
    public INSTRUCTION_TYPE getTrueTYPE() {
        return switch (label.substring(1, Math.min(5, label.length()))) {
            case "goto" -> INSTRUCTION_TYPE.LABEL_GOTO;
            case "cond" -> INSTRUCTION_TYPE.LABEL_CONDITION;
            default -> INSTRUCTION_TYPE.LABEL;
        };
    }

    @Override
    public String toString() {
        return label;
    }

    public static boolean isLabel(String label) {
        if (label == null)
            return false;
        return labelPattern.matcher(label).matches();
    }
}
