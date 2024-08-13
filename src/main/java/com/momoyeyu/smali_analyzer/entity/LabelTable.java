package com.momoyeyu.smali_analyzer.entity;

import com.momoyeyu.smali_analyzer.element.instructions.Label;

public interface LabelTable {

    void addLabel(String label);

    Label getLabel(String label);

    Label accessLabel(String label);
}
