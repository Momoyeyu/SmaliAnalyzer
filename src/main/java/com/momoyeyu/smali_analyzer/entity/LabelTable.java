package com.momoyeyu.smali_analyzer.entity;


import com.momoyeyu.smali_analyzer.element.instructions.Instruction;
import com.momoyeyu.smali_analyzer.enumeration.INSTRUCTION_TYPE;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public interface LabelTable {

    void addLabel(String label, int line, INSTRUCTION_TYPE labelType);

    LabelInfo getLabel(String label);

    void useLabel(String label, int line);

    boolean isPeer(String label);

    void arrangeInstruction(List<Instruction> instructions);

    class LabelInfo {
        private String label;
        private INSTRUCTION_TYPE labelType;
        private int origin;
        private Set<Integer> references = new HashSet<>();

        public LabelInfo(String label) {
            this.label = label;
        }

        public LabelInfo(String label, int origin, INSTRUCTION_TYPE labelType) {
            this.label = label;
            this.origin = origin;
            this.labelType = labelType;
        }

        public String getLabel() {
            return label;
        }

        public void setOrigin(int origin) {
            this.origin = origin;
        }

        public void addReference(int reference) {
            references.add(reference);
        }

        public int getOrigin() {
            return origin;
        }

        public Set<Integer> getReferences() {
            return references;
        }

        public INSTRUCTION_TYPE getLabelType() {
            return labelType;
        }
    }
}
