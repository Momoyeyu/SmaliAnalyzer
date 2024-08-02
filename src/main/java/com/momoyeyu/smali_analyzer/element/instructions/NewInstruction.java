package com.momoyeyu.smali_analyzer.element.instructions;

import com.momoyeyu.smali_analyzer.element.SmaliMethod;
import com.momoyeyu.smali_analyzer.utils.Stepper;
import com.momoyeyu.smali_analyzer.utils.TypeUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewInstruction extends Instruction {

    private static final Pattern newPattern = Pattern.compile("new(-(\\S+))?\\s+(.+),\\s*(\\S+);?");

    private String newType;

    public static void main(String[] args) {
        System.out.println(new NewInstruction("new-instance v4, Ljava/util/ArrayList;"));
        System.out.println(new NewInstruction("new-array v3, v3, [Ljava/lang/Object;"));
    }

    // test
    private NewInstruction(String instruction) {
        this(instruction, null);
    }

    public NewInstruction(String instruction, SmaliMethod parentMethod) {
        super(instruction, parentMethod);
        this.analyze();
    }

    @Override
    protected void analyze() {
        Matcher matcher = newPattern.matcher(signature);
        if (matcher.find()) {
            Stepper stp = new Stepper();
            operation = "new" + (matcher.group(stp.step(1)) == null ? "" : matcher.group(stp.step(0)));
            registers = getRegistersList(matcher.group(stp.step(2)));
            newType = TypeUtils.getTypeFromSmali(matcher.group(stp.step(1)));
            super.analyze();
        }
    }

    @Override
    public String toString() {
        if (!analyzed)
            return analysisFail("new");
        StringBuilder sb = new StringBuilder();
        sb.append(TypeUtils.getNameFromJava(newType)).append(" ").append(registers.getFirst());

        return sb.toString();
    }

    public static boolean isNewInstruction(String instruction) {
        if (instruction == null)
            return false;
        return newPattern.matcher(instruction).matches();
    }
}
