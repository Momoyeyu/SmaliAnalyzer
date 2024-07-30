package com.momoyeyu.smali_analyzer.entity;

import com.momoyeyu.smali_analyzer.element.SmaliLine;

import java.util.ArrayList;
import java.util.List;

public class InstructionBlock {

    private final List<SmaliLine> lines;

    public InstructionBlock() {
        lines = new ArrayList<SmaliLine>();
    }

}
