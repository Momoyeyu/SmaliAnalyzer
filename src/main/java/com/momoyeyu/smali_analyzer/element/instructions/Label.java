package com.momoyeyu.smali_analyzer.element.instructions;

import com.momoyeyu.smali_analyzer.element.SmaliMethod;
import com.momoyeyu.smali_analyzer.enumeration.INSTRUCTION_TYPE;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Label extends Instruction {

    private static final Pattern labelPattern = Pattern.compile("(:\\S+)");

    private String label;

    public static void main(String[] args) {
        System.out.println(new Label(":cond_1"));
        System.out.println(new Label(":cond_2"));
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
        if (matcher.find()) {
            label = matcher.group(1);
            super.analyze();
        }
    }

    @Override
    public INSTRUCTION_TYPE getType() {
        return INSTRUCTION_TYPE.LABEL;
    }

    @Override
    public INSTRUCTION_TYPE getSubType() {
        return getLabelType(this.label);
    }

    @Override
    public String toString() {
        if (!analyzed)
            return analysisFail("label");
        return label;
    }

    public static boolean isLabel(String label) {
        if (label == null)
            return false;
        return labelPattern.matcher(label).matches();
    }

    public static INSTRUCTION_TYPE getLabelType(String label) {
        if (label.startsWith(":try_start"))
            return INSTRUCTION_TYPE.LABEL_TRY_START;
        else if (label.startsWith(":try_end"))
            return INSTRUCTION_TYPE.LABEL_TRY_END;
        return switch (label.substring(1, Math.min(5, label.length()))) {
            case "goto" -> INSTRUCTION_TYPE.LABEL_GOTO;
            case "cond" -> INSTRUCTION_TYPE.LABEL_CONDITION;
            default -> INSTRUCTION_TYPE.LABEL;
        };
    }
}
