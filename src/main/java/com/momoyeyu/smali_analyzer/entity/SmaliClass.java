package com.momoyeyu.smali_analyzer.entity;

import com.momoyeyu.smali_analyzer.analyzers.ClassAnalyzer;

import java.util.ArrayList;
import java.util.List;

public class SmaliClass {
    private String classSignature; // init, get
    private SmaliClass superClass; // set once, get
    private List<SmaliClass> subClassList; // init, add, get
    private List<SmaliMethod> smaliMethodList; // init, add, get
    private boolean init; // get

    private String className;
    private String packageName;
    protected String accessModifier;
    protected String finalModifier;

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
    public String getSignature() {
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

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassName() {
        if (this.className == null || this.className.isEmpty()) {
            this.className = ClassAnalyzer.getClassName(this);
        }
        return className;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getAccessModifier() {
        return accessModifier;
    }

    public void setAccessModifier(String accessModifier) {
        this.accessModifier = accessModifier;
    }

    public String getFinalModifier() {
        return finalModifier;
    }

    public void setFinalModifier(String finalModifier) {
        this.finalModifier = finalModifier;
    }
}
