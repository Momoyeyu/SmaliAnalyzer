package com.momoyeyu.smali_analyzer.element;

import com.momoyeyu.smali_analyzer.analyzers.MethodAnalyzer;
import com.momoyeyu.smali_analyzer.utils.Stepper;
import com.momoyeyu.smali_analyzer.utils.TypeUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmaliCallInstruction extends SmaliInstruction {

    private static final Pattern callPattern = Pattern.compile("invoke-(\\S+)\\s+\\{(.*?)},\\s*(\\S+)->(\\S+)\\((\\S*)\\)(\\S+);?");

    private String callee;
    private String methodName;
    private List<String> parameters;
    private String returnType;
    private boolean isStatic;

    public static void main(String[] args) {
        System.out.println(new SmaliCallInstruction("invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;"));
        System.out.println(new SmaliCallInstruction("invoke-static {p2}, Ljava/util/Collections;->sort(Ljava/util/List;)V"));
    }

    public SmaliCallInstruction(String instruction) {
        super(instruction);
        isStatic = false;
    }

    @Override
    protected void analyze() {
        Matcher matcher = callPattern.matcher(signature);
        if (matcher.find()) {
            Stepper stepper = new Stepper();
            operation = "invoke-" + matcher.group(stepper.step(1));
            isStatic = matcher.group(stepper.step(0)).equals("static");
            registers = getRegistersList(matcher.group(stepper.step(1)));
            callee = TypeUtils.getTypeFromSmali(matcher.group(stepper.step(1)));
            methodName = matcher.group(stepper.step(1));
            parameters = TypeUtils.getJavaParametersFromSmali(matcher.group(stepper.step(1)));
            returnType = TypeUtils.getTypeFromSmali(matcher.group(stepper.step(1)));
        }
    }

    @Override
    public String toString() {
        this.analyze();
        StringBuilder builder = new StringBuilder();
        if (isStatic) {
//            builder.append(TypeUtils.getNameFromJava(returnType)).append(" result = ");
            builder.append(TypeUtils.getNameFromJava(callee)).append(".");
            builder.append(methodName).append("(");
            builder.append(MethodAnalyzer.listParameters(parameters, isStatic, registers)).append(")");
        } else {
            builder.append(registers.getFirst()).append(".");
            builder.append(methodName).append("(");
            builder.append(MethodAnalyzer.listParameters(parameters, isStatic, registers)).append(")");
        }
        return builder.toString();
    }

}
