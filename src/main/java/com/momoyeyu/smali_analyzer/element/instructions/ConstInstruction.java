package com.momoyeyu.smali_analyzer.element.instructions;

import com.momoyeyu.smali_analyzer.element.SmaliMethod;
import com.momoyeyu.smali_analyzer.utils.Stepper;
import com.momoyeyu.smali_analyzer.utils.TypeUtils;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConstInstruction extends Instruction {

    private static final Pattern constPattern = Pattern.compile("^const(-(\\S+?))?(/(\\S+))?\\s+(\\S+),\\s+(.+)");

    private String constType;
    private String constSize;
    private String value;

    public static void main(String[] args) {
        String[] strings = {
            "const-string v0, \"historical-record\"",
            "const-string v2, \"Error writing historical record file: \"",
            "const/4 v3, 0x0",
            "const/16 v0, 0x32",
            "const-string/jumbo v11, \"weight\"",
            "const-class v0, Landroidx/appcompat/widget/ActivityChooserModel;",
        };
        for (String string : strings) {
            System.out.println(new ConstInstruction(string));
        }
    }

    // test
    private ConstInstruction(String instruction) {
        this(instruction, null);
    }

    public ConstInstruction(String instruction, SmaliMethod parentMethod) {
        super(instruction, parentMethod);
        this.analyze();
    }

    @Override
    protected void analyze() {
        Matcher matcher = constPattern.matcher(signature);
        if (matcher.find()) {
            Stepper stp = new Stepper();
            constType = Objects.requireNonNullElse(matcher.group(stp.step(2)), "");
            constSize = matcher.group(stp.step(2));
            registers = getRegistersList(matcher.group(stp.step(1)));
            value = matcher.group(stp.step(1));
            if (constType != null && constType.equals("class")) {
                value = TypeUtils.getTypeFromSmali(value);
            }
            super.analyze();
        }
    }

    @Override
    public void updateTable() {
        if (parentMethod != null)
            parentMethod.registerTable.storeVariable(registers.getFirst(), null, value, constType);
    }

    @Override
    public INSTRUCTION_TYPE getTrueTYPE() {
        return switch (constType) {
            case "string" -> INSTRUCTION_TYPE.CONST_STRING;
            case "class" -> INSTRUCTION_TYPE.CONST_CLASS;
            default -> INSTRUCTION_TYPE.CONST;
        };
    }

    @Override
    public INSTRUCTION_TYPE getTYPE() {
        return INSTRUCTION_TYPE.CONST;
    }

    @Override
    public String toString() {
        if (!analyzed)
            return analysisFail("const");
        StringBuilder sb = new StringBuilder();
        sb.append(switch (constType) {
            default -> "";
            case "class" -> "final Class<?> ";
            case "string" -> "final String ";
        });
        sb.append(registers.getFirst()).append(" = ");
        if (constType != null && constType.equals("class")) {
            sb.append(TypeUtils.getNameFromJava(value)).append(".class");
        } else {
            sb.append(value);
        }
        return sb.toString();
    }

    public static boolean isConstInstruction(String instruction) {
        if (instruction == null) {
            return false;
        }
        return constPattern.matcher(instruction).matches();
    }
}
