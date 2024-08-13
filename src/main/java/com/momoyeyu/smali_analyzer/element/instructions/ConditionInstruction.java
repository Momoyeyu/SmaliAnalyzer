package com.momoyeyu.smali_analyzer.element.instructions;

import com.momoyeyu.smali_analyzer.element.SmaliMethod;
import com.momoyeyu.smali_analyzer.enumeration.INSTRUCTION_TYPE;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConditionInstruction extends Instruction {

    private static final Pattern conditionPattern = Pattern.compile("if-(\\S+)\\s+(.*),\\s*(:cond_(\\S+))");

    private String condition;
    private String conditionLabel;

    private static final Map<String, String> REVERSE_TABLE = new HashMap<>();

    static {
        REVERSE_TABLE.put("eq", "ne");
        REVERSE_TABLE.put("ne", "eq");
        REVERSE_TABLE.put("lt", "ge");
        REVERSE_TABLE.put("ge", "lt");
        REVERSE_TABLE.put("gt", "le");
        REVERSE_TABLE.put("le", "gt");
        REVERSE_TABLE.put("eqz", "nez");
        REVERSE_TABLE.put("nez", "eqz");
        REVERSE_TABLE.put("ltz", "gez");
        REVERSE_TABLE.put("gez", "ltz");
        REVERSE_TABLE.put("lez", "gtz");
        REVERSE_TABLE.put("gtz", "lez");
    }

    public static void main(String[] args) {
        String[] strings = {
                "if-eq vA, vB, :cond_x",
                "if-ne vA, vB, :cond_x",
                "if-lt vA, vB, :cond_x",
                "if-ge vA, vB, :cond_x",
                "if-gt vA, vB, :cond_x",
                "if-le vA, vB, :cond_x",
                "if-eqz vA, :cond_x",
                "if-nez vA, :cond_x",
                "if-ltz vA, :cond_x",
                "if-gez vA, :cond_x",
                "if-gtz vA, :cond_x",
                "if-lez vA, :cond_x",
        };
        for (String string : strings) {
            System.out.println(new ConditionInstruction(string));
        }
    }

    // test
    private ConditionInstruction(String instruction) {
        this(instruction, null);
    }

    public ConditionInstruction(String instruction, SmaliMethod parentMethod) {
        super(instruction, parentMethod);
        this.analyze();
    }

    @Override
    protected void analyze() {
        Matcher matcher = conditionPattern.matcher(signature);
        if (matcher.matches()) {
            condition = matcher.group(1);
            registers = getRegistersList(matcher.group(2));
            conditionLabel = matcher.group(3);
            super.analyze();
        }
    }

    @Override
    public INSTRUCTION_TYPE getSubType() {
        return INSTRUCTION_TYPE.CONDITION;
    }

    @Override
    public INSTRUCTION_TYPE getType() {
        return INSTRUCTION_TYPE.CONDITION;
    }

    @Override
    public String toString() {
        if (!analyzed)
            return analysisFail("condition");
        StringBuilder sb = new StringBuilder();
        sb.append("if (");
        switch (condition) {
            case "eq" -> sb.append(registers.getFirst()).append(" == ").append(registers.getLast());
            case "ne" -> sb.append(registers.getFirst()).append(" != ").append(registers.getLast());
            case "lt" -> sb.append(registers.getFirst()).append(" < ").append(registers.getLast());
            case "le" -> sb.append(registers.getFirst()).append(" <= ").append(registers.getLast());
            case "gt" -> sb.append(registers.getFirst()).append(" > ").append(registers.getLast());
            case "ge" -> sb.append(registers.getFirst()).append(" >= ").append(registers.getLast());
            case "eqz" -> sb.append(registers.getFirst()).append(" == 0");
            case "nez" -> sb.append(registers.getFirst()).append(" != 0");
            case "ltz" -> sb.append(registers.getFirst()).append(" < 0");
            case "lez" -> sb.append(registers.getFirst()).append(" <= 0");
            case "gtz" -> sb.append(registers.getFirst()).append(" > 0");
            case "gez" -> sb.append(registers.getFirst()).append(" >= 0");
        }
        sb.append(") goto ");
        sb.append(conditionLabel);
        return sb.toString();
    }

    public String reverseCondition() {
        if (!analyzed)
            return analysisFail("condition");
        StringBuilder sb = new StringBuilder();
        sb.append("if (");
        switch (REVERSE_TABLE.get(condition)) {
            case "eq" -> sb.append(registers.getFirst()).append(" == ").append(registers.getLast());
            case "ne" -> sb.append(registers.getFirst()).append(" != ").append(registers.getLast());
            case "lt" -> sb.append(registers.getFirst()).append(" < ").append(registers.getLast());
            case "le" -> sb.append(registers.getFirst()).append(" <= ").append(registers.getLast());
            case "gt" -> sb.append(registers.getFirst()).append(" > ").append(registers.getLast());
            case "ge" -> sb.append(registers.getFirst()).append(" >= ").append(registers.getLast());
            case "eqz" -> sb.append(registers.getFirst()).append(" == 0");
            case "nez" -> sb.append(registers.getFirst()).append(" != 0");
            case "ltz" -> sb.append(registers.getFirst()).append(" < 0");
            case "lez" -> sb.append(registers.getFirst()).append(" <= 0");
            case "gtz" -> sb.append(registers.getFirst()).append(" > 0");
            case "gez" -> sb.append(registers.getFirst()).append(" >= 0");
        }
        sb.append(")");
        return sb.toString();
    }

    // getter
    public String getConditionLabel() {
        return conditionLabel;
    }

    public static boolean isConditionInstruction(String instruction) {
        if (instruction == null) {
            return false;
        }
        return conditionPattern.matcher(instruction).matches();
    }

}
