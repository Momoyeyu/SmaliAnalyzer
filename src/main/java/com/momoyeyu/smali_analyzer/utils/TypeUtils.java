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
        System.out.println(isSmaliBasicType("B"));
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
     * Get Java Type's name from a smali type.
     * A 'Java Type's name' mean its name without package.
     * @param smaliType smali type, including basic type and object
     * @return corresponding Java type's name
     */
    public static String getNameFromSmali(String smaliType) {
        if (smaliType == null || smaliType.isEmpty()) {
            return null;
        }
        if (smaliType.endsWith(";"))
            return getNameFromSmali(smaliType.substring(0, smaliType.length() - 1));
        if (smaliType.startsWith("["))
            return getNameFromSmali(smaliType.substring(1)) + "[]";
        if (isSmaliBasicType(smaliType)) {
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

    /** Get Java type's name from a Java Type
     *
     * @param javaType a Java type mean a basic type or an object with its package
     * @return the name of the Java type (the one that don't contain package)
     * @throws IllegalArgumentException Invalid or Unknown Java object input
     */
    public static String getNameFromJava(String javaType) throws IllegalArgumentException {
        if (javaType.startsWith("@")) {
            int idx = javaType.indexOf(' ');
            return javaType.substring(0, idx + 1) + getNameFromJava(javaType.substring(idx + 1));
        }
        if (isJavaBasicType(javaType)) {
            return javaType;
        }
        return getObjectNameFromJava(javaType);
    }

    /**
     * Get Java type from a smali type.
     * A Java {@code type} mean Java's basic type or Java object <b>with its package ahead.</b>
     * The key difference from {@code name} is that 'name' don't have package, but {@code type} have.
     *
     * @param smaliType the datatype of a smali variable, including basic type and object
     * @return corresponding Java type
     */
    public static String getTypeFromSmali(String smaliType) {
        if (smaliType == null || smaliType.isEmpty()) {
            return null;
        }
        if (smaliType.startsWith("["))
            return getTypeFromSmali(smaliType.substring(1)) + "[]";
        if (isSmaliBasicType(smaliType)) {
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
     * Extract Java object's {@code classname} from its {@code routes}.
     * {@code classname} mean the object {@code name} without its package.
     *
     * @param routes Java object routes (like "java.lang.String")
     * @return the classname of the object
     * @throws IllegalArgumentException let the Logger catch and log the object information
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

    /**
     * Extract Java object's {@code classname} from its smali definition.
     * {@code classname} mean the object {@code name} without its package.
     *
     * @param smaliType smali object routes (like "Ljava/lang/String")
     * @return the classname of the smali object
     * @throws RuntimeException let the Logger catch and log the object information
     */
    public static String getObjectNameFromSmali(String smaliType)  throws IllegalArgumentException {
        return getObjectNameFromJava(getObjectRoutesFromSmali(smaliType));
    }

    /**
     * Return Java {@code routes} from smali signature.
     * This method is used for locating {@code package}.
     * The <b>difference</b></b> between {@code routes}
     * and {@code type} is that {@code routes} mean
     * {@code package + filename} whereas {@code type}
     * mean {@code package + classname}.
     *
     * @param object smali object routes (like "Ljava/lang/String;")
     * @return the corresponding Java object type of input smali object
     * @throws RuntimeException let the Logger catch and log the object information
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
     * Extract package from Java routes or type.
     * <pre>
     * {@code getObjectPackage("java.lang.String"); // java.lang}
     * </pre>
     * @param routes Java routes
     * @return package of the routes, or {@code default} if not explicit package
     */
    public static String getObjectPackageFromJava(String routes) {
        if (routes == null || routes.isBlank()) {
            throw new RuntimeException("Unknown type: " + routes);
        }
        if (routes.startsWith("L")) { // is smali object
            Logger.log("[WARN] " + routes + " is a smali object, shouldn't call getJavaObjectName()", true);
            routes = getObjectRoutesFromSmali(routes);
        }
        if (!routes.contains(".")) { // default package
            return "default";
        }
        return routes.substring(0, routes.lastIndexOf("."));
    }

    /**
     * Extract package from smali object.
     * <pre>
     * {@code getObjectPackage("Ljava/lang/String;"); // java.lang}
     * </pre>
     * @param object a smali object
     * @return package of the object
     */
    public static String getObjectPackageFromSmali(String object) {
        return getObjectPackageFromJava(getObjectRoutesFromSmali(object));
    }

    /**
     * Turn smali parameters {@code String} into
     * Java parameters {@code List}.
     * <p>
     * All Java parameter will remain their type,
     * which mean object package will remain.
     *
     * @test pass
     * @param parameters a line of smali parameter list string
     * @return a list of java type parameters
     */
    public static List<String> getJavaParametersFromSmali(String parameters) {
        List<String> parametersList = new ArrayList<>();
        if (parameters == null || parameters.isBlank()) {
            return parametersList;
        }
        List<String> params = splitSmaliParameters(parameters);
        for (String parameter : params) {
            parametersList.add(getTypeFromSmali(parameter.trim()));
        }
        return parametersList;
    }

    /**
     * Return weather a smali type is a basic type.
     * @param type smali type
     * @return boolean
     */
    public static boolean isSmaliBasicType(String type) {
        return basicTypeMap.containsKey(type);
    }

    /**
     * Return weather a Java type is a basic type.
     * @param type Java type
     * @return boolean
     */
    public static boolean isJavaBasicType(String type) {
        return basicTypeMap.containsValue(type);
    }

    /**
     * Split smali parameters {@code String} into a {@code List} of parameter
     * @param parameters smali parameters String
     * @return smali parameters List
     */
    public static List<String> splitSmaliParameters(String parameters) {
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
                        parametersList.add("[" + c);
                    }
                    state = 0;
                    break;
                }
            }
        }
        return parametersList;
    }

    public static boolean isVoid(String type) {
        if (type == null || type.isBlank()) {
            return false;
        }
        return type.equals("void") || type.equals("java.lang.Void");
    }

}
