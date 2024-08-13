package com.momoyeyu.smali_analyzer.entity;

import com.momoyeyu.smali_analyzer.element.SmaliMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockMap implements BlockTable {

    private final Map<String, Block> blocks = new HashMap<>();
    private int indentation;

    private SmaliMethod parentMethod;

    public BlockMap(SmaliMethod parentMethod) {
        this.parentMethod = parentMethod;
    }

    @Override
    public Block getBlock(String name) {
        return blocks.get(name);
    }

    @Override
    public Block newBlock(String name) {
        Block block = new Block(name, parentMethod, this);
        blocks.put(name, block);
        return block;
    }

    @Override
    public List<Block> getNextBlocks(String name) {
        return getNextBlocks(getBlock(name));
    }

    @Override
    public List<Block> getNextBlocks(Block block) {
        if (block == null)
            return null;
        return block.getNextBlocks();
    }

    @Override
    public void linkBlocks() {
//        linkBlocks("start");
    }

    private void linkBlocks(String name) {
        Block block = getBlock(name);
        List<Block> nextBlocks = block.getNextBlocks();
        for (Block nextBlock : nextBlocks) {
            nextBlock.setPreviousBlock(block.getName());
            linkBlocks(nextBlock.getName());
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        return sb.toString();
    }
}
