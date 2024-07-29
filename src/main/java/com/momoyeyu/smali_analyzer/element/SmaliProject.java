package com.momoyeyu.smali_analyzer.element;

import java.util.HashMap;
import java.util.Map;

public class SmaliProject {

    private final Map<String, SmaliFile> files = new HashMap<>(); // <routes, SmaliFile>
    private static final SmaliProject project = new SmaliProject();

    private SmaliProject() {}

    public static SmaliProject getProject() {
        return project;
    }

    public static void addFile(String routes, SmaliFile file) {
        project.files.put(routes, file);
    }

    public static SmaliFile getFile(String routes) {
        return project.files.get(routes);
    }

}
