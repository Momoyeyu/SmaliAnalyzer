package com.momoyeyu.smali_analyzer.entity;

import com.momoyeyu.smali_analyzer.element.SmaliMethod;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class RegisterMap implements RegisterTable {
    private static final Pattern registerPattern = Pattern.compile("([A-Za-z_]([0-9A-Za-z_]+?)?)(\\.(\\S+))?");

    private Map<String, String> reigisterMap = new HashMap<>();
    private Map<String, Variable> variableMap = new HashMap<>();
    private Map<String, Integer> variableIndexMap = new HashMap<>();
    private SmaliMethod parentMethod;

    public RegisterMap(SmaliMethod smaliMethod) {
        parentMethod = smaliMethod;
    }

    /**
     * @param register the register for the new variable
     * @param type     the datatype of new variable
     */
    @Override
    public void storeVariable(String register, String type) {
        String name = getNewName(type);
        reigisterMap.put(register, name);
        variableMap.put(name, new Variable(type));
    }

    /**
     * @param register register name
     * @return the name of the variable inside the register
     */
    @Override
    public String getVariableName(String register) {
        if (!reigisterMap.containsKey(register)) {
            return register;
        }
        return reigisterMap.get(register);
    }

    /**
     * Return a name that don't conflict with other name.
     * @param type datatype
     * @return a name that haven't been used in the namespace.
     */
    private String getNewName(String type) {
        if (!variableIndexMap.containsKey(type)) {
            variableIndexMap.put(type, 0);
        }
        int index = variableIndexMap.get(type);
        String name = type.toLowerCase() + "_" + index++;
        while (variableMap.containsKey(name)) {
            name = type.toLowerCase() + "_" + index++;
        }
        variableIndexMap.put(type, index);
        return name;
    }
}
