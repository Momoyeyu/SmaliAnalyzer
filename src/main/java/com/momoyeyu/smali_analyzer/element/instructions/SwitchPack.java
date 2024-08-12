package com.momoyeyu.smali_analyzer.element.instructions;

import com.momoyeyu.smali_analyzer.element.SmaliMethod;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SwitchPack extends Instruction {
    private static final Pattern pattern = Pattern.compile("\\.packed-switch\\s+(\\S+?),(.*),\\.end packed-switch");

    private String offset;
    private List<String> conditions;

    public SwitchPack(String instruction, SmaliMethod parentMethod) {
        super(instruction, parentMethod);
        this.analyze();
    }

    @Override
    protected void analyze() {
        if (!analyzed) {
            Matcher matcher = pattern.matcher(signature);
            if (matcher.matches()) {
                offset = matcher.group(1);
                conditions = getRegistersList(matcher.group(2));
                super.analyze();
            }
        }
    }

    @Override
    public INSTRUCTION_TYPE getType() {
        return INSTRUCTION_TYPE.SWITCH_PACK;
    }

    @Override
    public INSTRUCTION_TYPE getSubType() {
        return INSTRUCTION_TYPE.SWITCH_PACK;
    }

    public static boolean isSwitchPack(String instruction) {
        if (instruction == null)
            return false;
        return pattern.matcher(instruction).matches();
    }
}
