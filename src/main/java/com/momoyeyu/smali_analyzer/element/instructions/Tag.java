package com.momoyeyu.smali_analyzer.element.instructions;

import com.momoyeyu.smali_analyzer.element.SmaliMethod;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tag extends Instruction{

    private static final Pattern tagPattern = Pattern.compile(
            "\\.((line)|(registers)|(locals)|(end method))");
    private String tag;

    public Tag(String instruction, SmaliMethod parentMethod) {
        super(instruction, parentMethod);
    }

    @Override
    public String toString() {
        if (!analyzed)
            return analysisFail("tag");
        return "// ." + tag;
    }

    @Override
    protected void analyze() {
        Matcher matcher = tagPattern.matcher(signature);
        if (matcher.matches()) {
            tag = matcher.group(1);
            super.analyze();
        }
    }

    @Override
    public INSTRUCTION_TYPE getType() {
        return INSTRUCTION_TYPE.TAG;
    }

    @Override
    public INSTRUCTION_TYPE getSubType() {
        return INSTRUCTION_TYPE.TAG;
    }

    public static boolean isTag(String instruction) {
        if (instruction == null)
            return false;
        return tagPattern.matcher(instruction).matches();
    }
}
