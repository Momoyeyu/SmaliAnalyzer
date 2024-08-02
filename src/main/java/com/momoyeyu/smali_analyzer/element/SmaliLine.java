package com.momoyeyu.smali_analyzer.element;

import com.momoyeyu.smali_analyzer.element.instructions.MovInstruction;

import java.util.List;

public class SmaliLine {

    private final List<MovInstruction> instructions;

    public SmaliLine(List<MovInstruction> lines) {
        this.instructions = lines;
    }

}
