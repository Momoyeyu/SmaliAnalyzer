package com.momoyeyu.smali_analyzer.entity;

import com.momoyeyu.smali_analyzer.element.SmaliMethod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockMap implements BlockTable {

    private final Map<String, Block> blocks = new HashMap<>();

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
        Block block = new Block(parentMethod);
        blocks.put(name, block);
        return block;
    }

    @Override
    public List<Block> getFollowingBlocks(String name) {
        return List.of();
    }
}
