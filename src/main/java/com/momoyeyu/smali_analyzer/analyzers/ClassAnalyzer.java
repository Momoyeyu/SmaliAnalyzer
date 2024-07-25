package com.momoyeyu.smali_analyzer.analyzers;

import com.momoyeyu.smali_analyzer.entity.SmaliClass;
import com.momoyeyu.smali_analyzer.repository.ClassRepository;
import com.momoyeyu.smali_analyzer.utils.TypeTranslator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClassAnalyzer {

    private static final Pattern classPattern = Pattern.compile("\\.class\\s+(((private)|(protected)|(public))\\s+)?((final)\\s+)?((interface)\\s+)?((abstract)\\s+)?(L(.*)\\/((.*?)(\\$(.*))?));\\s*");

    public static void translate(SmaliClass smaliClass) throws RuntimeException {
        Matcher matcher = classPattern.matcher(smaliClass.getSignature());
        if (matcher.find()) {
            smaliClass.setAccessModifier(matcher.group(2) == null ? "default" : matcher.group(2));
            smaliClass.setFinalModifier(matcher.group(7) == null ? "null" : matcher.group(7));
            smaliClass.setInterfaceModifier(matcher.group(9) == null ? "null" : matcher.group(9));
            smaliClass.setAbstractModifier(matcher.group(11) == null ? "null" : matcher.group(11));
            smaliClass.setPackageName(matcher.group(13).replaceAll("/", "."));
            if (matcher.group(17) != null) {
                smaliClass.setClassName(matcher.group(17));
                try {
                    smaliClass.setSuperClass(ClassRepository.getClass(matcher.group(15)));
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            } else {
                smaliClass.setClassName(matcher.group(15));
            }
        } else {
            throw new RuntimeException("Invalid signature: " + smaliClass.getSignature());
        }
    }

    public static String getJavaClass(SmaliClass smaliClass) {
        return smaliClass.toJava();
    }

    public static String getJavaClass(String smaliClass) {
        return getJavaClass(new SmaliClass(smaliClass));
    }

    private static String getSignatureBody(String signature) {
        Matcher matcher = classPattern.matcher(signature);
        if (matcher.find()) {
            return matcher.group(2);
        }
        return null;
    }

    public static String getClassName(SmaliClass smaliClass) {
        return TypeTranslator.getObjectName(getSignatureBody(smaliClass.getSignature().strip()));
    }

    public static void main(String[] args) {
        System.out.println(getSignatureBody(".class public final Landroidx/appcompat/widget/ActivityChooserModel$HistoricalRecord;"));
        SmaliClass demoClass = new SmaliClass(".class public final abstract Landroidx/appcompat/widget/ActivityChooserModel$HistoricalRecord;");
        System.out.println(getJavaClass(demoClass));
    }

}
