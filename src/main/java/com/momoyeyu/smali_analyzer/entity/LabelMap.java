package com.momoyeyu.smali_analyzer.entity;

import com.momoyeyu.smali_analyzer.element.SmaliMethod;
import com.momoyeyu.smali_analyzer.element.instructions.Label;

import java.util.HashMap;
import java.util.Map;

public class LabelMap implements LabelTable {

    private final Map<String, Label> labels = new HashMap<>();;
    private SmaliMethod parentMethod;

    public LabelMap(SmaliMethod smaliMethod) {
        parentMethod = smaliMethod;
    }

    @Override
    public Label getLabel(String label) {
        return labels.get(label);
    }

    @Override
    public void addLabel(String label) {
        if (!labels.containsKey(label))
            labels.put(label, new Label(label, parentMethod));
    }

    @Override
    public Label accessLabel(String label) {
        this.addLabel(label);
        return labels.get(label);
    }
}
