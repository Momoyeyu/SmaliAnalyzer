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
        RegisterMap map = new RegisterMap(null);
        System.out.println(map.nameGenerator("SmaliMethod"));
        System.out.println(map.nameGenerator("java.lang.Class"));
        System.out.println(map.nameGenerator("smaliMethod"));
        System.out.println(map.nameGenerator("List<List<String>>"));
        System.out.println(map.nameGenerator("java.lang.Class[]"));
        System.out.println(map.nameGenerator("java.lang.String[]"));
    }

    public RegisterMap(SmaliMethod smaliMethod) {
        parentMethod = smaliMethod;
    }

    @Override
    public void storeParams() {
        if (parentMethod != null) {
            if (!parentMethod.isStaticModifier())
                storeVariableWithName("p0", parentMethod.getOwnerClassType(), "this");
            List<String> params = parentMethod.getParametersList();
            int i = parentMethod.isStaticModifier() ? 0 : 1;
            for (String param : params) {
                storeVariableWithName("p" + i, param, "arg" + i);
                i++;
            }
        }
    }

    /**
     * @param register the register for the new variable
     * @param type     the datatype of new variable
     */
    @Override
    public void storeVariable(String register, String type) {
        String name = nameGenerator(type);
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
     * @param register register name
     * @return the datatype of the variable inside the register
     */
    @Override
    public String getVariableType(String register) {
        if (!reigisterMap.containsKey(register)) {
            return "Object";
        }
        return variableMap.get(reigisterMap.get(register)).getType();
    }

    /**
     * Return a name that don't conflict with other name.
     * @param type datatype
     * @return a name that haven't been used in the namespace.
     */
    private String nameGenerator(String type) {
        if (type == null || type.isEmpty()) {
            type = "Object";
        }
        if (!variableIndexMap.containsKey(type)) {
            variableIndexMap.put(type, 1);
        }
        int index = variableIndexMap.get(type);
        String stdName = stdNameGenerator(type);
        while (JAVA_KEYWORDS.contains(stdName) || stdName.equals(type))
            stdName = "_" + stdName;
        if (!variableMap.containsKey(stdName))
            return stdName;
        String name = stdName + "_" + index++;
        while (variableMap.containsKey(name)) {
            name = stdName + "_" + index++;
        }
        variableIndexMap.put(type, index);
        return name;
    }

    public static String stdNameGenerator(String type) throws IllegalArgumentException {
        if (type == null || type.isEmpty()) {
            throw new IllegalArgumentException("type is null or empty");
        }
        if (type.endsWith("[]")) {
            String tmp = stdNameGenerator(type.substring(0, type.length() - 2));
            if (tmp.endsWith("s"))
                return tmp + "es";
            return tmp + "s";
        }
        Matcher matcher = typePattern.matcher(type);
        if (matcher.find()) {
            return stdNameGenerator(matcher.group(2)) + matcher.group(1);
        }
        String name = TypeUtils.getNameFromJava(type);
        name = name.substring(0, 1).toLowerCase() + name.substring(1);
        return name;
    }
}
