package com.momoyeyu.smali_analyzer.element.instructions;

import com.momoyeyu.smali_analyzer.element.SmaliMethod;
import com.momoyeyu.smali_analyzer.utils.Stepper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MovArrayInstruction extends Instruction {

    private static final Pattern arrayMovPattern = Pattern.compile("^a((put)|(get))(-(\\S+))?\\s+((\\S+),\\s*(\\S+),\\s*(\\S+))\\s*");

    public static void main(String[] args) {
        System.out.println(new MovArrayInstruction("aput-object v4, v3, v0"));
        System.out.println(new MovArrayInstruction("aget-object v4, p1, v3"));
    }

    private MovArrayInstruction(String instruction) {
        this(instruction, null);
    }

    public MovArrayInstruction(String instruction, SmaliMethod parentMethod) {
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
            registers = getRegistersList(matcher.group(stp.step(2)));
            super.analyze();
        }
    }

    @Override
    public INSTRUCTION_TYPE getSubType() {
        return INSTRUCTION_TYPE.MOV_ARRAY;
    }

    @Override
    public INSTRUCTION_TYPE getType() {
        return INSTRUCTION_TYPE.MOV_ARRAY;
    }

    @Override
    public String toString() {
        if (!analyzed) {
            return analysisFail("array mov");
        }
        StringBuilder sb = new StringBuilder();
        String valueRegister = registers.get(0);
        String arrayRegister = registers.get(1);
        String indexRegister = registers.get(2);
        if (operation.startsWith("aput")) {
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
