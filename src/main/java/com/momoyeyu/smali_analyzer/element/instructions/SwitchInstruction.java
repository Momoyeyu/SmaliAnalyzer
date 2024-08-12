package com.momoyeyu.smali_analyzer.element.instructions;

import com.momoyeyu.smali_analyzer.element.SmaliMethod;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SwitchInstruction extends Instruction{
    private static final Pattern pattern = Pattern.compile("^packed-switch\\s+(\\S+),\\s*(:\\S+)");

    private String label;

    public static void main(String[] args) {
        System.out.println(new SwitchInstruction("packed-switch p0, :pswitch_data_0"));
    }

    private SwitchInstruction(String instruction) {
        this(instruction, null);
    }

    public SwitchInstruction(String instruction, SmaliMethod parentMethod) {
        super(instruction, parentMethod);
        this.analyze();
    }

    @Override
    protected void analyze() {
        if (!analyzed) {
            Matcher matcher = pattern.matcher(signature);
            if (matcher.matches()) {
                operation = "packed-switch";
                registers = getRegistersList(matcher.group(1));
                label = matcher.group(2);
                super.analyze();
            }
        }
    }

    @Override
    public String toString() {
        if (!analyzed)
            return analysisFail("switch");
        return "switch (" + registers.getFirst() + ")";
    }

    @Override
    public INSTRUCTION_TYPE getType() {
        return INSTRUCTION_TYPE.SWITCH;
    }

    @Override
    public INSTRUCTION_TYPE getSubType() {
        return INSTRUCTION_TYPE.SWITCH;
    }

    public String getLabel() {
        return label;
    }

    public static boolean isSwitchInstruction(String instruction) {
        if (instruction == null)
            return false;
        return pattern.matcher(instruction).matches();
    }
}
