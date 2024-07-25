package com.momoyeyu.smali_analyzer.analyzers;

import com.momoyeyu.smali_analyzer.element.SmaliClass;
import com.momoyeyu.smali_analyzer.repository.ClassRepository;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClassAnalyzer {

    private static final Pattern classPattern = Pattern.compile("\\.class\\s+(((private)|(protected)|(public))\\s+)?((final)\\s+)?((interface)\\s+)?((abstract)\\s+)?(L(.*)\\/((.*?)(\\$(.*))?));\\s*");

    public static void main(String[] args) {
        SmaliClass demoClass = new SmaliClass(".class public final abstract Landroidx/appcompat/widget/ActivityChooserModel$HistoricalRecord;");
        System.out.println(getSignature(demoClass));
    }

    public static void translate(SmaliClass smaliClass) throws RuntimeException {
        Matcher matcher = classPattern.matcher(smaliClass.getSignature());
        if (matcher.find()) {
            smaliClass.setAccessModifier(matcher.group(2));
            smaliClass.setFinalModifier(matcher.group(7));
            smaliClass.setInterfaceModifier(matcher.group(9));
            smaliClass.setAbstractModifier(matcher.group(11));
            smaliClass.setPackageName(matcher.group(13).replaceAll("/", "."));
            if (matcher.group(17) != null) {
                smaliClass.setName(matcher.group(17));
                try {
                    smaliClass.setSuperClass(ClassRepository.getClass(matcher.group(15)));
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            } else {
                smaliClass.setName(matcher.group(15));
            }
        } else {
            throw new RuntimeException("Invalid signature: " + smaliClass);
        }
    }

    public static String getSignature(SmaliClass smaliClass) {
        return smaliClass.toJava() + ";";
    }

    public static String getSignature(String smaliClass) {
        return getSignature(new SmaliClass(smaliClass));
    }

}
