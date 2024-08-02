package com.momoyeyu.smali_analyzer.element;

import com.momoyeyu.smali_analyzer.element.instructions.SmaliMovInstruction;

import java.util.List;

public class SmaliLine {

    private final List<SmaliMovInstruction> instructions;

    public SmaliLine(List<SmaliMovInstruction> lines) {
        this.instructions = lines;
    }

}
