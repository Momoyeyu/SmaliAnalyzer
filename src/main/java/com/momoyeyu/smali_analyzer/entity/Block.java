package com.momoyeyu.smali_analyzer.entity;

import com.momoyeyu.smali_analyzer.element.SmaliMethod;
import com.momoyeyu.smali_analyzer.element.instructions.*;
import com.momoyeyu.smali_analyzer.enumeration.COMMENT;
import com.momoyeyu.smali_analyzer.enumeration.INSTRUCTION_TYPE;
import com.momoyeyu.smali_analyzer.utils.Formatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Stack;

public class Block {
    private String name;
    private final List<Instruction> instructions = new ArrayList<>();
    private final List<String> nextBlocks = new ArrayList<>();
    private String previousBlock;
    private SmaliMethod parentMethod;
    private BlockTable blockTable;
    private RegisterTable registerTable; // 值跟踪

    public Block(String name, SmaliMethod smaliMethod, BlockTable table) {
        this.name = name;
        this.parentMethod = smaliMethod;
        this.blockTable = table;
    }

    // adder
    public void addInstruction(Instruction instruction) {
        instructions.add(instruction);
    }

    public void addNextBlock(String name) {
        if (name == null || name.isEmpty() || nextBlocks.contains(name))
            return;
        nextBlocks.add(name);
    }

    public void removeNextBlock(String name) {
        nextBlocks.remove(name);
    }

    // getter
    public List<Block> getNextBlocks() {
        List<Block> nextBlocks = new ArrayList<>();
        for (String blockName : this.nextBlocks) {
            Block block = blockTable.getBlock(blockName);
            if (block != null)
                nextBlocks.add(block);
            else
                removeNextBlock(blockName);
        }
        return nextBlocks;
    }

    public Block getPreviousBlock() {
        return blockTable.getBlock(previousBlock);
    }

    public String getName() {
        return name;
    }

    // setter
    public void setPreviousBlock(String previousBlock) {
        this.previousBlock = previousBlock;
    }

    // functionality
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        INSTRUCTION_TYPE lastSubType = INSTRUCTION_TYPE.DEFAULT;
        INSTRUCTION_TYPE lastType = INSTRUCTION_TYPE.DEFAULT;
        Stack<Instruction> stack = new Stack<>();
        for (Instruction instruction : this.instructions) {
            COMMENT comment = instruction.getComment();
            INSTRUCTION_TYPE subType = instruction.getSubType();
            INSTRUCTION_TYPE type = instruction.getType();
            int indentLevel = blockTable.getIndentation();
//            instruction.updateTable();
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
            } else if (Instruction.equalType(type, INSTRUCTION_TYPE.CONDITION)) {
                ConditionInstruction condition = (ConditionInstruction) instruction;
                if (comment == COMMENT.IF) {
                    sb.append("\t".repeat(indentLevel)).append(condition.reverseCondition()).append(" {\n");
                    // add indentation
                }
            } else if (Instruction.equalType(subType, INSTRUCTION_TYPE.LABEL_CONDITION)) {
                if (comment == COMMENT.END_IF) {
                    // sub indentation
                    sb.append("\t".repeat(indentLevel)).append("}\n");
                } else if (comment == COMMENT.ELSE_IF) {
                    sb.append("\t".repeat(indentLevel - 1)).append("}\n");
                }
            } else if (Instruction.equalType(subType, INSTRUCTION_TYPE.LABEL_GOTO)) {
                if (comment == COMMENT.ELSE) {
                    // sub indentation
                    sb.append("\t".repeat(indentLevel)).append("} else {\n");
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
        return sb.toString();
    }

    public boolean isFakeBlock() {
        for (Instruction instruction : instructions) {
            if (!(instruction instanceof GotoInstruction || instruction instanceof Tag || instruction.getClass() == Instruction.class))
                return false;
        }
        return true;
    }
}
