package com.momoyeyu.smali_analyzer.element.instructions;

import com.momoyeyu.smali_analyzer.element.SmaliMethod;
import com.momoyeyu.smali_analyzer.entity.RegisterTable;
import com.momoyeyu.smali_analyzer.utils.TypeUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CastInstruction extends Instruction {
    private static final Pattern castPattern = Pattern.compile("^check-cast\\s+(\\S+),\\s*(\\S+)");

    private String castType;
    private String oldName;

    public static void main(String[] args) {
        System.out.println(new CastInstruction("check-cast v3, Landroid/content/pm/ResolveInfo"));
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
        Matcher matcher = castPattern.matcher(signature);
        if (matcher.matches()) {
            registers = getRegistersList(matcher.group(1));
            castType = TypeUtils.getTypeFromSmali(matcher.group(2));
            oldName = registers.getFirst();
            super.analyze();
        }
    }

    @Override
    public void updateTable() {
        if (!updated) {
            RegisterTable table = parentMethod.getRegisterTable();
            oldName = table.getVariableName(registers.getFirst());
            table.storeVariable(registers.getFirst(), castType);
        }
        super.updateTable();
    }

    @Override
    public String toString() {
        if (!analyzed)
            return analysisFail("cast");
        String typeName = TypeUtils.getNameFromJava(castType);
        return typeName + " " + registers.getFirst() + " = (" + typeName + ") " + oldName;
    }

    @Override
    public INSTRUCTION_TYPE getType() {
        return INSTRUCTION_TYPE.CAST;
    }

    @Override
    public INSTRUCTION_TYPE getSubType() {
        return INSTRUCTION_TYPE.CAST;
    }

    public static boolean isCastInstruction(String instruction) {
        if (instruction == null)
            return false;
        return castPattern.matcher(instruction).matches();
    }
}
