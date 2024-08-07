package com.momoyeyu.smali_analyzer.element.instructions;

import com.momoyeyu.smali_analyzer.element.SmaliMethod;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ThrowInstruction extends Instruction {
    private static final Pattern throwPattern = Pattern.compile("^throw\\s+(\\S+)");

    public static void main(String[] args) {
        System.out.println(new ThrowInstruction("throw v0"));
    }

    private ThrowInstruction(String instruction) {
        this(instruction, null);
    }

    public ThrowInstruction(String instruction, SmaliMethod parentMethod) {
        super(instruction, parentMethod);
        this.analyze();
    }

    @Override
    protected void analyze() {
        Matcher matcher = throwPattern.matcher(signature);
        if (matcher.find()) {
            operation = "throw";
            registers = getRegistersList(matcher.group(1));
            super.analyze();
        }
    }

    @Override
    public String toString() {
        return operation + " " + registers.getFirst();
    }

    @Override
    public INSTRUCTION_TYPE getType() {
        return INSTRUCTION_TYPE.THROW;
    }

    @Override
    public INSTRUCTION_TYPE getSubType() {
        return INSTRUCTION_TYPE.THROW;
    }

    public static boolean isThrowInstruction(String instruction) {
        if (instruction == null)
            return false;
        return throwPattern.matcher(instruction).matches();
    }
}
