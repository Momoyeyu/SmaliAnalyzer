package com.momoyeyu.smali_analyzer.utils;

import java.util.*;

public class TypeUtils {

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
//        System.out.println(getObjectPackageFromJava("Landroidx/appcompat/widget/ActivityChooserModel"));
//        System.out.println(getObjectPackageFromSmali("La")); // it should be null
//        System.out.println(getObjectNameFromSmali("La")); // it should be null
        System.out.println(getNameFromSmali("[Landroidx/appcompat/widget/ActivityChooserModel;"));
        System.out.println(getNameFromSmali("[La"));
        System.out.println(getNameFromSmali("["));
        System.out.println(getNameFromSmali("[Ljava/lang/String;"));
//        System.out.println(getRoutes("Landroidx/appcompat/widget/ActivityChooserModel"));
//        System.out.println(getRoutes("Z"));
//        System.out.println(getRoutes("[Ljava/lang/String"));
//        System.out.println(getRoutes("[B"));
//        System.out.println(getObjectNameFromJava("java.lang.String[]"));
//        List<String> strings = splitParameters("Landroid/content/ComponentName;J[FB[JZD");
//        for (String string : strings) {
//            System.out.print(string + ",");
//        }
    }

    /**
     * Translate smali type into Java type
     * @param smaliType smali type
     * @return corresponding Java type
     */
    public static String getNameFromSmali(String smaliType) {
        if (smaliType == null || smaliType.isEmpty()) {
            return null;
        }
        if (smaliType.endsWith(";"))
            return getNameFromSmali(smaliType.substring(0, smaliType.length() - 1));
        if (smaliType.startsWith("["))
            return getNameFromSmali(smaliType.substring(1)) + "[]";
        if (isBasicType(smaliType)) {
            return smaliType;
        }
        try {
            return getObjectNameFromSmali(smaliType);
        } catch (IllegalArgumentException e) {
            Logger.logException(e.getMessage());
            Logger.logAnalysisFailure("type", smaliType);
            return smaliType;
        }
    }

    public static String getNameFromJava(String javaType) throws IllegalArgumentException {
        return getObjectNameFromJava(javaType);
    }

    /**
     * Turn smali datatype into corresponding java datatype.
     *
     * @param smaliType the datatype of a smali variable, including basic type and object
     * @return the java type of the input (return only the classname of an object)
     */
    public static String getTypeFromSmali(String smaliType) {
        if (smaliType == null || smaliType.isEmpty()) {
            return null;
        }
        if (smaliType.startsWith("["))
            return getTypeFromSmali(smaliType.substring(1)) + "[]";
        if (isBasicType(smaliType)) {
            return basicTypeMap.get(smaliType);
        }
        try {
            return getObjectRoutesFromSmali(smaliType);
        } catch (IllegalArgumentException e) {
            Logger.logException(e.getMessage());
            return Logger.logAnalysisFailure("type", smaliType);
        }
    }

    /**
     * Extract Java object's classname from its routes
     * @param routes Java object routes (like "java.lang.String")
     * @return the classname of the object
     * @throws RuntimeException let the Logger catch and log invalid object
     */
    public static String getObjectNameFromJava(String routes) throws IllegalArgumentException {
        if (routes == null || routes.isBlank()) {
            throw new IllegalArgumentException("Unknown type: " + routes);
        }
        if (routes.endsWith("..."))
            return getNameFromJava(routes.substring(0, routes.length() - 3)) + "...";
        if (routes.endsWith("[]"))
            return getNameFromJava(routes.substring(0, routes.length() - 2)) + "[]";
        if (!routes.contains("."))
            return routes;
        return routes.substring(routes.lastIndexOf(".") + 1);
    }

    public static String getObjectNameFromSmali(String smaliType) {
        return getObjectNameFromJava(getObjectRoutesFromSmali(smaliType));
    }

    /**
     * Turn smali routes into Java object name
     * @param object smali object routes (like "Ljava/lang/String;")
     * @return the corresponding Java object type of input smali object
     */
    private static String getObjectRoutesFromSmali(String object) {
        if (object == null) {
            return null;
        }
        if (!object.startsWith("L")) {
            throw new RuntimeException("Unknown Object: " + object);
        }
        object = object.substring(1).replaceAll("[/$]", ".");
        if (object.endsWith(";")) {
            return object.substring(0, object.length() - 1);
        }
        return object;
    }

    /**
     * Return package the Java object belong to.
     * <pre>
     *     getObjectPackage("java.lang.String");
     *     return "java.lang"
     * </pre>
     * @param routes a Java object with package and name
     * @return package of the object
     */
    public static String getObjectPackageFromJava(String routes) {
        if (routes == null || routes.isBlank()) {
            throw new RuntimeException("Unknown type: " + routes);
        }
        if (routes.startsWith("L")) { // is smali object
            Logger.log("[WARN] " + routes + " is a smali object, shouldn't call getJavaObjectName()", true);
            routes = getObjectRoutesFromSmali(routes);
        }
        if (!routes.contains(".")) {
            return null;
        }
        return routes.substring(0, routes.lastIndexOf("."));
    }

    public static String getObjectPackageFromSmali(String routes) {
        return getObjectPackageFromJava(getObjectRoutesFromSmali(routes));
    }

    /**
     * Turn a line of smali parameters into a list of java parameters.
     * All Java object parameter will have their package with them.
     *
     * @test pass
     * @param parameters a line of smali parameter list string
     * @return a list of java type parameters
     */
    public static List<String> getJavaParameters(String parameters) {
        List<String> parametersList = new ArrayList<>();
        if (parameters == null || parameters.isBlank()) {
            return parametersList;
        }
        List<String> params = splitParameters(parameters);
        for (String parameter : params) {
            parametersList.add(getTypeFromSmali(parameter.trim()));
        }
        return parametersList;
    }

    /**
     * Return weather a Java type is a basic Java type.
     * @param type it should be a type that get from the getTypeFromSmali() method
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
