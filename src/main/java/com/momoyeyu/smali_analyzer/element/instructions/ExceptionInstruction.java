package com.momoyeyu.smali_analyzer.element.instructions;

import com.momoyeyu.smali_analyzer.element.SmaliMethod;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExceptionInstruction extends Instruction {

    private static final Pattern exceptionPattern = Pattern.compile("^move-exception\\s+(\\S+)");

    public ExceptionInstruction(String instruction, SmaliMethod parentMethod) {
        super(instruction, parentMethod);
        this.analyze();
    }

    @Override
    protected void analyze() {
        Matcher matcher = exceptionPattern.matcher(signature);
        if (matcher.matches()) {
            operation = "move-exception";
            registers = getRegistersList(matcher.group(1));
            super.analyze();
        }
    }

    @Override
    public String toString() {
        if (!analyzed)
            return analysisFail("exception");
        return "";
    }

    @Override
    public INSTRUCTION_TYPE getType() {
        return INSTRUCTION_TYPE.EXCEPTION;
    }

    @Override
    public INSTRUCTION_TYPE getSubType() {
        return INSTRUCTION_TYPE.EXCEPTION;
    }

    public static boolean isExceptionInstruction(String instruction) {
        if (instruction == null)
            return false;
        return exceptionPattern.matcher(instruction).matches();
    }
}
