package com.momoyeyu.smali_analyzer.element.instructions;

import com.momoyeyu.smali_analyzer.element.SmaliMethod;
import com.momoyeyu.smali_analyzer.entity.RegisterTable;
import com.momoyeyu.smali_analyzer.utils.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Instruction {

    protected String signature;
    protected String operation;
    protected List<String> registers;
    protected SmaliMethod parentMethod;
    protected boolean analyzed;
    protected boolean updated;

    protected void analyze() {
        analyzed = true;
    }

    public Instruction(String instruction, SmaliMethod parentMethod) {
        signature = Objects.requireNonNullElse(instruction, "");
        this.parentMethod = parentMethod;
        analyzed = false;
        updated = false;
    }

    protected List<String> getRegistersList(String registers) {
        if (registers == null) {
            return new ArrayList<>();
        }
        return Arrays.stream(registers.split("(,\\s*)")).toList();
    }

    @Override
    public String toString() {
        Logger.logTodo("instruction", signature);
        return "// " + signature;
    }

    public INSTRUCTION_TYPE getSubType() {
        return getType();
    }

    public INSTRUCTION_TYPE getType() {
        return INSTRUCTION_TYPE.DEFAULT;
    }

    protected String analysisFail(String type) {
        Logger.logAnalysisFailure(type, signature);
        return "// " + signature;
    }

    // getter
    public String getSignature() {
        return signature;
    }

    public static boolean equalType(INSTRUCTION_TYPE type, INSTRUCTION_TYPE... otherType) {
        for (INSTRUCTION_TYPE instructionType : otherType) {
            if (type.equals(instructionType)) {
                return true;
            }
        }
        return false;
    }

    public void updateTable() {
        if (!updated) {
            if (registers != null)
                registers = getSubstituteRegisters(registers);
            updated = true;
        }
    }

    protected List<String> getSubstituteRegisters(List<String> registers) {
        if (parentMethod == null)
            return registers;
        List<String> substitutedRegisters = new ArrayList<>();
        RegisterTable table = parentMethod.getRegisterTable();
        for (String register : registers) {
            substitutedRegisters.add(table.getVariableName(register));
        }
        return substitutedRegisters;
    }

}
