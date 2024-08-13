package com.momoyeyu.smali_analyzer.element.instructions;

import com.momoyeyu.smali_analyzer.element.SmaliMethod;
import com.momoyeyu.smali_analyzer.enumeration.INSTRUCTION_TYPE;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CompareInstruction extends Instruction{
    private static final Pattern pattern = Pattern.compile("(cmp|cmpl|cmpg)-(int|long|float|double)\\s+(.*)");

    private String cmpKind;
    private String cmpType;

    public static void main(String[] args) {
        System.out.println(new CompareInstruction("cmpl-float v0, p1, v0"));
        System.out.println(new CompareInstruction("cmpg-int v2, p1, v1"));
        System.out.println(new CompareInstruction("cmp-float v2, p1, v0"));
    }

    private CompareInstruction(String instruction) {
        this(instruction, null);
    }

    public CompareInstruction(String instruction, SmaliMethod parentMethod) {
        super(instruction, parentMethod);
        this.analyze();
    }

    @Override
    protected void analyze() {
        if (!analyzed) {
            Matcher matcher = pattern.matcher(signature);
            if (matcher.find()) {
                cmpKind = matcher.group(1);
                cmpType = matcher.group(2);
                registers = getRegistersList(matcher.group(3));
                super.analyze();
            }
        }
    }

    @Override
    public void updateTable() {
        if (!analyzed) {
            registerTable.storeVariable(registers.getFirst(), cmpType);
            super.updateTable();
        }
    }

    @Override
    public String toString() {
        if (!analyzed)
            return analysisFail("cmp");
        if (cmpKind.equals("cmpl"))
            return cmpType + " " + registers.getFirst() + " = " + registers.getLast() + " - " + registers.get(1);
        else
            return cmpType + " " + registers.getFirst() + " = " + registers.get(1) + " - " + registers.getLast();
    }

    @Override
    public INSTRUCTION_TYPE getType() {
        return INSTRUCTION_TYPE.CMP;
    }

    @Override
    public INSTRUCTION_TYPE getSubType() {
        return INSTRUCTION_TYPE.CMP;
    }

    public static boolean isCompareInstruction(String instruction) {
        if (instruction == null)
            return false;
        return pattern.matcher(instruction).matches();
    }
}
