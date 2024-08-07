package com.momoyeyu.smali_analyzer.element.instructions;

import com.momoyeyu.smali_analyzer.element.SmaliMethod;
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

    protected void analyze() {
        analyzed = true;
    }

    public Instruction(String instruction, SmaliMethod parentMethod) {
        signature = Objects.requireNonNullElse(instruction, "");
        this.parentMethod = parentMethod;
        analyzed = false;
    }

    protected List<String> getRegistersList(String registers) {
        if (registers == null) {
            return new ArrayList<>();
        }
        return thisRegister(Arrays.stream(registers.split("(,\\s*)")).toList());
    }

    @Override
    public String toString() {
        Logger.log("[TODO] unknown instruction: " + signature);
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

    protected List<String> thisRegister(List<String> registers) {
        if (this.parentMethod == null || this.parentMethod.isStaticModifier()) {
            return registers;
        }
        List<String> thisRegisters = new ArrayList<>();
        for (String register : registers) {
            thisRegisters.add(register.equals("p0") ? "this" : register);
        }
        return thisRegisters;
    }

    public static boolean equalType(INSTRUCTION_TYPE type, INSTRUCTION_TYPE... otherType) {
        for (INSTRUCTION_TYPE instructionType : otherType) {
            if (type.equals(instructionType)) {
                return true;
            }
        }
        return false;
    }

}
