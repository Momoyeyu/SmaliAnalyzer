package com.momoyeyu.smali_analyzer.utils;

import java.util.*;

public class TypeTranslator {

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
     * Test
     * @param args user input
     */
    public static void main(String[] args) {
        System.out.println(getObjectPackage("Landroidx/appcompat/widget/ActivityChooserModel"));
        System.out.println(getRoutes("Landroidx/appcompat/widget/ActivityChooserModel"));
        System.out.println(getRoutes("Z"));
        System.out.println(getRoutes("[Ljava/lang/String"));
        System.out.println(getRoutes("[B"));
        System.out.println(getJavaObjectName("java.lang.String[]"));
        List<String> strings = splitParameters("Landroid/content/ComponentName;J[FB[JZD");
        for (String string : strings) {
            System.out.print(string + ",");
        }
    }

    /**
     * Translate smali type into Java type
     * @param smaliType smali type
     * @return corresponding Java type
     */
    public static String getType(String smaliType) {
        String type = getRoutes(smaliType);
        if (isBasicType(type)) {
            return type;
        }
        try {
            return getJavaObjectName(type);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            Logger.logAnalysisFailure("type", smaliType);
            return "/* " + smaliType + " */";
        }
    }

    /**
     * Turn smali datatype into corresponding java datatype.
     *
     * @param smaliType the datatype of a smali variable, including basic type and object
     * @return the java type of the input (return only the classname of an object)
     */
    public static String getRoutes(String smaliType) {
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
            type = getSmaliObjectType(smaliType);
            return isArray ? type + "[]" : type;
        } else if (isBasicType(smaliType)){
            type = basicTypeMap.get(smaliType);
            return isArray ? type + "[]" : type;
        }
        System.out.println("[WARN] Unknown smali type: " + smaliType);
        return smaliType;
    }

    /**
     * Extract Java object's classname from its routes
     * @param javaObjectRoutes Java object routes (like "java.lang.String")
     * @return the classname of the object
     * @throws RuntimeException let the Logger catch and log invalid object
     */
    public static String getJavaObjectName(String javaObjectRoutes) throws IllegalArgumentException {
        if (javaObjectRoutes == null || javaObjectRoutes.isBlank()) {
            throw new RuntimeException("[ERROR] Invalid type: " + javaObjectRoutes);
        }
        if (isBasicType(javaObjectRoutes)) {
            Logger.log("[WARN] " + javaObjectRoutes + " is a basic type, shouldn't call getObjectName()");
            return javaObjectRoutes;
        }
        if (javaObjectRoutes.endsWith("...")) { // deal with varargs type
            String tmp = javaObjectRoutes.substring(0, javaObjectRoutes.length() - 3);
            return javaObjectRoutes.substring(tmp.lastIndexOf('.') + 1);
        }
        return javaObjectRoutes.substring(javaObjectRoutes.lastIndexOf(".") + 1);
    }

    /**
     * Turn smali routes into Java object name
     * @param smaliObjectRoutes smali object routes (like "Ljava/lang/String;")
     * @return the corresponding Java object type of input smali object
     */
    private static String getSmaliObjectType(String smaliObjectRoutes) {
        if (smaliObjectRoutes == null) {
            return "Object";
        }
        if (!smaliObjectRoutes.startsWith("L")) {
            throw new RuntimeException("[ERROR] Invalid Object: " + smaliObjectRoutes);
        }
        smaliObjectRoutes = smaliObjectRoutes.substring(1).replaceAll("[/$]", ".");
        if (smaliObjectRoutes.endsWith(";")) {
            return smaliObjectRoutes.substring(0, smaliObjectRoutes.length() - 1);
        }
        return smaliObjectRoutes;
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
            objectType = getSmaliObjectType(objectType);
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
            parametersList.add(getRoutes(parameter.trim()));
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
     * A state machine that split smali parameters
     * @param parameters smali parameters String
     * @return Java parameters List
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
                    } else { // is an array of basic type
                        parametersList.add("[" + String.valueOf(c));
                    }
                    state = 0;
                    break;
                }
            }
        }
        return parametersList;
    }

}
