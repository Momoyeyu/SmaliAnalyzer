package com.momoyeyu.smali_analyzer.element.instructions;

import com.momoyeyu.smali_analyzer.element.SmaliMethod;
import com.momoyeyu.smali_analyzer.enumeration.INSTRUCTION_TYPE;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResultInstruction extends Instruction {

    private static final Pattern resultPattern = Pattern.compile("^move-result(\\S+)?\\s+(\\S+)");

    private String resultType = null;

    public static void main(String[] args) {
        System.out.println(new ResultInstruction("move-result-object v1"));
        System.out.println(new ResultInstruction("move-result v0"));
    }

    // test
    private ResultInstruction(String instruction) {
        this(instruction, null);
    }

    public ResultInstruction(String instruction, SmaliMethod parentMethod) {
        super(instruction, parentMethod);
        this.analyze();
    }

    @Override
    protected void analyze() {
        Matcher matcher = resultPattern.matcher(signature);
        if (matcher.matches()) {
            operation = "move-result" + (matcher.group(1) == null ? "" : matcher.group(1));
            registers = getRegistersList(matcher.group(2));
            super.analyze();
        }
    }

    @Override
    public void updateTable() {
        if (!updated && resultType != null) {
            registerTable.storeVariable(registers.getFirst(), resultType);
            super.updateTable();
        }
    }

    @Override
    public INSTRUCTION_TYPE getSubType() {
        return INSTRUCTION_TYPE.RESULT;
    }

    @Override
    public INSTRUCTION_TYPE getType() {
        return INSTRUCTION_TYPE.RESULT;
    }

    @Override
    public String toString() {
        if (!analyzed)
            return analysisFail("move-result");
        StringBuilder sb = new StringBuilder();
        sb.append(registers.getFirst()).append(" =");
        return sb.toString();
    }

    // setter
    public void setResultType(String resultType) {
        updated = false;
        this.resultType = resultType;
    }

    public static boolean isResultInstruction(String instruction) {
        if (instruction == null)
            return false;
        return resultPattern.matcher(instruction).matches();
    }
}
