package com.momoyeyu.smali_analyzer.element.instructions;

import com.momoyeyu.smali_analyzer.element.SmaliMethod;
import com.momoyeyu.smali_analyzer.enumeration.INSTRUCTION_TYPE;
import com.momoyeyu.smali_analyzer.utils.TypeUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InstanceOfInstruction extends Instruction{
    private static final Pattern instanceOfPattern = Pattern.compile("^instance-of\\s+(.*),\\s*(\\S+);?");

    private String instanceType;
    private String oldName;

    public InstanceOfInstruction(String instruction, SmaliMethod parentMethod) {
        super(instruction, parentMethod);
        this.analyze();
    }

    @Override
    protected void analyze() {
        Matcher matcher = instanceOfPattern.matcher(signature);
        if (matcher.find()) {
            operation = "instance-of";
            registers = getRegistersList(matcher.group(1));
            instanceType = matcher.group(2);
            super.analyze();
        }
    }

    @Override
    public void updateTable() {
        if (!updated) {
            oldName = registerTable.getVariableName(registers.getLast());
            registerTable.storeVariable(registers.getFirst(), "boolean");
            super.updateTable();
        }
    }

    @Override
    public String toString() {
        if (!analyzed)
            return analysisFail("instance of");
        return "boolean " + registers.getFirst() + " = " + oldName + " instanceof " + TypeUtils.getNameFromJava(instanceType);
    }

    @Override
    public INSTRUCTION_TYPE getType() {
        return INSTRUCTION_TYPE.INSTANCE_OF;
    }

    @Override
    public INSTRUCTION_TYPE getSubType() {
        return INSTRUCTION_TYPE.INSTANCE_OF;
    }

    public static boolean isInstanceOfInstruction(String instruction) {
        if (instruction == null)
            return false;
        return instanceOfPattern.matcher(instruction).matches();
    }
}
