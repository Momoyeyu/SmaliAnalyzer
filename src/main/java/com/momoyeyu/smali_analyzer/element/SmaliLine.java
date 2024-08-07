package com.momoyeyu.smali_analyzer.element;

import com.momoyeyu.smali_analyzer.element.instructions.INSTRUCTION_TYPE;
import com.momoyeyu.smali_analyzer.element.instructions.Instruction;
import com.momoyeyu.smali_analyzer.entity.RegisterMap;

import java.util.List;
import java.util.regex.Pattern;

public class SmaliLine {

    private static final Pattern expression = Pattern.compile("(\\S+)\\s*=\\s*(\\S+)");
    private final RegisterMap registerTable;
    private INSTRUCTION_TYPE status;

    private final List<Instruction> instructions;

    public SmaliLine(List<Instruction> lines, RegisterMap registerTable) {
        this.registerTable = registerTable;
        this.instructions = lines;
        this.status = INSTRUCTION_TYPE.DEFAULT;
    }
}
