package com.momoyeyu.smali_analyzer.element;

import com.momoyeyu.smali_analyzer.element.instructions.INSTRUCTION_TYPE;
import com.momoyeyu.smali_analyzer.element.instructions.Instruction;
import com.momoyeyu.smali_analyzer.entity.MethodStack;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class SmaliLine {

    private static final Pattern expression = Pattern.compile("(\\S+)\\s*=\\s*(\\S+)");
    private final MethodStack methodStack;
    private INSTRUCTION_TYPE status;

    private final List<Instruction> instructions;

    private SmaliLine() {
        this(new ArrayList<>(), new MethodStack());
    }

    public SmaliLine(List<Instruction> lines, MethodStack methodStack) {
        this.methodStack = methodStack;
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
