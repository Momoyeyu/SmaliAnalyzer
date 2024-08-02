package com.momoyeyu.smali_analyzer.element.instructions;

import com.momoyeyu.smali_analyzer.element.SmaliMethod;
import com.momoyeyu.smali_analyzer.utils.Logger;

import java.util.Arrays;
import java.util.List;

public class SmaliInstruction {

    protected String signature;
    protected String operation;
    protected SmaliMethod parentMethod;
    protected boolean analyzed;

    protected void analyze() {
        analyzed = true;
    }

    public SmaliInstruction(String instruction, SmaliMethod parentMethod) {
        signature = instruction;
        parentMethod = parentMethod;
        analyzed = false;
    }

    protected static List<String> getRegistersList(String registers) {
        return Arrays.stream(registers.split("(,\\s*)")).toList();
    }

    @Override
    public String toString() {
        return signature;
    }

    protected String analysisFail(String type) {
        Logger.logAnalysisFailure(type, signature);
        return toString();
    }

}
