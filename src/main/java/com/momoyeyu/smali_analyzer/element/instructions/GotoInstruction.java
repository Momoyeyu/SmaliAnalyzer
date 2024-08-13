package com.momoyeyu.smali_analyzer.element.instructions;

import com.momoyeyu.smali_analyzer.element.SmaliMethod;
import com.momoyeyu.smali_analyzer.enumeration.INSTRUCTION_TYPE;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GotoInstruction extends Instruction {

    private static final Pattern gotoPattern = Pattern.compile("goto(/\\S+?)?\\s+(:\\S+)");
    private String label;
    private String size;

    public static void main(String[] args) {
        System.out.println(new GotoInstruction("goto/16 :goto_ad"));
    }

    private GotoInstruction(String instruction) {
        this(instruction, null);
    }

    public GotoInstruction(String instruction, SmaliMethod parentMethod) {
        super(instruction, parentMethod);
        this.analyze();
    }

    @Override
    protected void analyze() {
        Matcher matcher = gotoPattern.matcher(signature);
        if (matcher.find()) {
            size = matcher.group(1);
            label = matcher.group(2);
            super.analyze();
        }
    }

    @Override
    public INSTRUCTION_TYPE getType() {
        return INSTRUCTION_TYPE.GOTO;
    }

    @Override
    public INSTRUCTION_TYPE getSubType() {
        return INSTRUCTION_TYPE.GOTO;
    }

    @Override
    public String toString() {
        if (!analyzed)
            return analysisFail("goto");
        return "goto " + label;
    }

    public String getLabel() {
        return label;
    }

    public static boolean isGoto(String instruction) {
        if (instruction == null)
            return false;
        return gotoPattern.matcher(instruction).matches();
    }
}
