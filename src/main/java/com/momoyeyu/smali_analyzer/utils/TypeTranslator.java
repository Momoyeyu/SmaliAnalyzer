package com.momoyeyu.smali_analyzer.utils;

import java.util.*;

public class TypeTranslator {
    /**
     * Test
     * @param args user input
     */
    public static void main(String[] args) {
        System.out.println(getObjectPackage("Landroidx/appcompat/widget/ActivityChooserModel"));
        System.out.println(getType("Landroidx/appcompat/widget/ActivityChooserModel"));
        System.out.println(getType("Z"));
        System.out.println(getType("[Ljava/lang/String"));
        System.out.println(getType("[B"));
        System.out.println(getObjectName("java.lang.String[]"));
        List<String> strings = splitParameters("Landroid/content/ComponentName;J[FB[JZD");
        for (String string : strings) {
            System.out.print(string + ",");
        }
    }

    private static final Map<String, String> basicTypeMap = new HashMap<>();
    static {
        basicTypeMap.put("Z", "boolean");
        basicTypeMap.put("B", "byte");
        basicTypeMap.put("C", "char");
        basicTypeMap.put("D", "double");
        basicTypeMap.put("F", "float");
        basicTypeMap.put("I", "int");
        basicTypeMap.put("J", "long");
        basicTypeMap.put("S", "short");
        basicTypeMap.put("V", "void");
    }

    /**
     * Turn smali datatype into corresponding java datatype.
     *
     * @param smaliType the datatype of a smali variable, including basic type and object
     * @return the java type of the input (return only the classname of an object)
     */
    public static String getType(String smaliType) {
        boolean isArray = false;
        if (smaliType == null || smaliType.isEmpty()) {
            return null;
        }
        if (smaliType.startsWith("[")) {
            isArray = true;
            smaliType = smaliType.substring(1);
        }
        String type;
        if (smaliType.startsWith("L")) {
            type = getObjectType(smaliType);
            return isArray ? type + "[]" : type;
        } else if (isBasicType(smaliType)){
            type = basicTypeMap.get(smaliType);
            return isArray ? type + "[]" : type;
        }
        System.out.println("[WARN] Unknown smali type: " + smaliType);
        return smaliType;
    }

    /**
     * Extract Java object's classname from its Type
     * @param objectType Java Object
     * @return the classname of the object
     * @throws RuntimeException let the Logger catch and log invalid object
     */
    public static String getObjectName(String objectType) throws IllegalArgumentException {
        if (objectType == null || objectType.isBlank()) {
            throw new RuntimeException("[ERROR] Invalid type: " + objectType);
        }
        if (isBasicType(objectType)) {
            Logger.log("[WARN] " + objectType + " is a basic type, shouldn't call getObjectName()");
            return objectType;
        }
        if (objectType.endsWith("...")) { // deal with varargs type
            String tmp = objectType.substring(0, objectType.length() - 3);
            return objectType.substring(tmp.lastIndexOf('.') + 1);
        }
        return objectType.substring(objectType.lastIndexOf(".") + 1);
    }

    /**
     * Turn smali object type into Java type
     * @param smaliObject a String of smali object (like "Ljava/lang/String;")
     * @return the corresponding Java object type of input smali object
     */
    private static String getObjectType(String smaliObject) {
        if (smaliObject == null) {
            return "Object";
        }
        if (!smaliObject.startsWith("L")) {
            throw new RuntimeException("[ERROR] Invalid Object: " + smaliObject);
        }
        smaliObject = smaliObject.substring(1);
        smaliObject = smaliObject.replaceAll("[/$]", ".");
        if (smaliObject.endsWith(";")) {
            return smaliObject.substring(0, smaliObject.length() - 1);
        }
        return smaliObject;
    }

    /**
     * Return package the smali object belong to.
     * <pre>
     *     getObjectPackage("java.lang.String");
     *     return "java.lang"
     * </pre>
     * @param objectType a smali object with package and name
     * @return package of the object
     */
    public static String getObjectPackage(String objectType) {
        if (objectType == null || objectType.isBlank()) {
            throw new RuntimeException("[ERROR] Invalid type: " + objectType);
        }
        if (objectType.startsWith("L")) {
            objectType = getObjectType(objectType);
        }
        if (isBasicType(objectType)) {
            Logger.log("[WARN] " + objectType + " is a basic type, shouldn't call getObjectName()");
            return objectType;
        }
        return objectType.substring(0, objectType.lastIndexOf("."));
    }

    /**
     * Turn a line of smali parameters into a list of java parameters
     *
     * @test pass
     * @param parameters a line of smali parameter list string
     * @return a list of java type parameters
     */
    public static List<String> getJavaParameters(String parameters) {
        List<String> parametersList = new ArrayList<>();
        if (parameters == null || parameters.isEmpty()) {
            return parametersList;
        }
        List<String> params = splitParameters(parameters);
        for (String parameter : params) {
            parametersList.add(getType(parameter.trim()));
        }
        return parametersList;
    }

    /**
     * Return weather a Java type is a basic Java type.
     * @param type it should be a type that get from the getType() method
     * @return boolean, weather the input type is a Java basic type
     */
    public static boolean isBasicType(String type) {
        return basicTypeMap.get(type) != null;
    }

    /**
     * A state machine that split parameters
     * @param parameters
     * @return
     */
    public static List<String> splitParameters(String parameters) {
        List<String> parametersList = new LinkedList<>();
        int state = 0;
        int objIdx;
        for (int i = 0; i < parameters.length(); i++) {
            char c = parameters.charAt(i);
            switch (state) {
                case 0: { // scan a new parameter
                    if (Character.isSpaceChar(c)) {
                        continue;
                    }
                    if (c == '[') { // is an array
                        state = 1;
                    } else if (c == 'L') { // is an object
                        objIdx = i;
                        while(parameters.charAt(i) != ';') {
                            i += 1;
                        }
                        parametersList.add(parameters.substring(objIdx, i));
                        state = 0;
                    } else { // is a basic type
                        parametersList.add(String.valueOf(c));
                        // state = 0;
                    }
                    break;
                }
                case 1: { // is an array
                    if (c == 'L') { // is an array of object
                        objIdx = i;
                        while(parameters.charAt(i) != ';') {
                            i += 1;
                        }
                        parametersList.add("[" + parameters.substring(objIdx, i));
                        state = 0;
                    } else { // is an array of basic type
                        parametersList.add("[" + String.valueOf(c));
                        state = 0;
                    }
                    break;
                }
            }
        }
        return parametersList;
    }

}
