package com.momoyeyu.smali_analyzer.utils;

import java.util.HashMap;
import java.util.Map;

public class TypeMap {
    private static Map<String, String> typeMap = new HashMap<>();
    static {
        typeMap.put("Z", "boolean");
        typeMap.put("B", "byte");
        typeMap.put("C", "char");
        typeMap.put("D", "double");
        typeMap.put("F", "float");
        typeMap.put("I", "int");
        typeMap.put("J", "long");
        typeMap.put("S", "short");
        typeMap.put("V", "void");
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
            smaliType = smaliType.substring(1, smaliType.length());
        }
        String type;
        if (smaliType.startsWith("L")) {
            type = getObjectName(smaliType);
        } else {
            type = typeMap.get(smaliType);
        }
        return isArray ? type + "[]" : type;
    }

    private static String getObjectName(String name) {
        if (name == null || !name.startsWith("L")) {
            return null;
        }
        String routes = getPackageRoutes(name);
        return routes.substring(routes.lastIndexOf(".") + 1);
    }

    private static String getPackageRoutes(String smaliObject) {
        if (smaliObject == null || !smaliObject.startsWith("L")) {
            return null;
        }
        smaliObject = smaliObject.substring(1);
        smaliObject = smaliObject.replaceAll("[/$]", ".");
        return smaliObject;
    }

    /**
     * Get the package the smali object belong to.
     *
     * @param objectName smali full name of an Object like "Ljava/lang/String"
     * @return package of the object like "java.lang"
     */
    public static String getPackage(String objectName) {
        if (objectName == null || !objectName.startsWith("L")) {
            return null;
        }
        String routes = getPackageRoutes(objectName);
        return routes.substring(0, routes.lastIndexOf("."));
    }

    public static void main(String[] args) {
        System.out.println(getPackage("Landroidx/appcompat/widget/ActivityChooserModel"));
        System.out.println(getType("Landroidx/appcompat/widget/ActivityChooserModel"));
        System.out.println(getType("Z"));
        System.out.println(getType("[Ljava/lang/String"));
        System.out.println(getType("[B"));
    }

}
