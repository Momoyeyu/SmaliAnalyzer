package com.momoyeyu.smali_analyzer.element;

import java.util.List;

public class SmaliLine {

    private final List<SmaliInstruction> instructions;

    public SmaliLine(List<SmaliInstruction> lines) {
        this.instructions = lines;
    }

}
