package com.momoyeyu.smali_analyzer.element.instructions;

import com.momoyeyu.smali_analyzer.analyzers.MethodAnalyzer;
import com.momoyeyu.smali_analyzer.element.SmaliMethod;
import com.momoyeyu.smali_analyzer.utils.Stepper;
import com.momoyeyu.smali_analyzer.utils.TypeUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmaliCallInstruction extends SmaliInstruction {

    private static final Pattern callPattern = Pattern.compile("^invoke-(\\S+)\\s+\\{(.*?)},\\s*(\\S+)->(\\S+)\\((\\S*)\\)(\\S+);?");
    private static final Pattern returnPattern = Pattern.compile("^return-");

    private String callee;
    private String methodName;
    private List<String> arguments;
    private List<String> parameters;
    private String returnType;
    private boolean isStatic;

    public static void main(String[] args) {
        System.out.println(new SmaliCallInstruction("invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;"));
        System.out.println(new SmaliCallInstruction("invoke-static {p2}, Ljava/util/Collections;->sort(Ljava/util/List;)V"));
    }

    // testing
    private SmaliCallInstruction(String instruction) {
        this(instruction, null);
    }

    public SmaliCallInstruction(String instruction, SmaliMethod smaliMethod) {
        super(instruction, smaliMethod);
        isStatic = false;
        this.analyze();
    }

    @Override
    protected void analyze() {
        Matcher matcher = callPattern.matcher(signature);
        if (matcher.matches()) {
            Stepper stepper = new Stepper();
            operation = "invoke-" + matcher.group(stepper.step(1));
            isStatic = matcher.group(stepper.step(0)).equals("static");
            arguments = getRegistersList(matcher.group(stepper.step(1)));
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
            builder.append(MethodAnalyzer.listParameters(parameters, isStatic, arguments)).append(")");
        } else {
            builder.append(arguments.getFirst()).append(".");
            builder.append(methodName).append("(");
            builder.append(MethodAnalyzer.listParameters(parameters, isStatic, arguments)).append(")");
        }
        return builder.toString();
    }

    public String getReturnType() {
        return returnType;
    }

    public static boolean isCallInstruction(String instruction) {
        if (instruction == null) {
            return false;
        }
        return instruction.startsWith("invoke-");
    }

}
