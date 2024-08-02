package com.momoyeyu.smali_analyzer.element.instructions;

import com.momoyeyu.smali_analyzer.element.SmaliMethod;

import java.util.Arrays;
import java.util.List;

public class SmaliInstruction {

    protected String signature;
    protected String operation;
    protected SmaliMethod parentMethod;

    protected void analyze() {
        return;
    }

    public SmaliInstruction(String instruction, SmaliMethod parentMethod) {
        signature = instruction;
        parentMethod = parentMethod;
    }

    protected static List<String> getRegistersList(String registers) {
        return Arrays.stream(registers.split("(,\\s*)")).toList();
    }

    @Override
    public String toString() {
        return null;
    }

}
