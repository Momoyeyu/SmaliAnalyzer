package com.momoyeyu.smali_analyzer.analyzers;

import com.momoyeyu.smali_analyzer.element.SmaliClass;
import com.momoyeyu.smali_analyzer.element.SmaliConstructor;
import com.momoyeyu.smali_analyzer.element.SmaliMethod;
import com.momoyeyu.smali_analyzer.utils.TypeTranslator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConstructorAnalyzer extends MethodAnalyzer {

    private static final Pattern constructorPattern = Pattern.compile("\\.method\\s+(((private)|(protected)|(public))\\s+)?((static)\\s+)?constructor\\s+((<init>)|(<clinit>))\\((.*?)\\)V");

    /**
     * Testing ConstructorAnalyzer/
     * @param args user input
     */
    public static void main(String[] args) {
        System.out.println(getSignature(".method public constructor <init>(Landroid/content/ComponentName;JF)V",
                ".class public final Landroidx/appcompat/widget/ActivityChooserModel$HistoricalRecord;"));
    }

    /**
     * Check is the method given a constructor
     *
     * @param smaliSignature smali constructor signature
     * @return true or false
     */
    public static boolean isConstructor(String smaliSignature) {
        return constructorPattern.matcher(smaliSignature).matches();
    }

    /**
     * Judge if the given method a constructor.
     * @param smaliMethod SmaliMethod object
     * @return boolean
     */
    public static boolean isConstructor(SmaliMethod smaliMethod) {
        return isConstructor(smaliMethod.getSignature());
    }

    /**
     * Translate SmaliConstructor's signature into Java signature.
     * The translation result will be stored in the param object.
     * @param smaliConstructor SmaliConstructor object to be translated
     * @throws RuntimeException constructor signature mismatch regex.
     */
    public static void translate(SmaliConstructor smaliConstructor) throws RuntimeException {
        Matcher matcher = constructorPattern.matcher(smaliConstructor.getSignature());
        if (matcher.find()) {
            smaliConstructor.setAccessModifier(matcher.group(2)); // access?
            smaliConstructor.setStaticModifier(matcher.group(7)); // static?
            smaliConstructor.setInitType(matcher.group(8)); // init type
            smaliConstructor.setParametersList(TypeTranslator.getJavaParameters(matcher.group(11))); // params?
        } else {
            throw new RuntimeException("[WARN] Invalid constructor: " + smaliConstructor.getSignature());
        }
    }

    /**
     * Get SmaliConstructor's Java signature
     * @param smaliConstructor SmaliConstructor object
     * @return Java signature
     */
    public static String getSignature(SmaliConstructor smaliConstructor) {
        return smaliConstructor.toJava() + ";";
    }

    /**
     * Get Java signature of smaliConstructor from a String of smaliConstructor.
     * Mostly used for testing.
     * @param smaliConstructor smaliConstructor signature String
     * @return Java signature
     */
    public static String getSignature(String smaliConstructor, String onwerClass) {
        return getSignature(new SmaliConstructor(smaliConstructor, new SmaliClass(onwerClass)));
    }
}
