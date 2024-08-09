package com.momoyeyu.smali_analyzer.element.instructions;

import com.momoyeyu.smali_analyzer.element.SmaliMethod;
import com.momoyeyu.smali_analyzer.entity.RegisterTable;
import com.momoyeyu.smali_analyzer.utils.TypeUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CastInstruction extends Instruction {
    private static final Pattern checkCastPattern = Pattern.compile("^check-cast\\s+(\\S+),\\s*(\\S+)");
    private static final Pattern basicCastPattern = Pattern.compile("^(\\S+?)-to-(\\S+)\\s+(.+)");
    private String oldType;
    private String newType;
    private String oldName;

    public static void main(String[] args) {
        System.out.println(new CastInstruction("check-cast v3, Landroid/content/pm/ResolveInfo"));
        System.out.println(new CastInstruction("float-to-double v2, v2"));
    }

    private CastInstruction(String instruction) {
        this(instruction, null);
    }

    public CastInstruction(String instruction, SmaliMethod parentMethod) {
        super(instruction, parentMethod);
        this.analyze();
    }

    @Override
    protected void analyze() {
        Matcher matcher = checkCastPattern.matcher(signature);
        if (matcher.matches()) {
            registers = getRegistersList(matcher.group(1));
            newType = TypeUtils.getTypeFromSmali(matcher.group(2));
            oldName = registers.getFirst();
            oldType = null;
        }
        matcher = basicCastPattern.matcher(signature);
        if (matcher.matches()) {
            oldType = matcher.group(1);
            newType = matcher.group(2);
            registers = getRegistersList(matcher.group(3));
            oldName = registers.get(1);
        }
        super.analyze();
    }

    @Override
    public void updateTable() {
        RegisterTable table = parentMethod.getRegisterTable();
        if (!updated) {
            oldName = table.getVariableName(oldName);
            table.storeVariable(registers.getFirst(), newType);
            super.updateTable();
        }
    }

    @Override
    public String toString() {
        if (!analyzed)
            return analysisFail("cast");
        String typeName = TypeUtils.getNameFromJava(newType);
        return typeName + " " + registers.getFirst() + " = (" + typeName + ") " + oldName;
    }

    @Override
    public INSTRUCTION_TYPE getType() {
        return INSTRUCTION_TYPE.CAST;
    }

    @Override
    public INSTRUCTION_TYPE getSubType() {
        if (checkCastPattern.matcher(signature).matches())
            return INSTRUCTION_TYPE.CHECK_CAST;
        if (basicCastPattern.matcher(signature).matches())
            return INSTRUCTION_TYPE.BASIC_CAST;
        return INSTRUCTION_TYPE.CAST;
    }

    public static boolean isCastInstruction(String instruction) {
        if (instruction == null)
            return false;
        return checkCastPattern.matcher(instruction).matches() || basicCastPattern.matcher(instruction).matches();
    }
}
