package com.momoyeyu.smali_analyzer.element.instructions;

import com.momoyeyu.smali_analyzer.element.SmaliMethod;
import com.momoyeyu.smali_analyzer.utils.TypeUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LocalInstruction extends Instruction{
    private static final Pattern pattern = Pattern.compile("^\\.local\\s+(\\S+?),\\s+\"(\\S+)\":(\\S+)");

    private String varName;
    private String oldName;
    private String type;

    public static void main(String[] args) {
        System.out.println(new LocalInstruction(".local v0, \"aIn\":Lcmb/shield/sm/asn1/ASN1InputStream;"));
    }

    private LocalInstruction(String instruction) {
        this(instruction, null);
    }

    public LocalInstruction(String instruction, SmaliMethod parentMethod) {
        super(instruction, parentMethod);
        this.analyze();
    }

    @Override
    protected void analyze() {
        if (!analyzed) {
            Matcher matcher = pattern.matcher(signature);
            if (matcher.matches()) {
                registers = getRegistersList(matcher.group(1));
                oldName = registers.getFirst();
                varName = matcher.group(2);
                type = TypeUtils.getTypeFromSmali(matcher.group(3));
                super.analyze();
            }
        }
    }

    @Override
    public void updateTable() {
        if (!updated) {
            oldName = registerTable.getVariableName(oldName);
            registerTable.storeVariableWithName(registers.getFirst(), type, varName);
            super.updateTable();
        }
    }

    @Override
    public String toString() {
        if (!analyzed)
            return analysisFail("local");
        return TypeUtils.getNameFromJava(type) + " " + registers.getFirst();
    }

    public static boolean isLocalInstruction(String instruction) {
        if (instruction == null)
            return false;
        return pattern.matcher(instruction).matches();
    }
}
