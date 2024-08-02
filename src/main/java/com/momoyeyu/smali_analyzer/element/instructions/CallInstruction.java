package com.momoyeyu.smali_analyzer.element.instructions;

import com.momoyeyu.smali_analyzer.analyzers.MethodAnalyzer;
import com.momoyeyu.smali_analyzer.element.SmaliMethod;
import com.momoyeyu.smali_analyzer.utils.Stepper;
import com.momoyeyu.smali_analyzer.utils.TypeUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CallInstruction extends Instruction {

    private static final Pattern callPattern = Pattern.compile("^invoke-(\\S+)\\s+\\{(.*?)},\\s*(\\S+)->(\\S+)\\((\\S*)\\)(\\S+);?");
    private static final Pattern returnPattern = Pattern.compile("^return-");

    private String callee;
    private String methodName;
    private List<String> parameters;
    private String returnType;
    private boolean isStatic;

    public static void main(String[] args) {
        System.out.println(new CallInstruction("invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;"));
        System.out.println(new CallInstruction("invoke-static {p2}, Ljava/util/Collections;->sort(Ljava/util/List;)V"));
        System.out.println(new CallInstruction("invoke-direct {p0, p1, p2, p3, p4}, Landroidx/appcompat/widget/ActivityChooserModel$HistoricalRecord;-><init>(Landroid/content/ComponentName;JF)V"));
    }

    // testing
    private CallInstruction(String instruction) {
        this(instruction, null);
    }

    public CallInstruction(String instruction, SmaliMethod smaliMethod) {
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
            registers = getRegistersList(matcher.group(stepper.step(1)));
            callee = TypeUtils.getTypeFromSmali(matcher.group(stepper.step(1)));
            methodName = matcher.group(stepper.step(1));
            parameters = TypeUtils.getJavaParametersFromSmali(matcher.group(stepper.step(1)));
            returnType = TypeUtils.getTypeFromSmali(matcher.group(stepper.step(1)));
            super.analyze();
        }
    }

    @Override
    public String toString() {
        if (!analyzed) {
            return analysisFail("call");
        }
        StringBuilder sb = new StringBuilder();
        if (operation.equals("invoke-direct")) {
            sb.append("new ").append(TypeUtils.getNameFromJava(callee));
        } else {
            if (isStatic) {
                sb.append(TypeUtils.getNameFromJava(callee));
            } else {
                sb.append(registers.getFirst());
            }
            sb.append(".").append(methodName);
        }
        sb.append("(").append(MethodAnalyzer.listArguments(registers, isStatic)).append(")");
        return sb.toString();
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
