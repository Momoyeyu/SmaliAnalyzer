package com.momoyeyu.smali_analyzer.element.instructions;

import com.momoyeyu.smali_analyzer.element.SmaliMethod;
import com.momoyeyu.smali_analyzer.entity.RegisterTable;
import com.momoyeyu.smali_analyzer.utils.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OperationInstruction extends Instruction {
    private static final Pattern operationPattern = Pattern.compile(
            "^(add|sub|mul|div|rem|and|or|xor|shl|shr|ushr|neg)-(int|long|float|double)(/\\S+)?\\s+(.+)");

    private String operationType;
    private String valueType;

    private static final Map<String, String> operatorMap = new HashMap<>();
    static {
        operatorMap.put("add", "+");
        operatorMap.put("sub", "-");
        operatorMap.put("mul", "*");
        operatorMap.put("div", "/");
        operatorMap.put("rem", "%");
        operatorMap.put("and", "&");
        operatorMap.put("or", "|");
        operatorMap.put("xor", "^");
        operatorMap.put("shl", "<<");
        operatorMap.put("shr", ">>"); // 算数右移
        operatorMap.put("ushr", ">>>"); // 无符号右移
    }

    public static void main(String[] args) {
        System.out.println(new OperationInstruction("add-float/2addr v0, v1"));
        System.out.println(new OperationInstruction("add-float v0, v1, v2"));
        System.out.println(new OperationInstruction("add-int/lit8 v1, v1, 0x1"));
        System.out.println(new OperationInstruction("neg-int v3, v7"));
    }

    private OperationInstruction(String instruction) {
        this(instruction, null);
    }

    public OperationInstruction(String instruction, SmaliMethod parentMethod) {
        super(instruction, parentMethod);
        this.analyze();
    }

    @Override
    public void updateTable() {
        if (!updated) {
            if (registers.size() == 3 && newRegister()) {
                RegisterTable table = parentMethod.getRegisterTable();
                table.storeVariable(registers.getFirst(), valueType);
            }
            super.updateTable();
        }
    }

    @Override
    protected void analyze() {
        Matcher matcher = operationPattern.matcher(signature);
        if (matcher.find()) {
            operationType = matcher.group(1);
            valueType = matcher.group(2);
            registers = getRegistersList(matcher.group(4));
            super.analyze();
        }
    }

    @Override
    public String toString() {
        if (!analyzed)
            return analysisFail("calculation");
        StringBuilder sb = new StringBuilder();
        if (operationType.equals("neg")) {
            if (newRegister())
                sb.append(valueType).append(" ");
            sb.append(registers.get(0)).append(" = -").append(registers.get(1));
        } else if (registers.size() == 3) {
            if (newRegister())
                sb.append(valueType).append(" ");
            sb.append(registers.get(0)).append(" = ");
            sb.append(registers.get(1)).append(" ").append(operatorMap.get(operationType)).append(" ").append(registers.get(2));
        } else if (registers.size() == 2) {
            sb.append(registers.get(0)).append(" = ").append(registers.get(0)).append(" ").append(operatorMap.get(operationType)).append(" ").append(registers.get(1));
        } else {
            Logger.logAnalysisFailure("operation", signature);
            sb.append("// ").append(signature);
        }
        return sb.toString();
    }

    @Override
    public INSTRUCTION_TYPE getType() {
        return INSTRUCTION_TYPE.OPERATION;
    }

    @Override
    public INSTRUCTION_TYPE getSubType() {
        if (registers.size() == 3)
            return INSTRUCTION_TYPE.TRI_OPERATION;
        return INSTRUCTION_TYPE.BIN_OPERATION;
    }

    public static boolean isOperationInstruction(String instruction) {
        if (instruction == null)
            return false;
        return operationPattern.matcher(instruction).find();
    }

    private boolean newRegister() {
        String first = registers.getFirst();
        for (int i = 1; i < registers.size(); i++) {
            if (first.equals(registers.get(i)))
                return false;
        }
        return true;
    }
}
