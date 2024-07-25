package com.momoyeyu.smali_analyzer.entity;

import com.momoyeyu.smali_analyzer.analyzers.ClassAnalyzer;
import com.momoyeyu.smali_analyzer.analyzers.ConstructorAnalyzer;

import java.util.ArrayList;
import java.util.List;

public class SmaliClass {
    private String signature; // init, get
    private SmaliClass superClass; // set once, get
    private List<SmaliClass> subClassList; // init, add, get
    private List<SmaliMethod> smaliMethodList; // init, add, get
    private boolean init; // get

    private String className;
    private String packageName;
    private String accessModifier;
    private String finalModifier;
    private String interfaceModifier;
    private String abstractModifier;
    private boolean translated;

    public SmaliClass() {
        this("");
        this.init = false;
    }

    public SmaliClass(String classSignature) {
        this(classSignature, new ArrayList<SmaliClass>(), new ArrayList<SmaliMethod>());
    }

    public SmaliClass(String classSignature, List<SmaliClass> subClassList, List<SmaliMethod> methodList) {
        this.signature = classSignature.strip();
        this.subClassList = subClassList;
        this.smaliMethodList = methodList;
        this.superClass = null;
        this.init = true;
        this.translated = false;
    }

    public void init(String classSignature) {
        this.signature = classSignature;
        this.init = true;
    }

    public String toJava() {
        if (!translated) {
            translated = true;
            try {
                ClassAnalyzer.translate(this);
            } catch (RuntimeException e) {
                e.printStackTrace();
                return "[ERROR] unable to translate class: " + signature;
            }
        }
        StringBuilder sb = new StringBuilder();
        if (!accessModifier.equals("default")) {
            sb.append(accessModifier).append(" ");
        }
        if (!finalModifier.equals("null")) {
            sb.append(finalModifier).append(" ");
        }
        if (!interfaceModifier.equals("null")) {
            sb.append(interfaceModifier).append(" ");
        }
        if (!abstractModifier.equals("null")) {
            sb.append(abstractModifier).append(" ");
        }
        sb.append(className).append(";");
        return sb.toString();
    }

    // setter
    /**
     * Set the super class.
     * This method should only be called once.
     * Multiple call will lead to a RuntimeException.
     *
     * @author momoyeyu
     * @param superClass super class object
     */
    public void setSuperClass(SmaliClass superClass) throws RuntimeException {
        if (superClass != null) {
            if (this.superClass == null) {
                this.superClass = superClass;
            } else {
                throw new RuntimeException("Super class already set");
            }
        }
    }

    // setter
    public void setClassName(String className) {
        this.className = className;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void setAccessModifier(String accessModifier) {
        this.accessModifier = accessModifier;
    }

    public void setFinalModifier(String finalModifier) {
        this.finalModifier = finalModifier;
    }

    public void setAbstractModifier(String abstractModifier) {
        this.abstractModifier = abstractModifier;
    }

    public void setInterfaceModifier(String interfaceModifier) {
        this.interfaceModifier = interfaceModifier;
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
        return signature;
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

    public boolean isTranslated() {
        return translated;
    }

    public String getClassName() {
        if (!translated) {
            ClassAnalyzer.translate(this);
        }
        return className;
    }
}
