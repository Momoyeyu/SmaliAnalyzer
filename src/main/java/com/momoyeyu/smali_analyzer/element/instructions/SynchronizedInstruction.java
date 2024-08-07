package com.momoyeyu.smali_analyzer.element.instructions;

import com.momoyeyu.smali_analyzer.element.SmaliMethod;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SynchronizedInstruction extends Instruction {
    private static final Pattern synchronizedPattern = Pattern.compile("^monitor-((enter)|(exit))\\s+(\\S+)");

    public static void main(String[] args) {
        System.out.println(new SynchronizedInstruction("monitor-enter p0"));
        System.out.println(new SynchronizedInstruction("monitor-exit p0"));
    }

    private SynchronizedInstruction(String instruction) {
        this(instruction, null);
    }

    public SynchronizedInstruction(String instruction, SmaliMethod parentMethod) {
        super(instruction, parentMethod);
        this.analyze();
    }

    @Override
    protected void analyze() {
        Matcher matcher = synchronizedPattern.matcher(signature);
        if (matcher.matches()) {
            operation = "monitor-" + matcher.group(1);
            registers = getRegistersList(matcher.group(4));
            super.analyze();
        }
    }

    @Override
    public String toString() {
        if (!analyzed)
            return analysisFail("synchronized");
        if (operation.endsWith("enter"))
            return "synchronized (" + registers.getFirst() + ") {";
        return "} // end synchronized";
    }

    public static boolean isSynchronizedInstruction(String instruction) {
        if (instruction == null)
            return false;
        return synchronizedPattern.matcher(instruction).matches();
    }
}
