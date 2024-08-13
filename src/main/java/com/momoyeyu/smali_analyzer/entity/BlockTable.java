package com.momoyeyu.smali_analyzer.entity;

import java.util.List;

public interface BlockTable {

    Block getBlock(String name);

    Block newBlock(String name);

    List<Block> getNextBlocks(String name);

    List<Block> getNextBlocks(Block block);

    void linkBlocks();
}
