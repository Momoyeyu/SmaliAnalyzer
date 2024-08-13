package com.momoyeyu.smali_analyzer.element.instructions;

import com.momoyeyu.smali_analyzer.element.SmaliMethod;
import com.momoyeyu.smali_analyzer.enumeration.INSTRUCTION_TYPE;

public class NopInstruction extends Instruction {
    public NopInstruction(String instruction, SmaliMethod parentMethod) {
        super(instruction, parentMethod);
        operation = "nop";
    }

    @Override
    protected void analyze() {
        if (!analyzed)
            super.analyze();
    }

    @Override
    public void updateTable() {
        if (!updated)
            super.updateTable();
    }

    @Override
    public String toString() {
        return "// nop";
    }

    @Override
    public INSTRUCTION_TYPE getType() {
        return INSTRUCTION_TYPE.NOP;
    }

    @Override
    public INSTRUCTION_TYPE getSubType() {
        return INSTRUCTION_TYPE.NOP;
    }

    public static boolean isNopInstruction(String instruction) {
        if (instruction == null)
            return false;
        return instruction.equals("nop");
    }
}
