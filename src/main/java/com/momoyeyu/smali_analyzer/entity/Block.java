package com.momoyeyu.smali_analyzer.entity;

import com.momoyeyu.smali_analyzer.element.SmaliMethod;
import com.momoyeyu.smali_analyzer.element.instructions.GotoInstruction;
import com.momoyeyu.smali_analyzer.element.instructions.Instruction;

import java.util.ArrayList;
import java.util.List;

public class Block {
    private String name;
    private final List<Instruction> instructions = new ArrayList<>();
    private final List<String> nextBlocks = new ArrayList<>();
    private String previousBlock;
    private SmaliMethod parentMethod;
    private BlockTable blockTable;
    private RegisterTable registerTable; // 值跟踪
    private int indentation = 0;

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
    public void setIndentation(int indentation) {
        this.indentation = indentation;
    }

    public void setPreviousBlock(String previousBlock) {
        this.previousBlock = previousBlock;
    }

    // functionality
    @Override
    public String toString() {
        return null;
    }

    public boolean isFakeBlock() {
        for (Instruction instruction : instructions) {
            if (!(instruction instanceof GotoInstruction))
                return false;
        }
        return true;
    }
}
