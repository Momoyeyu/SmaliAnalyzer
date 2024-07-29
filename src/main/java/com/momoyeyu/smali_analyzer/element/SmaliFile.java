package com.momoyeyu.smali_analyzer.element;

import com.momoyeyu.smali_analyzer.utils.ImportPackageList;

import java.util.ArrayList;
import java.util.List;

public class SmaliFile {
    private String routes; // .source
    public final List<SmaliClass> classes;
    private final ImportPackageList importPackageList;

    public SmaliFile(SmaliClass smaliClass) {
        this.classes = new ArrayList<>();
        this.importPackageList = new ImportPackageList();
    }

    // class
    public String getRoutes() {
        return routes;
    }

    public void addClass(SmaliClass smaliClass) {
        this.classes.add(smaliClass);
    }

    @Override
    public String toString() {
        return null;
    }

    // import
    public ImportPackageList getImportPackageList() {
        return importPackageList;
    }
}
