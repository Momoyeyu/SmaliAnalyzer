package com.momoyeyu.smali_analyzer.element.instructions;

import com.momoyeyu.smali_analyzer.element.SmaliMethod;
import com.momoyeyu.smali_analyzer.utils.TypeUtils;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MovInstruction extends Instruction {

    private static final Pattern movPattern = Pattern.compile("move(-object|-wide)?(/\\S+)?\\s+(.+)");

    private String dataType = "Object";

    public static void main(String[] args) {
        System.out.println(new MovInstruction("move-object v6, v1"));
        System.out.println(new MovInstruction("move v0, v3"));
        System.out.println(new MovInstruction("move/from16 v31, v5"));
        System.out.println(new MovInstruction("move-object/from16 v28, v6"));
        System.out.println(new MovInstruction("move-wide v3, v5"));
    }

    // testing
    private MovInstruction(String instruction) {
        this(instruction, null);
    }

    public MovInstruction(String instruction, SmaliMethod smaliMethod) {
        super(instruction, smaliMethod);
        this.analyze();
    }

    @Override
    public void updateTable() {
        if (!updated) {
            dataType = registerTable.getVariableType(registers.get(1));
            if (!registers.getFirst().equals(registers.get(1)))
                registerTable.storeVariable(registers.getFirst(), dataType);
            super.updateTable();
        }
    }

    @Override
    protected void analyze() {
        Matcher matcher = movPattern.matcher(signature);
        if (matcher.find()) {
            operation = "move" + Objects.requireNonNullElse(matcher.group(1), "");
            registers = getRegistersList(matcher.group(3));
            super.analyze();
        }
    }

    @Override
    public INSTRUCTION_TYPE getSubType() {
        return INSTRUCTION_TYPE.MOV;
    }

    @Override
    public INSTRUCTION_TYPE getType() {
        return INSTRUCTION_TYPE.MOV;
    }

    @Override
    public String toString() {
        if (!analyzed) {
            return analysisFail("mov");
        }
        StringBuilder sb = new StringBuilder();
        sb.append(TypeUtils.getNameFromJava(dataType)).append(" ").append(registers.getFirst()).append(" = ").append(registers.get(1));
        return sb.toString();
    }

    public static boolean isMovInstruction(String instruction) {
        if (instruction == null)
            return false;
        return movPattern.matcher(instruction).matches();
    }

}
