package com.momoyeyu.smali_analyzer.entity;

import com.momoyeyu.smali_analyzer.element.SmaliMethod;

import java.util.HashMap;
import java.util.Map;

public class RegisterTable {

    private final Map<String, Variable> table = new HashMap<>();

    public RegisterTable(SmaliMethod smaliMethod) {
        int i = smaliMethod.isStaticModifier() ? 0 : 1;
        for (String parameter : smaliMethod.getParametersList()) {
            table.put(String.format("p%d", i), new Variable(parameter, null));
        }
    }

    public static class Variable {
        private String type;
        private String value;

        public Variable(String type, String value) {
            this.type = type;
            this.value = value;
        }

        public Variable() {}

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
