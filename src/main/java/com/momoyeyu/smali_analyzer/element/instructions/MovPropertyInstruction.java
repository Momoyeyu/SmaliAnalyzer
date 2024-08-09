package com.momoyeyu.smali_analyzer.element.instructions;

import com.momoyeyu.smali_analyzer.element.SmaliMethod;
import com.momoyeyu.smali_analyzer.utils.Stepper;
import com.momoyeyu.smali_analyzer.utils.TypeUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MovPropertyInstruction extends Instruction {

    private static final Pattern movPropertyPattern = Pattern.compile("^((i)|(s))((put)|(get))(-(\\S+))?\\s+(.+),\\s*(\\S+)->(\\S+):(\\S+);?");

    private String object;
    private String property;
    private String propertyType;
    private boolean newVar;

    public static void main(String[] args) {
        System.out.println(new MovPropertyInstruction("iput v3, v2, Landroidx/appcompat/widget/ActivityChooserModel$ActivityResolveInfo;->weight:F"));
        System.out.println(new MovPropertyInstruction("iget-object v2, p0, Landroidx/appcompat/widget/ActivityChooserModel;->mIntent:Landroid/content/Intent;"));
        System.out.println();
    }

    // testing
    private MovPropertyInstruction(String instruction) {
        this(instruction, null);
    }

    public MovPropertyInstruction(String instruction, SmaliMethod smaliMethod) {
        super(instruction, smaliMethod);
        newVar = false;
        this.analyze();
    }

    @Override
    public void updateTable() {
        if (!updated) {
            if (operation.startsWith("get", 1)) {
                String register = registers.getFirst();
                if (registerTable.getVariableName(register).equals(register)) {
                    registerTable.storeVariable(register, propertyType);
                    newVar = true;
                }
            }
            super.updateTable();
        }
    }

    @Override
    protected void analyze() {
        Matcher matcher = movPropertyPattern.matcher(signature);
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
    public INSTRUCTION_TYPE getSubType() {
        return INSTRUCTION_TYPE.MOV_PROPERTY;
    }

    @Override
    public INSTRUCTION_TYPE getType() {
        return INSTRUCTION_TYPE.MOV;
    }

    @Override
    public String toString() {
        if (!analyzed) {
            return analysisFail("mov property");
        }
        StringBuilder sb = new StringBuilder();
        if (newVar)
            sb.append(TypeUtils.getNameFromJava(propertyType)).append(" ");
        if (operation.startsWith("put", 1)) {
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

    public static boolean isMovPropertyInstruction(String instruction) {
        if (instruction == null)
            return false;
        return movPropertyPattern.matcher(instruction).matches();
    }

    // getter
    public String getPropertyType() {
        return propertyType;
    }

}
