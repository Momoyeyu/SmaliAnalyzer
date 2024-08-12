package com.momoyeyu.smali_analyzer.element.instructions;

import com.momoyeyu.smali_analyzer.element.SmaliMethod;
import com.momoyeyu.smali_analyzer.utils.TypeUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArrayData extends Instruction {
    private static final Pattern pattern = Pattern.compile("^\\.array-data\\s+(\\S+?),(.*),\\.end array-data");

    private String size;
    private List<String> data;
    private String dataType;

    public static void main(String[] args) {
        System.out.println(new ArrayData(".array-data 4,-0x10100a7,-0x101009c,.end array-data"));
    }

    private ArrayData(String instruction) {
        this(instruction, null);
    }

    public ArrayData(String instruction, SmaliMethod parentMethod) {
        super(instruction, parentMethod);
        analyze();
    }

    @Override
    protected void analyze() {
        if (!analyzed) {
            Matcher matcher = pattern.matcher(signature);
            if (matcher.find()) {
                size = matcher.group(1);
                data = getRegistersList(matcher.group(2));
                super.analyze();
            }
        }
    }

    @Override
    public String toString() {
        if (!analyzed)
            return analysisFail("array-data");
        StringBuilder sb = new StringBuilder();
        if (dataType != null) {
            if (!TypeUtils.isJavaBasicType(dataType))
                sb.append("new ").append(TypeUtils.getNameFromJava(dataType)).append("[]");
            // else : none
        } else {
            sb.append("new Object[]");
        }
        return sb.append("{").append(String.join(", ", data)).append("}").toString();
    }

    @Override
    public INSTRUCTION_TYPE getSubType() {
        return INSTRUCTION_TYPE.ARRAT_DATA;
    }

    @Override
    public INSTRUCTION_TYPE getType() {
        return INSTRUCTION_TYPE.ARRAT_DATA;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public static boolean isArrayData(String instruction) {
        if (instruction == null)
            return false;
        return pattern.matcher(instruction).matches();
    }
}
