package com.momoyeyu.smali_analyzer.entity;

import com.momoyeyu.smali_analyzer.element.SmaliMethod;
import com.momoyeyu.smali_analyzer.element.instructions.Instruction;

import java.util.ArrayList;
import java.util.List;

public class Block {

    private final List<Block> blocks = new ArrayList<>();
    protected SmaliMethod parentMethod;

    public Block(SmaliMethod smaliMethod) {
        this.parentMethod = smaliMethod;
    }

    public void addBlock(Block block) {
        blocks.add(block);
    }

    public void addInstruction(Instruction instruction) {
        blocks.add(instruction);
    }

}
