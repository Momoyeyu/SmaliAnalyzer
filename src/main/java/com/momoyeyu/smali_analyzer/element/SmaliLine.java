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

    @Override
    public String toString() {
        String ret = "";
        for (Instruction instruction : instructions) {
            if (equalType(instruction.TYPE(), INSTRUCTION_TYPE.CONST,
                    INSTRUCTION_TYPE.NEW, INSTRUCTION_TYPE.MOV)) {
                instruction.store();
                status = INSTRUCTION_TYPE.DEFAULT;
            } else if (equalType(instruction.TYPE(), INSTRUCTION_TYPE.INVOKE_DIRECT)) {
                ret = instruction.toString(); // new xxx
                status = INSTRUCTION_TYPE.INVOKE;
            } else if (equalType(instruction.TYPE(), INSTRUCTION_TYPE.RESULT)) {
                if (status == INSTRUCTION_TYPE.INVOKE) {

                }
            }
        }

        return null;
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
