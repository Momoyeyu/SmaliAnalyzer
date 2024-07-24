package com.momoyeyu.smali_analyzer.analyzers;

import com.momoyeyu.smali_analyzer.entity.SmaliClass;
import com.momoyeyu.smali_analyzer.utils.TypeTranslator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClassAnalyzer {

    private static final Pattern classPattern = Pattern.compile("\\.class\\s+(((private)|(protected)|(public))\\s+)?((final)\\s+)?((interface)\\s+)?((abstract)\\s+)?(.*?);\\s*");

    public static String getJavaSignature(SmaliClass smaliClass) {
        Matcher matcher = classPattern.matcher(smaliClass.getClassName());
        if (matcher.find()) {
            smaliClass.setAccessModifier(matcher.group(2) == null ? "default" : matcher.group(2));
            smaliClass.setFinalModifier(matcher.group(7) == null ? "instance" : matcher.group(7));
        }
    }

    private static String getSignatureBody(String signature) {
        return signature.substring(signature.lastIndexOf(" ") + 1, signature.length() - 1).strip();
    }

    public static String getClassName(SmaliClass smaliClass) {
        return TypeTranslator.getObjectName(getSignatureBody(smaliClass.getSignature().strip()));
    }

    public static void main(String[] args) {
        System.out.println(getSignatureBody(".class public final Landroidx/appcompat/widget/ActivityChooserModel$HistoricalRecord;\n"));
    }

}
