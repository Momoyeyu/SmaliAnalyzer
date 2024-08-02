package com.momoyeyu.smali_analyzer.entity;

import com.momoyeyu.smali_analyzer.utils.TypeUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MethodStack {
    private static final Pattern variablePattern = Pattern.compile("\\.((locals)|(registers))\\s+(\\d+)\\s*");
    private static final Pattern constPattern = Pattern.compile("const-(\\S+)\\s+(\\S+),\\s+(\\S+)\\s*");

    private final Map<String, Variable> table = new HashMap<>();

    public MethodStack() {

    }

    private boolean safeStoreVariable(String instruction) {
        Matcher matcher = constPattern.matcher(instruction);
        if (matcher.find()) {
            String register = matcher.group(2);
            if (matcher.group(1).equals("string")) {
                table.put(register, new Variable("java.lang.String", matcher.group(3)));
            } else if (matcher.group(1).equals("class")){
                table.put(register, new Variable(TypeUtils.getTypeFromSmali(matcher.group(7)), null));
            } else {
                table.put(register, new Variable(matcher.group(2), matcher.group(7)));
            }
            return true;
        }
        return false;
    }

    public void storeVariable(String instruction) throws IllegalArgumentException {
        boolean flag = safeStoreVariable(instruction);
        if (!flag) {
            throw new IllegalArgumentException("Unknown instruction: " + instruction);
        }
    }

    public Variable getVariable(String register) {
        return table.get(register);
    }

    public String getValue(String register) {
        return table.get(register).getValue();
    }


}
