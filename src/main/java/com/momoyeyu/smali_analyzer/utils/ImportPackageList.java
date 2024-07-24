package com.momoyeyu.smali_analyzer.utils;

import java.util.LinkedList;
import java.util.List;

public class ImportPackageList {
    private List<String> list;

    private static List<String> defaultImportPackageList = new LinkedList<>();

    static {
        defaultImportPackageList.add("java.lang.*");
    }

    public ImportPackageList() {
        this.list = new LinkedList<String>();
    }

}
