package com.momoyeyu.smali_analyzer.entity;

import com.momoyeyu.smali_analyzer.element.SmaliMethod;
import com.momoyeyu.smali_analyzer.element.instructions.*;
import com.momoyeyu.smali_analyzer.enumeration.INSTRUCTION_TYPE;
import com.momoyeyu.smali_analyzer.utils.Formatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Block {

    private final List<Instruction> instructions = new ArrayList<>();
    private final List<String> nextBlocks = new ArrayList<>();
    protected SmaliMethod parentMethod;

    public Block(SmaliMethod smaliMethod) {
        this.parentMethod = smaliMethod;
    }

    // adder
    public void addInstruction(Instruction instruction) {
        instructions.add(instruction);
    }

    public void addNextBlock(String name) {
        nextBlocks.add(name);
    }

    // getter
    public List<String> getNextBlocks() {
        return nextBlocks;
    }

    // setter

    // functionality
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        INSTRUCTION_TYPE lastSubType = INSTRUCTION_TYPE.DEFAULT;
        INSTRUCTION_TYPE lastType = INSTRUCTION_TYPE.DEFAULT;
        int indentLevel = 1;
        Stack<Instruction> stack = new Stack<>();
        Stack<String> labelStack = new Stack<>();
        for (Instruction instruction : this.instructions) {
            INSTRUCTION_TYPE subType = instruction.getSubType();
            INSTRUCTION_TYPE type = instruction.getType();
            instruction.updateTable();
            if (subType == INSTRUCTION_TYPE.INVOKE_CONSTRUCTOR) {
                if (lastSubType == INSTRUCTION_TYPE.NEW_INSTANCE) {
                    sb.append("\t".repeat(indentLevel)).append(stack.pop()).append(" ");
                    String instance = instruction.toString();
                    sb.append(instance.substring(instance.indexOf('='))).append(";\n");
                } else {
                    stack.push(instruction);
                    lastType = type;
                    lastSubType = subType;
                    continue;
                }
            } else if (type == INSTRUCTION_TYPE.INVOKE) {
                if (!instruction.toString().contains("=")) {
                    sb.append("\t".repeat(indentLevel)).append(instruction).append(";\n");
                } else {
                    stack.push(instruction);
                    lastType = type;
                    lastSubType = subType;
                    continue;
                }
            } else if (subType == INSTRUCTION_TYPE.NEW_INSTANCE) {
                stack.push(instruction);
                lastType = type;
                lastSubType = subType;
                continue;
            } else if (type == INSTRUCTION_TYPE.RETURN) {
                sb.append("\t".repeat(indentLevel)).append(instruction).append(";\n");
                lastType = type;
                lastSubType = subType;
                continue;
            } else if (subType == INSTRUCTION_TYPE.TAG_END_METHOD) {
                if (indentLevel > 1) {
                    String tmp = "";
                    if (lastType == INSTRUCTION_TYPE.RETURN) {
                        int idx = sb.lastIndexOf("\t".repeat(indentLevel) + "return");
                        tmp = sb.substring(idx + indentLevel);
                        sb.delete(idx, sb.length());
                    }
                    while (indentLevel > 1) {
                        indentLevel--;
                        sb.append("\t".repeat(indentLevel)).append("}\n");
                    }
                    sb.append("\t".repeat(indentLevel)).append(tmp);
                }
//            } else if (subType == INSTRUCTION_TYPE.LABEL_TRY_START) {
//                Label label = (Label) instruction;
//                sb.append("\t".repeat(indentLevel)).append("try {\n");
//                labelStack.push(label.toString());
//                indentLevel++;
//            } else if (subType == INSTRUCTION_TYPE.LABEL_TRY_END) {
//                String start = Objects.requireNonNullElse(tryMap.get(labelStack.peek()), "");
//                if (start.equals(instruction.toString())) {
//                    labelStack.pop();
//                    indentLevel--;
//                    sb.append("\t".repeat(indentLevel)).append("}\n");
//                    lastSubType = INSTRUCTION_TYPE.LABEL_TRY_END;
//                    lastType = INSTRUCTION_TYPE.LABEL;
//                    continue;
//                }
            } else if (Instruction.equalType(type, INSTRUCTION_TYPE.CONDITION)) {
                ConditionInstruction condition = (ConditionInstruction) instruction;
                sb.append("\t".repeat(indentLevel)).append(condition.reverseCondition()).append(" {\n");
                labelStack.push(condition.getLabel());
                indentLevel++;
            } else if (Instruction.equalType(subType, INSTRUCTION_TYPE.LABEL_CONDITION)) {
                if (!labelStack.isEmpty() && labelStack.peek().equals(instruction.toString())) {
                    labelStack.pop();
                    indentLevel--;
                    sb.append("\t".repeat(indentLevel)).append("}\n");
                }
            } else if (subType == INSTRUCTION_TYPE.RESULT && lastType == INSTRUCTION_TYPE.INVOKE) {
                InvokeInstruction invokeInstruction = (InvokeInstruction) stack.pop();
                ((ResultInstruction) instruction).setResultType(invokeInstruction.getReturnType());
                instruction.updateTable();
                sb.append("\t".repeat(indentLevel)).append(Formatter.replacePattern(
                        invokeInstruction.toString(),
                        "(.*?) ret = (.*)",
                        "$1 " + instruction + " $2")).append(";\n");
            } else if (Instruction.equalType(type, INSTRUCTION_TYPE.DEFAULT)) {
                sb.append("\t".repeat(indentLevel)).append(instruction).append("\n");
            } else if (Instruction.equalType(type, INSTRUCTION_TYPE.TAG,
                    INSTRUCTION_TYPE.SYNCHRONIZED, INSTRUCTION_TYPE.NOP,
                    INSTRUCTION_TYPE.ARRAT_DATA, INSTRUCTION_TYPE.SWITCH_PACK)) {
                continue;
            } else { // other
                sb.append("\t".repeat(indentLevel)).append(instruction).append(";\n");
            }
            lastSubType = INSTRUCTION_TYPE.DEFAULT;
            lastType = INSTRUCTION_TYPE.DEFAULT;
        }
        sb.append("}\n");
        return sb.toString();
    }



}
