package com.momoyeyu.smali_analyzer.entity;

import com.momoyeyu.smali_analyzer.element.SmaliMethod;

import java.util.*;

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
    public void computeBlockPath() {
        Set<Block> visited = new HashSet<>();
        Block block = getBlock("start");
        visited.add(block);
        computeBlockPath(block, visited);
    }

    private void computeBlockPath(Block block, Set<Block> visited) {
        List<Block> nextBlocks = block.getNextBlocks();
        for (Block nextBlock : nextBlocks) {
            if (visited.contains(nextBlock))
                continue;
            nextBlock.setPreviousBlock(block.getName());
            computeBlockPath(nextBlock, visited);
        }
    }

    private void removeFakeBlock() {

    }

    private void removeFakeBlock(Block block) {
        Block next = block.getNextBlocks().getFirst();
        List<Block> parentBlocks = new ArrayList<>();
        for (Block b : blocks.values()) {
            if (b.getNextBlocks().contains(block)) {
                b.removeNextBlock(block.getName());
                b.addNextBlock(next.getName());
            }
        }
        blocks.remove(block.getName());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        return sb.toString();
    }
}
