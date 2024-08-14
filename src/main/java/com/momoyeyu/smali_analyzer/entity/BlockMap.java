package com.momoyeyu.smali_analyzer.entity;

import com.momoyeyu.smali_analyzer.element.SmaliMethod;

import java.util.*;

public class BlockMap implements BlockTable {

    private final Map<String, Block> blocks = new HashMap<>();
    private int indentation = 0;

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
    public String newName(String name) {
        if (blocks.containsKey(name)) {
            int i = 1;
            while (blocks.containsKey(name + "_" + i))
                i += 1;
            return name + "_" + i;
        }
        return name;
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
        removeFakeBlock();
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
            visited.add(nextBlock);
            computeBlockPath(nextBlock, visited);
        }
    }

    private void removeFakeBlock() {
        boolean finish = false;
        while (!finish && !blocks.isEmpty()) {
            for (Block block : blocks.values()) {
                if (block.isFakeBlock() && !block.getName().equals("start")) {
                    removeFakeBlock(block);
                    break;
                }
                finish = true;
            }
        }
    }

    private void removeFakeBlock(Block block) {
        if (!block.getNextBlocks().isEmpty()) {
            Block next = block.getNextBlocks().getFirst();
            List<Block> parentBlocks = new ArrayList<>();
            for (Block b : blocks.values()) {
                if (b.getNextBlocks().contains(block)) {
                    b.removeNextBlock(block.getName());
                    b.addNextBlock(next.getName());
                }
            }
        }
        blocks.remove(block.getName());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Block block = getBlock("start");

        return sb.toString();
    }
}
