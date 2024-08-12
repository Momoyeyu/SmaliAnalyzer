package com.momoyeyu.smali_analyzer.element.instructions;

import com.momoyeyu.smali_analyzer.element.SmaliMethod;
import com.momoyeyu.smali_analyzer.utils.TypeUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FillArrayDataInstruction extends Instruction{
    private static final Pattern pattern = Pattern.compile("^fill-array-data\\s+(\\S+),\\s*(:\\S+)");

    private String label;
    private String dataType;

    public static void main(String[] args) {
        System.out.println(new FillArrayDataInstruction("fill-array-data v1, :array_0"));
    }

    private FillArrayDataInstruction(String instruction) {
        this(instruction, null);
    }

    public FillArrayDataInstruction(String instruction, SmaliMethod parentMethod) {
        super(instruction, parentMethod);
        analyze();
    }

    @Override
    protected void analyze() {
        if (!analyzed) {
            Matcher matcher = pattern.matcher(signature);
            if (matcher.matches()) {
                registers = getRegistersList(matcher.group(1));
                label = matcher.group(2);
                super.analyze();
            }
        }
    }

    @Override
    public void updateTable() {
        if (!updated) {
            if (dataType != null)
                registerTable.storeVariable(registers.getFirst(), dataType);
            else
                registerTable.storeVariable(registers.getFirst(), "java.lang.Object");
            super.updateTable();
        }
    }

    @Override
    public String toString() {
        if (!analyzed)
            return analysisFail("fill-array-data");
        if (dataType != null)
            return TypeUtils.getNameFromJava(dataType) + "[] " + registers.getFirst();
        return "Object[] " + registers.getFirst();
    }

    @Override
    public INSTRUCTION_TYPE getType() {
        return INSTRUCTION_TYPE.FILL_ARRAY_DATA;
    }

    @Override
    public INSTRUCTION_TYPE getSubType() {
        return INSTRUCTION_TYPE.FILL_ARRAY_DATA;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getLabel() {
        return label;
    }

    public static boolean isFillArrayDataInstruction(String instruction) {
        if (instruction == null)
            return false;
        return pattern.matcher(instruction).matches();
    }
}
