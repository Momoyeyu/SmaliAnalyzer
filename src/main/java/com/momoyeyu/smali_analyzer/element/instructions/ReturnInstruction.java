package com.momoyeyu.smali_analyzer.element.instructions;

import com.momoyeyu.smali_analyzer.element.SmaliMethod;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReturnInstruction extends Instruction {

    private static final Pattern returnPattern = Pattern.compile("return(-(\\S+))?(\\s+(\\S+))?");

    private String returnType;

    public static void main(String[] args) {
        System.out.println(new ReturnInstruction("return-void"));
        System.out.println(new ReturnInstruction("return-object v0"));
        System.out.println(new ReturnInstruction("return v1"));
    }

    private ReturnInstruction(String instruction)  {
        this(instruction, null);
    }

    public ReturnInstruction(String instruction, SmaliMethod parentMethod) {
        super(instruction, parentMethod);
        this.analyze();
    }

    @Override
    protected void analyze() {
        Matcher matcher = returnPattern.matcher(signature);
        if (matcher.matches()) {
            returnType = matcher.group(2) == null ? "" : matcher.group(2);
            registers = getRegistersList(matcher.group(4));
            super.analyze();
        }
    }

    @Override
    public String toString() {
        if (!analyzed) {
            return analysisFail("return");
        }
        if (returnType.equals("void")) {
            return "return";
        }
        return "return " + registers.getFirst();
    }

    public static boolean isReturnInstruction(String instruction) {
        if (instruction == null)
            return false;
        return returnPattern.matcher(instruction).matches();
    }
}
