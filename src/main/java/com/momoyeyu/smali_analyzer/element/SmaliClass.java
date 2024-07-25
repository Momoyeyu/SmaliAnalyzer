package com.momoyeyu.smali_analyzer.element;

import com.momoyeyu.smali_analyzer.analyzers.ClassAnalyzer;

import java.util.ArrayList;
import java.util.List;

public class SmaliClass extends SmaliElement {
    private SmaliClass superClass; // set once, get
    private List<SmaliClass> subClassList; // init, add, get
    private List<SmaliMethod> smaliMethodList; // init, add, get
    private boolean init; // get

    private String packageName;
    private String interfaceModifier;
    private String abstractModifier;
    private boolean translated;

    // constructor
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

    @Override
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
        sb.append(name).append(";");
        return sb.toString();
    }

    // getter
    public String getPackage() {
        return "package " + packageName.strip() + ";";
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

    public void setPackageName(String packageName) {
        this.packageName = packageName;
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

    public boolean isInit() {
        return init;
    }
}
