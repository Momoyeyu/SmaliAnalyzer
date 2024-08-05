package com.momoyeyu.smali_analyzer.element;

import com.momoyeyu.smali_analyzer.element.instructions.Instruction;

import java.util.ArrayList;
import java.util.List;

public class SmaliLine {

    private int status;

    private final List<Instruction> instructions;

    private SmaliLine() {
        this(new ArrayList<>());
    }

    public SmaliLine(List<Instruction> lines) {
        this.instructions = lines;
        this.status = 0;
    }



}
