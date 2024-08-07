package com.momoyeyu.smali_analyzer.entity;

import com.momoyeyu.smali_analyzer.element.SmaliMethod;
import com.momoyeyu.smali_analyzer.utils.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterMap implements RegisterTable {
    private static final Pattern registerPattern = Pattern.compile("([A-Za-z_]([0-9A-Za-z_]+?)?)(\\.(\\S+))?");
    private static final Pattern instructionPattern = Pattern.compile("^(\\S+)\\s+(\\S+)\\s*=\\s*(\\S+)");

    private final Map<String, Variable> variableMap = new HashMap<>();
    private final Map<String, String> registerTable = new HashMap<>();
    private final SmaliMethod parentMathod;

    public RegisterMap(SmaliMethod smaliMethod) {
        parentMathod = smaliMethod;
    }

    @Override
    public void storeVariable(String register, String name, Variable var) {
        registerTable.put(register, name);
        variableMap.put(name, var);
    }

    @Override
    public void storeVariable(String instruction) {

    }

    @Override
    public Variable getVariable(String register) {
        return null;
    }

    public String getValue(String domain) throws IllegalArgumentException{
        Matcher matcher = registerPattern.matcher(domain);
        if (matcher.matches()) {
            Variable variable = variableMap.get(matcher.group(1));
            try {
                return Objects.requireNonNullElse(getValue(variable, matcher.group(3)), domain);
            } catch (NullPointerException e) {
                Logger.log("[WARN] access wild register domain: " + domain);
                Logger.logMulti(e.getStackTrace());
                return domain;
            }
        }
        Logger.log("[ERROR] unknown domain: " + domain);
        return domain;
    }

    @Override
    public void updateVariable(String domain, String value) {

    }

    public void storeVariable(String register, String property, String value, String type) {
        assert register != null;
        String trueValue = getTrueValue(value);
        if (!inStack(register)) {
            variableMap.put(register, new Variable(type, trueValue));
            return;
        }
        if (property != null) {
            Variable var = variableMap.get(register);
            var.setProperty(property, new Variable(type, trueValue));
        }
    }

    public void storeVariable(String domain, String value, String type) {
        Matcher matcher = registerPattern.matcher(domain);
        if (matcher.matches()) {
            storeVariable(matcher.group(1), matcher.group(3), value, type);
        }
    }

    private String getTrueValue(String value) {
        while (variableMap.containsKey(value)) {
            String trueValue = getValue(value);
            if (trueValue.equals(value)) {
                break;
            }
            value = trueValue;
        }
        return value;
    }

    private String getValue(Variable variable, String domain) throws NullPointerException {
        if (variable == null) {
            throw new NullPointerException();
        }
        if (domain == null) {
            return variable.getValue();
        }
        Matcher matcher = registerPattern.matcher(domain);
        if (matcher.matches()) {
            return getValue(variable.getProperty(matcher.group(1)), matcher.group(3));
        }
        return null;
    }

    private boolean inStack(String register) {
        return variableMap.containsKey(register);
    }
}
