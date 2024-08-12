package com.momoyeyu.smali_analyzer.element.instructions;

import com.momoyeyu.smali_analyzer.element.SmaliMethod;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArrayLengthInstruction extends Instruction {
    private static final Pattern pattern = Pattern.compile("array-length\\s+(.*)");

    private String array;

    public static void main(String[] args) {
        System.out.println(new ArrayLengthInstruction("array-length v0, p1"));
    }

    private ArrayLengthInstruction(String instruction) {
        this(instruction, null);
    }

    public ArrayLengthInstruction(String instruction, SmaliMethod parentMethod) {
        super(instruction, parentMethod);
        this.analyze();
    }

    @Override
    protected void analyze() {
        Matcher matcher = pattern.matcher(signature);
        if (matcher.find()) {
            registers = getRegistersList(matcher.group(1));
            array = registers.getLast();
            super.analyze();
        }
    }

    @Override
    public void updateTable() {
        if (!updated) {
            array = registerTable.getVariableName(registers.getLast());
            registerTable.storeVariable(registers.getFirst(), "int");
            super.updateTable();
        }
    }

    @Override
    public String toString() {
        if (!analyzed)
            return analysisFail("array-length");
        return "int " + registers.getFirst() + " = " + array + ".length";
    }

    @Override
    public INSTRUCTION_TYPE getSubType() {
        return INSTRUCTION_TYPE.ARRAY_LENGTH;
    }

    @Override
    public INSTRUCTION_TYPE getType() {
        return INSTRUCTION_TYPE.ARRAY_LENGTH;
    }

    public static boolean isArrayLengthInstruction(String instruction) {
        if (instruction == null)
            return false;
        return pattern.matcher(instruction).matches();
    }
}
