package com.momoyeyu.smali_analyzer.entity;

import com.momoyeyu.smali_analyzer.element.SmaliMethod;
import com.momoyeyu.smali_analyzer.enumeration.INSTRUCTION_TYPE;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LabelMap implements LabelTable {

    private final Map<String, LabelInfo> labels = new HashMap<>();;
    private SmaliMethod parentMethod;

    public LabelMap(SmaliMethod smaliMethod) {
        parentMethod = smaliMethod;
    }

    @Override
    public LabelInfo getLabel(String label) {
        return labels.get(label);
    }

    @Override
    public void addLabel(String label, int line, INSTRUCTION_TYPE labelType) {
        if (!labels.containsKey(label))
            labels.put(label, new LabelInfo(label, line, labelType));
        else
            labels.get(label).setOrigin(line);
    }

    @Override
    public void useLabel(String label, int line) {
        if (!labels.containsKey(label))
            labels.put(label, new LabelInfo(label));
        labels.get(label).addReference(line);
    }

    @Override
    public boolean isPeer(String label) {
        return labels.get(label).getReferences().size() == 1;
    }

    @Override
    public List<LabelInfo> getLabels() {
        return List.copyOf(labels.values());
    }
}
