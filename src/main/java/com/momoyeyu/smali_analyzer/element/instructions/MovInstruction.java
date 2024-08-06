package com.momoyeyu.smali_analyzer.element.instructions;

import com.momoyeyu.smali_analyzer.element.SmaliMethod;
import com.momoyeyu.smali_analyzer.utils.Stepper;
import com.momoyeyu.smali_analyzer.utils.TypeUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MovInstruction extends Instruction {

    private static final Pattern movPattern = Pattern.compile("^((i)|(s))((put)|(get))(-(\\S+))?\\s+(.+),\\s*(\\S+)->(\\S+):(\\S+);?");

    private String object;
    private String property;
    private String propertyType;

    public static void main(String[] args) {
        System.out.println(new MovInstruction("iput v3, v2, Landroidx/appcompat/widget/ActivityChooserModel$ActivityResolveInfo;->weight:F"));
        System.out.println(new MovInstruction("iget-object v2, p0, Landroidx/appcompat/widget/ActivityChooserModel;->mIntent:Landroid/content/Intent;"));
        System.out.println();
    }

    // testing
    private MovInstruction(String instruction) {
        this(instruction, null);
    }

    public MovInstruction(String instruction, SmaliMethod smaliMethod) {
        super(instruction, smaliMethod);
        this.analyze();
    }

    @Override
    protected void analyze() {
        Matcher matcher = movPattern.matcher(signature);
        if (matcher.find()) {
            Stepper stp = new Stepper();
            operation = matcher.group(stp.step(1)) + matcher.group(stp.step(3));
            operation += matcher.group(stp.step(3)) == null ? "" : matcher.group(stp.step(0));
            registers = getRegistersList(matcher.group(stp.step(2)));
            object = TypeUtils.getTypeFromSmali(matcher.group(stp.step(1)));
            property = matcher.group(stp.step(1));
            propertyType = TypeUtils.getTypeFromSmali(matcher.group(stp.step(1)));
            super.analyze();
        }
    }

    @Override
    public void updateTable() {
        if (operation.substring(1).equals("put") && parentMethod != null) {
            parentMethod.registerTable.storeVariable(registers.getLast(), property, registers.getFirst(), "data");
        }
    }

    @Override
    public INSTRUCTION_TYPE getSubType() {
        return INSTRUCTION_TYPE.MOV;
    }

    @Override
    public INSTRUCTION_TYPE getType() {
        return INSTRUCTION_TYPE.MOV;
    }

    @Override
    public String toString() {
        if (!analyzed) {
            return analysisFail("mov");
        }
        StringBuilder sb = new StringBuilder();
        if (operation.substring(1).startsWith("put")) {
            if (operation.startsWith("i")) {
                sb.append(registers.getLast());
            } else {
                sb.append(TypeUtils.getNameFromJava(object));
            }
            sb.append(".").append(property).append(" = ").append(registers.getFirst());
        } else { // get
            sb.append(registers.getFirst()).append(" = ");
            if (operation.startsWith("i")) {
                sb.append(registers.getLast());
            } else {
                sb.append(TypeUtils.getNameFromJava(object));
            }
            sb.append(".").append(property);
        }
        return sb.toString();
    }

    public static boolean isMovInstruction(String instruction) {
        if (instruction == null)
            return false;
        return movPattern.matcher(instruction).matches();
    }

    // getter
    public String getPropertyType() {
        return propertyType;
    }

}
