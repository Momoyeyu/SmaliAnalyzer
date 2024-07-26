package com.momoyeyu.smali_analyzer.analyzers;

import com.momoyeyu.smali_analyzer.element.SmaliClass;
import com.momoyeyu.smali_analyzer.repository.ClassRepository;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClassAnalyzer {

    private static final Pattern classPattern = Pattern.compile("\\.((class)|(super))\\s+(((private)|(protected)|(public))\\s+)?((final)\\s+)?((interface)\\s+)?((abstract)\\s+)?(L(.*)\\/((.*?)(\\$(.*))?));\\s*");

    public static void main(String[] args) {
        SmaliClass demoClass = new SmaliClass(".class public final abstract Landroidx/appcompat/widget/ActivityChooserModel$HistoricalRecord;");
        System.out.println(getSignature(demoClass));
    }

    public static void translate(SmaliClass smaliClass) throws RuntimeException {
        Matcher matcher = classPattern.matcher(smaliClass.getSignature());
        if (matcher.find()) {
            smaliClass.setAccessModifier(matcher.group(5));
            smaliClass.setFinalModifier(matcher.group(10));
            smaliClass.setInterfaceModifier(matcher.group(12));
            smaliClass.setAbstractModifier(matcher.group(14));
            smaliClass.setPackageName(matcher.group(16).replaceAll("/", "."));
            if (matcher.group(20) != null) {
                smaliClass.setName(matcher.group(20));
                try {
                    smaliClass.setSuperClass(ClassRepository.getClass(matcher.group(18)));
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            } else {
                smaliClass.setName(matcher.group(18));
            }
        } else {
            throw new RuntimeException("Invalid signature: " + smaliClass.getSignature());
        }
    }

    public static String getSignature(SmaliClass smaliClass) {
        return smaliClass.toJava() + ";";
    }

    public static String getSignature(String smaliClass) {
        return getSignature(new SmaliClass(smaliClass));
    }

}
