package com.momoyeyu.smali_analyzer.entity;

import com.momoyeyu.smali_analyzer.element.SmaliMethod;
import com.momoyeyu.smali_analyzer.element.instructions.Instruction;

import java.util.ArrayList;
import java.util.List;

public class Block {
    private String name;
    private final List<Instruction> instructions = new ArrayList<>();
    private final List<String> nextBlocks = new ArrayList<>();
    private String previousBlock;
    private SmaliMethod parentMethod;
    private BlockTable table;
    private int indentation = 0;

    public Block(String name, SmaliMethod smaliMethod, BlockTable table) {
        this.name = name;
        this.parentMethod = smaliMethod;
        this.table = table;
    }

    // adder
    public void addInstruction(Instruction instruction) {
        instructions.add(instruction);
    }

    public void addNextBlock(String name) {
        nextBlocks.add(name);
    }

    // getter
    public List<Block> getNextBlocks() {
        List<Block> nextBlocks = new ArrayList<>();
        for (String blockName : this.nextBlocks) {
            Block block = table.getBlock(blockName);
            if (block != null)
                nextBlocks.add(block);
        }
        return nextBlocks;
    }

    public Block getPreviousBlock() {
        return table.getBlock(previousBlock);
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
}
