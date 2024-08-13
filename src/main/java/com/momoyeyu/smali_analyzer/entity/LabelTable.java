package com.momoyeyu.smali_analyzer.entity;

import com.momoyeyu.smali_analyzer.element.instructions.Label;

public interface LabelTable {

    void addLabel(String label);

    Label getLabel(String label);

    /**
     * Make sure there is only one Label instance name {@code label}
     * @param label the name of Label instance
     * @return the corresponding Label instance
     */
    Label accessLabel(String label);
}
