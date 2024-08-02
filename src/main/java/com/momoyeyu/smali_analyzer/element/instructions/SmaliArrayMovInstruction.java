package com.momoyeyu.smali_analyzer.element.instructions;

import com.momoyeyu.smali_analyzer.element.SmaliMethod;
import com.momoyeyu.smali_analyzer.utils.Stepper;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmaliArrayMovInstruction extends SmaliInstruction {

    private static final Pattern arrayMovPattern = Pattern.compile("^a((put)|(get))(-(\\S+))?\\s+(\\S+),\\s*(\\S+),\\s*(\\S+)\\s*");

    private String valueRegister;
    private String arrayRegister;
    private String indexRegister;

    public static void main(String[] args) {
        System.out.println(new SmaliArrayMovInstruction("aput-object v4, v3, v0"));
        System.out.println(new SmaliArrayMovInstruction("aget-object v4, p1, v3"));
    }

    private SmaliArrayMovInstruction(String instruction) {
        this(instruction, null);
    }

    public SmaliArrayMovInstruction(String instruction, SmaliMethod parentMethod) {
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
            valueRegister = matcher.group(stp.step(2));
            arrayRegister = matcher.group(stp.step(1));
            indexRegister = matcher.group(stp.step(1));
            super.analyze();
        }
    }

    @Override
    public String toString() {
        if (!analyzed) {
            return super.toString();
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
}
