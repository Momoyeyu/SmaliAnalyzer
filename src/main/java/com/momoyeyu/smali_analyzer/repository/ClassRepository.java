package com.momoyeyu.smali_analyzer.repository;

import com.momoyeyu.smali_analyzer.element.SmaliClass;

import java.util.HashMap;
import java.util.Map;

public class ClassRepository {

    // Map<signature, SmaliClass>
    private final Map<String, SmaliClass> classMap = new HashMap<String, SmaliClass>();
    private static final ClassRepository INSTANCE = new ClassRepository();

    private ClassRepository() {}

    public static void addClass(SmaliClass smaliClass) {
        if (smaliClass == null || hasClass(smaliClass.getSignature())) {
            return;
        }
        INSTANCE.classMap.put(smaliClass.getSignature(), smaliClass);
    }

    public static SmaliClass getClass(String signature) {
        if (!hasClass(signature))
            addClass(new SmaliClass(signature));
        return INSTANCE.classMap.get(signature);
    }

    public static boolean hasClass(String signature) {
        return INSTANCE.classMap.containsKey(signature);
    }

}
