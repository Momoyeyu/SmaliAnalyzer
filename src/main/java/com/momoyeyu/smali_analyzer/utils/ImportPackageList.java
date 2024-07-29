package com.momoyeyu.smali_analyzer.utils;

import java.util.*;

public class ImportPackageList {
    // 'java.lang' packages are imported by default.
    private static final String defaultImportPackages = "java\\.lang(\\.\\S+)?";
    private final Set<String> importPackages;
    private final Set<String> localPackages;

    /**
     * Test
     * @param args user input
     */
    public static void main(String[] args) {
        ImportPackageList importPackageList = new ImportPackageList();
        importPackageList.importPackage("java.lang.String"); // wouldn't be imported
        importPackageList.importPackage("java.util.List"); // import
        importPackageList.importPackage("java.util.ArrayList"); // import
        importPackageList.importPackage("java.lang"); // wouldn't be imported
        System.out.println(importPackageList);
    }

    public ImportPackageList() {
        this.importPackages = new HashSet<>();
        this.localPackages = new HashSet<>();
    }

    /**
     * Add a new package to the importPackageList.
     *
     * @param packageName both package and object name.
     */
    public void importPackage(String packageName) {
        if (packageName == null || packageName.matches(defaultImportPackages)) {
            return;
        }
        packageName = packageName.strip();
        this.importPackages.add(packageName);
    }

    public void addLocalPackage(String packageName) {
        if (packageName == null || packageName.matches(defaultImportPackages) || localPackages.contains(packageName)) {
            return;
        }
        this.localPackages.add(packageName);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String importPackage : this.importPackages) {
            sb.append("import ").append(importPackage).append(";\n");
        }
        return sb.toString();
    }

}
