package com.momoyeyu.smali_analyzer.element;

import com.momoyeyu.smali_analyzer.element.instructions.INSTRUCTION_TYPE;
import com.momoyeyu.smali_analyzer.element.instructions.Instruction;
import com.momoyeyu.smali_analyzer.entity.RegisterTable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class SmaliLine {

    private static final Pattern expression = Pattern.compile("(\\S+)\\s*=\\s*(\\S+)");
    private final RegisterTable registerTable;
    private INSTRUCTION_TYPE status;

    private final List<Instruction> instructions;

    private SmaliLine() {
        this(new ArrayList<>(), new RegisterTable());
    }

    public SmaliLine(List<Instruction> lines, RegisterTable registerTable) {
        this.registerTable = registerTable;
        this.instructions = lines;
        this.status = INSTRUCTION_TYPE.DEFAULT;
    }

    private boolean equalType(INSTRUCTION_TYPE type, INSTRUCTION_TYPE... otherType) {
        for (INSTRUCTION_TYPE instructionType : otherType) {
            if (type.equals(instructionType)) {
                return true;
            }
        }
        return false;
    }
}
