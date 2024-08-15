package com.momoyeyu.smali_analyzer.entity;

import com.momoyeyu.smali_analyzer.element.SmaliMethod;
import com.momoyeyu.smali_analyzer.element.instructions.Instruction;
import com.momoyeyu.smali_analyzer.enumeration.INSTRUCTION_TYPE;

import java.util.*;

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
    public void arrangeInstruction(List<Instruction> instructions) {
        for (LabelInfo labelInfo : labels.values()) {
            int origin = labelInfo.getOrigin();
            Set<Integer> references = labelInfo.getReferences();
            String label = labelInfo.getLabel();
            if (labelInfo.getLabelType() == INSTRUCTION_TYPE.LABEL_GOTO) {
                for (int reference : references) {
                    if (reference < origin) // reference before origin: else
                        instructions.get(reference).setComment("else" + label);
                    else // reference after origin: continue / end while loop
                        instructions.get(reference).setComment("continue" + label);
                }
                int max = Collections.max(references);
                if (max < origin) {
                    instructions.get(origin).setComment("start while loop" + label);
                    instructions.get(max).setComment("end while loop" + label);
                } else {
                    instructions.get(origin).setComment("end else" + label);
                }
            }
            if (labelInfo.getLabelType() == INSTRUCTION_TYPE.LABEL_CONDITION) {
                for (int reference : references) {
                    if (reference < origin) // reference before origin: if
                        instructions.get(reference).setComment("if" + label);
                    else // reference after origin:
                        instructions.get(reference).setComment("continue" + label);
                }
                int max = Collections.max(references);
                if (max < origin) {
                    instructions.get(origin).setComment("start do while loop" + label);
                    instructions.get(max).setComment("end while loop" + label);
                } else {
                    instructions.get(origin).setComment("end if" + label);
                }
            }
        } // end for (LabelInfo labelInfo : labels.values())
    }
}
