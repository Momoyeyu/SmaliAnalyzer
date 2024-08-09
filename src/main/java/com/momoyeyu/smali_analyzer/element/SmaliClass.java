package com.momoyeyu.smali_analyzer.element;

import com.momoyeyu.smali_analyzer.analyzers.ClassAnalyzer;
import com.momoyeyu.smali_analyzer.utils.Formatter;
import com.momoyeyu.smali_analyzer.utils.Logger;
import com.momoyeyu.smali_analyzer.utils.TypeUtils;

import java.util.ArrayList;
import java.util.List;

public class SmaliClass extends SmaliElement {
    private SmaliClass superClass; // set once, get
    private List<SmaliClass> subClassList; // add, get
    private List<SmaliMethod> smaliMethodList; // add, get
    private List<SmaliField> smaliFieldList; // add, get

    private String routes;
    private String classFileType;
    private boolean abstractModifier;

    // constructor
    public SmaliClass(String classSignature) {
        this(classSignature, null, null, null);
    }

    public SmaliClass(String signature, List<SmaliClass> subClassList, List<SmaliMethod> methodList, List<SmaliField> fieldList) {
        super(signature);
        this.subClassList = subClassList == null ? new ArrayList<>() : subClassList;
        this.smaliMethodList = methodList == null ? new ArrayList<>() : methodList;
        this.smaliFieldList = fieldList == null ? new ArrayList<>() : fieldList;
        this.classFileType = "class";
        this.superClass = null;
    }

    @Override
    public String toString() {
        this.toJava();
        String pkg = TypeUtils.getObjectPackageFromJava(routes);
        if (pkg.equals("default")) {
            return toStringIndent(0);
        }
        return "package " + pkg + ";\n\n" + toStringIndent(0);
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

    /**
     * A Safe method that generate Java class signature from SmaliClass
     * @return Java signature of SmaliClass
     */
    @Override
    public String toJava() {
        if (!analyzed) {
            try {
                ClassAnalyzer.analyze(this);
                analyzed = true;
            } catch (RuntimeException e) {
                Logger.logException(e.getMessage());
                return Logger.logAnalysisFailure("class", signature);
            }
        }
        StringBuilder sb = new StringBuilder();
        if (!accessModifier.equals("default")) {
            sb.append(accessModifier).append(" ");
        }
        if (finalModifier) {
            sb.append("final ");
        }
        if (abstractModifier) {
            sb.append("abstract ");
        }
        sb.append(classFileType).append(" "); // class / interface / enum
        sb.append(name);
        if (superClass != null && !superClass.getName().equals("Object")) {
            sb.append(" extends ");
            sb.append(superClass.getName());
        }
        return sb.toString();
    }

    // getter
    public String getRoutes() {
        return routes;
    }

    @Override
    public String getName() {
        if (this.name == null) {
            ClassAnalyzer.analyze(this);
        }
        return this.name;
    }

    public String getClassFileType() {
        return classFileType;
    }

    public String getClassType() {
        return TypeUtils.getObjectPackageFromJava(routes) + "." + name;
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

    public void setRoutes(String routes) {
        this.routes = routes;
    }

    public void setClassFileType(String classFileType) {
        if (classFileType != null)
            this.classFileType = classFileType;
    }

    public void setAbstractModifier(String abstractModifier) {
        this.abstractModifier = abstractModifier != null;
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
}
