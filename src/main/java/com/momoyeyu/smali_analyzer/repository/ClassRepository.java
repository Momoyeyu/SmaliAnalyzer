package com.momoyeyu.smali_analyzer.repository;

import com.momoyeyu.smali_analyzer.element.SmaliClass;

import java.util.HashMap;
import java.util.Map;

public class ClassRepository {

    // Map<signature, SmaliClass>
    private static Map<String, SmaliClass> classMap = new HashMap<String, SmaliClass>();

    public static void addClass(SmaliClass smaliClass) {
        classMap.put(smaliClass.toString(), smaliClass);
    }

    public static SmaliClass getClass(String signature) {
        return classMap.get(signature);
    }

}
