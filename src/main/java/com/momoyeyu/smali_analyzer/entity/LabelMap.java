package com.momoyeyu.smali_analyzer.entity;

import com.momoyeyu.smali_analyzer.element.SmaliMethod;
import com.momoyeyu.smali_analyzer.element.instructions.Instruction;
import com.momoyeyu.smali_analyzer.enumeration.COMMENT;
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
                        instructions.get(reference).setComment(COMMENT.ELSE);
                    else // reference after origin: continue / end while loop
                        instructions.get(reference).setComment(COMMENT.CONTINUE);
                }
                int max = Collections.max(references);
                if (max < origin) {
                    instructions.get(origin).setComment(COMMENT.WHILE);
                    instructions.get(max).setComment(COMMENT.END_WHILE);
                } else {
                    instructions.get(origin).setComment(COMMENT.END_ELSE);
                }
            }
            if (labelInfo.getLabelType() == INSTRUCTION_TYPE.LABEL_CONDITION) {
                for (int reference : references) {
                    if (reference < origin) // reference before origin: if
                        instructions.get(reference).setComment(COMMENT.IF);
                    else // reference after origin:
                        instructions.get(reference).setComment(COMMENT.CONTINUE);
                }
                int max = Collections.max(references);
                if (max < origin) {
                    instructions.get(origin).setComment(COMMENT.DO_WHILE);
                    instructions.get(max).setComment(COMMENT.END_DO_WHILE);
                } else {
                    instructions.get(origin).setComment(COMMENT.END_IF);
                }
            }
        } // end for (LabelInfo labelInfo : labels.values())
    }
}
