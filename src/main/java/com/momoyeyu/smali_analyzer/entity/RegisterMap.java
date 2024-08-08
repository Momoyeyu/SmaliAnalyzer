package com.momoyeyu.smali_analyzer.entity;

import com.momoyeyu.smali_analyzer.element.SmaliMethod;
import com.momoyeyu.smali_analyzer.utils.TypeUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterMap implements RegisterTable {
    private static final Pattern registerPattern = Pattern.compile("([A-Za-z_]([0-9A-Za-z_]+?)?)(\\.(\\S+))?");
    private static final Pattern typePattern = Pattern.compile("^(\\S+?)<(\\S+)>$");

    private Map<String, String> reigisterMap = new HashMap<>();
    private Map<String, Variable> variableMap = new HashMap<>();
    private Map<String, Integer> variableIndexMap = new HashMap<>();
    private SmaliMethod parentMethod;

    private static final Set<String> JAVA_KEYWORDS = new HashSet<>();
    static {
        String[] keywords = new String[]{
                "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class",
                "const", "continue", "default", "do", "double", "else", "enum", "extends", "final",
                "finally", "float", "for", "goto", "if", "implements", "import", "instanceof", "int",
                "interface", "long", "native", "new", "package", "private", "protected", "public",
                "return", "short", "static", "strictfp", "super", "switch", "synchronized", "this",
                "throw", "throws", "transient", "try", "void", "volatile", "while"
        };
        for (String keyword : keywords) {
            JAVA_KEYWORDS.add(keyword);
        }
    }

    public static void main(String[] args) {
        System.out.println(getStdName("SmaliMethod"));
        System.out.println(getStdName("List<List<String>>"));
        System.out.println(getStdName("java.lang.String[]"));
        RegisterMap map = new RegisterMap(null);
        System.out.println(map.getNewName("SmaliMethod"));
        System.out.println(map.getNewName("SmaliMethod"));
    }

    public RegisterMap(SmaliMethod smaliMethod) {
        parentMethod = smaliMethod;
        if (parentMethod != null) {
            List<String> params = parentMethod.getParametersList();
            for (int i = 0; i < params.size(); i++) {
                String param = params.get(i);
                String name;
                if ( i == 0 && !parentMethod.isStaticModifier())
                    name = "this";
                else
                    name = "arg" + i;
                storeVariableWithName("p" + i, param, name);
            }
        }
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

    private void storeVariableWithName(String register, String type, String name) {
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
        if (type == null || type.isEmpty()) {
            type = "Var";
        }
        if (!variableIndexMap.containsKey(type)) {
            variableIndexMap.put(type, 0);
        }
        int index = variableIndexMap.get(type);
        String stdName = getStdName(type);
        String name = stdName + "_" + index++;
        while (variableMap.containsKey(name)) {
            name = stdName + "_" + index++;
        }
        variableIndexMap.put(type, index);
        if (name.endsWith("_0"))
            name = name.substring(0, name.length() - 2);
        while (variableMap.containsKey(name) || JAVA_KEYWORDS.contains(name))
            name = name + "_0";
        return name;
    }

    private static String getStdName(String type) throws IllegalArgumentException {
        if (type == null || type.isEmpty()) {
            throw new IllegalArgumentException("type is null or empty");
        }
        if (type.endsWith("[]")) {
            String tmp = getStdName(type.substring(0, type.length() - 2));
            if (tmp.endsWith("s"))
                return tmp + "es";
            return tmp + "s";
        }
        Matcher matcher = typePattern.matcher(type);
        if (matcher.find()) {
            return getStdName(matcher.group(2)) + matcher.group(1);
        }
        String name = TypeUtils.getNameFromJava(type);
        return name.substring(0, 1).toLowerCase() + name.substring(1);
    }
}