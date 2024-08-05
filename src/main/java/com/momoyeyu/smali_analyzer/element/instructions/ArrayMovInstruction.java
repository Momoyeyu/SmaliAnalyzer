package com.momoyeyu.smali_analyzer.element.instructions;

import com.momoyeyu.smali_analyzer.element.SmaliMethod;
import com.momoyeyu.smali_analyzer.entity.MethodStack;
import com.momoyeyu.smali_analyzer.utils.Stepper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArrayMovInstruction extends Instruction {

    private static final Pattern arrayMovPattern = Pattern.compile("^a((put)|(get))(-(\\S+))?\\s+(\\S+),\\s*(\\S+),\\s*(\\S+)\\s*");

    private String valueRegister;
    private String arrayRegister;
    private String indexRegister;

    public static void main(String[] args) {
        System.out.println(new ArrayMovInstruction("aput-object v4, v3, v0"));
        System.out.println(new ArrayMovInstruction("aget-object v4, p1, v3"));
    }

    private ArrayMovInstruction(String instruction) {
        this(instruction, null);
    }

    public ArrayMovInstruction(String instruction, SmaliMethod parentMethod) {
        super(instruction, parentMethod);
        this.analyze();
    }

    @Override
    protected void analyze() {
        Matcher matcher = arrayMovPattern.matcher(signature);
        if (matcher.matches()) {
            Stepper stp = new Stepper();
            operation = "a" + matcher.group(stp.step(1));
            operation += matcher.group(stp.step(3)) == null ? "" : matcher.group(stp.step(0));
            valueRegister = getRegistersList(matcher.group(stp.step(2))).getFirst();
            arrayRegister = getRegistersList(matcher.group(stp.step(1))).getFirst();
            indexRegister = getRegistersList(matcher.group(stp.step(1))).getFirst();
            super.analyze();
        }
    }

    @Override
    public void updateTable() {
        MethodStack stack = parentMethod.getStack();
        if (parentMethod != null) {
            arrayRegister = stack.getValue(arrayRegister);
            indexRegister = stack.getValue(indexRegister);
            valueRegister = stack.getValue(valueRegister);
            if (operation.substring(1).startsWith("put")) {
                stack.storeVariable(arrayRegister, indexRegister, valueRegister, "array");
            } else {
                stack.storeVariable(valueRegister, null,
                        arrayRegister + "[" + indexRegister + "]", "array");
            }
        }
    }

    @Override
    public String toString() {
        if (!analyzed) {
            return analysisFail("array mov");
        }
        StringBuilder sb = new StringBuilder();
        if (operation.substring(1).startsWith("put")) {
            sb.append(arrayRegister).append("[").append(indexRegister).append("]");
            sb.append(" = ").append(valueRegister);
        } else {
            sb.append(valueRegister).append(" = ");
            sb.append(arrayRegister).append("[").append(indexRegister).append("]");
        }
        return sb.toString();
    }

    public static boolean isArrayMovInstruction(String instruction) {
        if (instruction == null)
            return false;
        return arrayMovPattern.matcher(instruction).matches();
    }
}
