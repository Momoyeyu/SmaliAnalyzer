package com.momoyeyu.smali_analyzer.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MethodStack {
    private static final Pattern registerPattern = Pattern.compile("(\\S+?)(\\.(\\S+))?");

    private final Map<String, Variable> table = new HashMap<>();

    public MethodStack() {}

    public void storeVariable(String register, String property, String value, String type) {
        assert register != null;
        String trueValue = getTrueValue(value);
        if (!inStack(register)) {
            table.put(register, new Variable(type, trueValue));
            return;
        }
        if (property != null) {
            Variable var = table.get(register);
            var.setProperty(property, trueValue);
        }
    }

    public void storeVariable(String domain, String value, String type) {
        Matcher matcher = registerPattern.matcher(domain);
        if (matcher.matches()) {
            storeVariable(matcher.group(1), matcher.group(3), value, type);
        }
    }

    private String getTrueValue(String value) {
        while (table.containsKey(value)) {
            String trueValue = getValue(value);
            if (trueValue.equals(value)) {
                break;
            }
            value = trueValue;
        }
        return value;
    }

    public String getValue(String domain) {
        Matcher matcher = registerPattern.matcher(domain);
        if (matcher.matches()) {
            Variable variable = table.get(matcher.group(1));
            if (variable != null) {
                String property = matcher.group(3);
                if (property != null) {
                    return variable.getProperty(property);
                }
                return variable.getValue();
            }
        }
        return null;
    }

    private boolean inStack(String register) {
        return table.containsKey(register);
    }

//    public boolean inStack(String domain) {
//        Matcher matcher = registerPattern.matcher(domain);
//        if (matcher.matches()) {
//            String register = matcher.group(1);
//            String property = matcher.group(3);
//            Variable variable = table.get(register);
//            if (variable != null) {
//                if (property != null) {
//                    return variable.getProperty(property) != null;
//                } // property == null:
//                return true;
//            }
//        }
//        return false;
//    }
//    private boolean safeStoreVariable(String instruction) {
//        Matcher matcher = constPattern.matcher(instruction);
//        if (matcher.find()) {
//            String register = matcher.group(2);
//            if (matcher.group(1).equals("string")) {
//                table.put(register, new Variable("java.lang.String", matcher.group(3)));
//            } else if (matcher.group(1).equals("class")){
//                table.put(register, new Variable(TypeUtils.getTypeFromSmali(matcher.group(7)), null));
//            } else {
//                table.put(register, new Variable(matcher.group(2), matcher.group(7)));
//            }
//            return true;
//        }
//        return false;
//    }
//
//    public void storeVariable(String instruction) throws IllegalArgumentException {
//        boolean flag = safeStoreVariable(instruction);
//        if (!flag) {
//            throw new IllegalArgumentException("Unknown instruction: " + instruction);
//        }
//    }
}
