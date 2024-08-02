package com.momoyeyu.smali_analyzer.element.instructions;

import com.momoyeyu.smali_analyzer.element.SmaliMethod;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConditionInstruction extends Instruction {

    private static final Pattern conditionPattern = Pattern.compile("if-(\\S+)\\s+(.*),\\s*:cond_(\\S+)");

    private String condition;
    private List<String> registers;
    private String conditionLabel;

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
    public String toString() {
        if (!analyzed)
            return super.toString();
        StringBuilder sb = new StringBuilder();
        sb.append("if (");
        if (condition.equals("eq")) {
            sb.append(registers.getFirst()).append(" == ").append(registers.getLast());
        } else if (condition.equals("ne")) {
            sb.append(registers.getFirst()).append(" != ").append(registers.getLast());
        } else if (condition.equals("lt")) {
            sb.append(registers.getFirst()).append(" < ").append(registers.getLast());
        } else if (condition.equals("le")) {
            sb.append(registers.getFirst()).append(" <= ").append(registers.getLast());
        } else if (condition.equals("gt")) {
            sb.append(registers.getFirst()).append(" > ").append(registers.getLast());
        } else if (condition.equals("ge")) {
            sb.append(registers.getFirst()).append(" >= ").append(registers.getLast());
        } else if (condition.equals("eqz")) {
            sb.append(registers.getFirst()).append(" == 0");
        } else if (condition.equals("nez")) {
            sb.append(registers.getFirst()).append(" != 0");
        } else if (condition.equals("ltz")) {
            sb.append(registers.getFirst()).append(" < 0");
        } else if (condition.equals("lez")) {
            sb.append(registers.getFirst()).append(" <= 0");
        } else if (condition.equals("gtz")) {
            sb.append(registers.getFirst()).append(" > 0");
        } else if (condition.equals("gez")) {
            sb.append(registers.getFirst()).append(" >= 0");
        }
        sb.append(")");
        return sb.toString();
    }

    public static boolean isConditionInstruction(String instruction) {
        if (instruction == null) {
            return false;
        }
        return conditionPattern.matcher(instruction).matches();
    }

}
