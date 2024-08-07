package com.momoyeyu.smali_analyzer.entity;

import com.momoyeyu.smali_analyzer.element.instructions.Instruction;

import java.util.ArrayList;
import java.util.List;

public class InstructionBlock {

    private final List<Instruction> lines;

    public InstructionBlock() {
        lines = new ArrayList<>();
    }

}
