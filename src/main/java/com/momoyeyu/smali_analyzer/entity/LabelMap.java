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

    /**
     * Write comment for all label and jump statement of method.
     * @param instructions all instruction of the method
     */
    @Override
    public void arrangeInstruction(List<Instruction> instructions) {
        List<Loop> loops = new ArrayList<>();
        Map<Integer, Integer> elseOrBreak = new HashMap<>();
        Map<Integer, Integer> ifOrBreak = new HashMap<>();
        for (LabelInfo labelInfo : labels.values()) {
            int origin = labelInfo.getOrigin();
            Set<Integer> references = labelInfo.getReferences();
            if (labelInfo.getLabelType() == INSTRUCTION_TYPE.LABEL_GOTO) { // loop / break / continue / else
                for (int reference : references) {
                    if (reference < origin) { // reference before origin: else / break
                        instructions.get(reference).setComment(COMMENT.ELSE); // else by default
                        elseOrBreak.put(reference, origin); // or break
                    } else { // reference after origin: continue / end while loop
                        instructions.get(reference).setComment(COMMENT.CONTINUE);
                    }
                }
                // find end loop
                int max = Collections.max(references);
                if (origin < max) { // label before max
                    instructions.get(origin).setComment(COMMENT.WHILE); // label
                    instructions.get(max).setComment(COMMENT.END_WHILE); // goto
                    loops.add(new Loop(origin, max));
                } else {
                    instructions.get(origin).setComment(COMMENT.END_ELSE);
                }
            }
            if (labelInfo.getLabelType() == INSTRUCTION_TYPE.LABEL_CONDITION) { // if / do while
                for (int reference : references) {
                    if (reference < origin) { // reference before origin: if
                        instructions.get(reference).setComment(COMMENT.IF);
                        ifOrBreak.put(reference, origin); // or break
                    } else { // reference after origin:
                        instructions.get(reference).setComment(COMMENT.IF_CONTINUE);
                    }
                }
                // find end loop
                int max = Collections.max(references);
                if (origin < max) { // label before condition
                    instructions.get(origin).setComment(COMMENT.DO_WHILE); // label
                    instructions.get(max).setComment(COMMENT.END_DO_WHILE); // condition
                    loops.add(new Loop(origin, max));
                } else {
                    instructions.get(origin).setComment(COMMENT.END_IF);
                }
            }
        } // end for (LabelInfo labelInfo : labels.values())
        // find break
        for (int line : elseOrBreak.keySet()) {
            for (Loop loop : loops) {
                if (loop.contain(line) && !loop.contain(elseOrBreak.get(line))) {
                    instructions.get(line).setComment(COMMENT.BREAK);
                    break;
                }
            }
        }
        for (int line : ifOrBreak.keySet()) {
            for (Loop loop : loops) {
                if (loop.contain(line) && !loop.contain(elseOrBreak.get(line))) {
                    instructions.get(line).setComment(COMMENT.IF_BREAK);
                    break;
                }
            }
        }
    } // end method

    private static class Loop {
        int start;
        int end;
        Loop(int start, int end) {
            this.start = start;
            this.end = end;
        }

        public boolean contain(int line) {
            return start < line && line < end;
        }
    }
}
