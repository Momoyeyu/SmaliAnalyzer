package com.momoyeyu.smali_analyzer.utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ImportPackageList {
    private List<String> list;
    private static List<String> defaultImportPackageList = new LinkedList<>();
    static {
        // 'java.lang' packages are imported by default.
        defaultImportPackageList.add("java\\.lang(\\.\\S+)?");
    }

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
        this.list = new ArrayList<>();
    }

    public void importPackage(String packageName) {
        packageName = packageName.strip();
        for (String importPackage : defaultImportPackageList) {
            if (packageName.matches(importPackage)) {
                return;
            }
        }
        if (packageName != null && !packageName.isEmpty()) {
            this.list.add(packageName);
        }
    }

    @Override
    public String toString() {
        list.sort(null);
        StringBuilder sb = new StringBuilder();
        for (String importPackage : this.list) {
            sb.append("import ").append(importPackage).append(";\n");
        }
        return sb.toString();
    }

}
