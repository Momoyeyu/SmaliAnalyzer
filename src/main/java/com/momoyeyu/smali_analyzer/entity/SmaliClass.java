package com.momoyeyu.smali_analyzer.entity;

import java.util.ArrayList;
import java.util.List;

public class SmaliClass {
    private String classSignature; // init, get
    private SmaliClass superClass; // set once, get
    private List<SmaliClass> subClassList; // init, add, get
    private List<SmaliMethod> smaliMethodList; // init, add, get
    private boolean init; // get

    public SmaliClass() {
        this("");
        this.init = false;
    }

    public SmaliClass(String classSignature) {
        this(classSignature, new ArrayList<SmaliClass>(), new ArrayList<SmaliMethod>());
    }

    public SmaliClass(String classSignature, List<SmaliClass> subClassList, List<SmaliMethod> methodList) {
        this.classSignature = classSignature;
        this.subClassList = subClassList;
        this.smaliMethodList = methodList;
        this.superClass = null;
        this.init = true;
    }

    public void init(String classSignature) {
        this.classSignature = classSignature;
        this.init = true;
    }

    // setter
    /**
     * Set the super class (only once otherwise throw a runtime exception)
     * @param superClass super class object
     */
    public void setSuperClass(SmaliClass superClass) {
        if (superClass != null) {
            if (this.superClass == null) {
                this.superClass = superClass;
            } else {
                throw new RuntimeException("Super class already exists");
            }
        } else {
            throw new RuntimeException("Super class is null");
        }
    }

    // adder
    public void addSubClass(SmaliClass subClass) {
        subClassList.add(subClass);
    }

    public void addSmaliMethod(SmaliMethod method) {
        smaliMethodList.add(method);
    }

    // getter
    public String getClassSignature() {
        return classSignature;
    }

    public SmaliClass getSuperClass() {
        return superClass;
    }

    public List<SmaliClass> getSubClassList() {
        return subClassList;
    }

    public List<SmaliMethod> getSmaliMethodList() {
        return smaliMethodList;
    }

    public boolean isInit() {
        return init;
    }
}
