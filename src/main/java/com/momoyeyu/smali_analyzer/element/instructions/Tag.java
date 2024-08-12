package com.momoyeyu.smali_analyzer.element.instructions;

import com.momoyeyu.smali_analyzer.element.SmaliMethod;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tag extends Instruction{

    private static final Pattern tagPattern = Pattern.compile(
            "^\\.(line|registers|locals|end method|param|end param|end local).*");
    private String tag;

    public static void main(String[] args) {
        System.out.println(new Tag(".end method"));
    }

    private Tag(String instruction) {
        this(instruction, null);
    }

    public Tag(String instruction, SmaliMethod parentMethod) {
        super(instruction, parentMethod);
        this.analyze();
    }

    @Override
    public String toString() {
        if (!analyzed)
            return analysisFail("tag");
        return "// " + signature;
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
        if (tag.equals("end method"))
            return INSTRUCTION_TYPE.TAG_END_METHOD;
        return INSTRUCTION_TYPE.TAG;
    }

    public static boolean isTag(String instruction) {
        if (instruction == null)
            return false;
        return tagPattern.matcher(instruction).matches();
    }
}
