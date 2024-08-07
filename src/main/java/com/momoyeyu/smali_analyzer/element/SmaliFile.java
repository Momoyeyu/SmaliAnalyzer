package com.momoyeyu.smali_analyzer.element;

import com.momoyeyu.smali_analyzer.analyzers.ClassAnalyzer;
import com.momoyeyu.smali_analyzer.utils.Logger;

import java.util.Stack;

public class SmaliFile {
    private final String routes;
    private SmaliClass mainClass;
    private final Stack<SmaliClass> classStack = new Stack<>();

    public SmaliFile(String routes) {
        this.routes = routes;
    }

    public String getRoutes() {
        return routes;
    }

    public void addClass(SmaliClass smaliClass) {
        if (ClassAnalyzer.isMainClass(smaliClass)) {
            mainClass = smaliClass;
        } else {
            if (mainClass == null) {
                classStack.push(smaliClass);
            } else {
                mainClass.addSubClass(smaliClass);
                arrangeFile();
            }
        }
    }

    /**
     * Push all subClass into mainClass
     */
    public void arrangeFile() {
        while (!classStack.isEmpty()) {
            mainClass.addSubClass(classStack.pop());
        }
    }

    @Override
    public String toString() {
        if (mainClass == null) {
            Logger.log("[ERROR] lost main class at: " + routes);
            StringBuilder builder = new StringBuilder();
            for (SmaliClass smaliClass : classStack) {
                builder.append(smaliClass.toString());
            }
            return builder.toString();
        }
        arrangeFile();
        return mainClass.toString();
    }
}
