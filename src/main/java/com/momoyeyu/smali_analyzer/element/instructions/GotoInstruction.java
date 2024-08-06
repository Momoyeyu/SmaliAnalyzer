package com.momoyeyu.smali_analyzer.element.instructions;

import com.momoyeyu.smali_analyzer.element.SmaliMethod;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GotoInstruction extends Instruction {

    private static final Pattern gotoPattern = Pattern.compile("goto\\s+(:\\S+)");
    private String label;

    public GotoInstruction(String instruction, SmaliMethod parentMethod) {
        super(instruction, parentMethod);
        this.analyze();
    }

    @Override
    protected void analyze() {
        Matcher matcher = gotoPattern.matcher(signature);
        if (matcher.find()) {
            label = matcher.group(1);
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

    public static boolean isGoto(String instruction) {
        if (instruction == null)
            return false;
        return gotoPattern.matcher(instruction).matches();
    }
}
