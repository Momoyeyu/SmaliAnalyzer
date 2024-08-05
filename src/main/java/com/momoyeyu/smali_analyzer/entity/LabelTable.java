package com.momoyeyu.smali_analyzer.entity;

import com.momoyeyu.smali_analyzer.element.SmaliMethod;
import com.momoyeyu.smali_analyzer.element.instructions.Label;

import java.util.HashMap;
import java.util.Map;

public class LabelTable {

    private Map<String, Label> labels;
    private SmaliMethod parentMethod;

    public LabelTable() {
        labels = new HashMap<String, Label>();
    }

    public Label getLabel(String label) {
        return labels.get(label);
    }

    public void addLabel(String label) {
        if (!labels.containsKey(label))
            labels.put(label, new Label(label));
    }

}
