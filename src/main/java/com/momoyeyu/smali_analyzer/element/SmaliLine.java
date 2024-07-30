package com.momoyeyu.smali_analyzer.element;

import java.util.List;

public class SmaliLine {

    private final List<String> instructions;

    public SmaliLine(final List<String> lines) {
        this.instructions = lines;
    }

}
