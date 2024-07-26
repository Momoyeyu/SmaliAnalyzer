package com.momoyeyu.smali_analyzer.element;

import com.momoyeyu.smali_analyzer.analyzers.ClassAnalyzer;
import com.momoyeyu.smali_analyzer.utils.Formatter;
import com.momoyeyu.smali_analyzer.utils.Logger;

import java.util.ArrayList;
import java.util.List;

public class SmaliClass extends SmaliElement {
    private SmaliClass superClass; // set once, get
    private List<SmaliClass> subClassList; // add, get
    private List<SmaliMethod> smaliMethodList; // add, get
    private List<SmaliField> smaliFieldList; // add, get
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
        this(classSignature, null, null, null);
    }

    public SmaliClass(String signature, List<SmaliClass> subClassList, List<SmaliMethod> methodList, List<SmaliField> fieldList) {
        super(signature);
        this.subClassList = subClassList == null ? new ArrayList<>() : subClassList;
        this.smaliMethodList = methodList == null ? new ArrayList<>() : methodList;
        this.smaliFieldList = fieldList == null ? new ArrayList<>() : fieldList;
        this.superClass = null;
        this.init = true;
        this.translated = false;
    }

    public void init(String classSignature) {
        this.signature = classSignature;
        this.init = true;
    }

    @Override
    public String toString() {
        this.toJava();
        return "package " + packageName + ";\n\n" + toStringIndent(0);
    }

    private String toStringIndent(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append("\t".repeat(Math.max(0, indent))).append(this.toJava()).append(" {\n");
        for (SmaliField field : smaliFieldList) {
            sb.append(Formatter.addIndent(field.toString(), indent + 1));
//            sb.append("\t".repeat(Math.max(0, indent + 1))).append(field.toJava()).append("\n");
        }
        for (SmaliMethod method : smaliMethodList) {
            sb.append(Formatter.addIndent(method.toString(), indent + 1));
//            sb.append("\t".repeat(Math.max(0, indent + 1))).append(method.toJava()).append(";\n\n");
        }
        for (SmaliClass superClass : subClassList) {
            sb.append(Formatter.addIndent(superClass.toStringIndent(indent), indent + 1));
        }
        sb.append("\t".repeat(Math.max(0, indent))).append("}\n");
        return sb.toString();
    }



    @Override
    public String toJava() {
        if (!translated) {
            try {
                ClassAnalyzer.translate(this);
                translated = true;
            } catch (RuntimeException e) {
                e.printStackTrace();
                return Logger.failToAnalyze("class", signature);
            }
        }
        StringBuilder sb = new StringBuilder();
        if (!accessModifier.equals("default")) {
            sb.append(accessModifier).append(" ");
        }
        if (!finalModifier.equals("default")) {
            sb.append(finalModifier).append(" ");
        }
        if (!abstractModifier.equals("default")) {
            sb.append(abstractModifier).append(" ");
        }
        if (!interfaceModifier.equals("default")) {
            sb.append(interfaceModifier).append(" ");
        } else {
            sb.append("class ");
        }
        sb.append(name);
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
        this.abstractModifier = abstractModifier == null ? "default" : abstractModifier;
    }

    public void setInterfaceModifier(String interfaceModifier) {
        this.interfaceModifier = interfaceModifier == null ? "default" : interfaceModifier;
    }

    // adder
    public void addSubClass(SmaliClass subClass) {
        subClassList.add(subClass);
    }

    public void addSmaliMethod(SmaliMethod method) {
        smaliMethodList.add(method);
    }

    public void addSmaliField(SmaliField field) {
        smaliFieldList.add(field);
    }

    public boolean isInit() {
        return init;
    }
}
