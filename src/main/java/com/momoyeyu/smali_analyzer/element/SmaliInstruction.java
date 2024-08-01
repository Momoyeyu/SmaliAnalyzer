package com.momoyeyu.smali_analyzer.element;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmaliInstruction {

    private static final Pattern instructionPattern = Pattern.compile("(\\S+)\\s+(\\S+)(,\\s*(\\S+))?");

    protected String signature;

    protected String operation;
    protected List<String> registers;
    private String value;

    public SmaliInstruction(String instruction) {
        signature = instruction;
    }

    protected void analyze() {
        Matcher matcher = instructionPattern.matcher(signature);
        if (matcher.find()) {
            operation = matcher.group(1);
            registers = getRegistersList(matcher.group(2));
            value = matcher.group(4);
        }
    }

    protected static List<String> getRegistersList(String registers) {
        return Arrays.stream(registers.split("(,\\s*)")).toList();
    }

    @Override
    public String toString() {
        this.analyze();
        return null;
    }

}
