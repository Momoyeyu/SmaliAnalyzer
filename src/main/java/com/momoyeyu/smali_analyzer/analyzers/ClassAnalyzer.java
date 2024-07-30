package com.momoyeyu.smali_analyzer.analyzers;

import com.momoyeyu.smali_analyzer.element.SmaliClass;
import com.momoyeyu.smali_analyzer.utils.Stepper;
import com.momoyeyu.smali_analyzer.utils.TypeTranslator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClassAnalyzer {

    private static final Pattern classPattern = Pattern.compile("\\.((class)|(super))\\s+(((private)|(protected)|(public))\\s+)?((final)\\s+)?((interface)\\s+)?((abstract)\\s+)?(L(.*)/((.*?)(\\$(.*))?));\\s*");

    /**
     * Testing usage of ClassAnalyzer.
     * @param args user input
     */
    public static void main(String[] args) {
        SmaliClass demoClass = new SmaliClass(".class public final abstract Landroidx/appcompat/widget/ActivityChooserModel$HistoricalRecord;");
        System.out.println(getSignature(demoClass));
        System.out.println(getRoutes(new SmaliClass(".class Landroidx/appcompat/widget/ActivityChooserModel;\n")));
        System.out.println(getRoutes(new SmaliClass(".class public interface abstract Landroidx/appcompat/widget/ActivityChooserModel$ActivityChooserModelClient;\n")));
    }

    /**
     * Set SmaliClass's Java properties by translating its smali signature.
     * @param smaliClass SmaliClass object
     * @throws RuntimeException its super class might not be stored in ClassRepository
     */
    public static void analyze(SmaliClass smaliClass) throws RuntimeException {
        Matcher matcher = classPattern.matcher(smaliClass.getSignature());
        if (matcher.find()) {
            Stepper stepper = new Stepper();
            smaliClass.setAccessModifier(matcher.group(stepper.step(5)));
            smaliClass.setFinalModifier(matcher.group(stepper.step(5)));
            smaliClass.setInterfaceModifier(matcher.group(stepper.step(2)));
            smaliClass.setAbstractModifier(matcher.group(stepper.step(2)));
            smaliClass.setPackageName(matcher.group(stepper.step(2)).replaceAll("/", "."));
            if (matcher.group(stepper.step(4)) != null) {
                smaliClass.setName(matcher.group(stepper.step(0)));
            } else {
                smaliClass.setName(matcher.group(stepper.step(-2)));
            }
        } else {
            throw new RuntimeException("Invalid signature: " + smaliClass.getSignature());
        }
    }

    public static String getRoutes(SmaliClass smaliClass) {
        Matcher matcher = classPattern.matcher(smaliClass.getSignature());
        if (matcher.find()) {
            return TypeTranslator.getRoutes("L" + matcher.group(16) + "/" + matcher.group(18));
        }
        return "";
    }

    /**
     * Get SmaliClass's Java signature
     * @param smaliClass SmaliClass object
     * @return Java signature
     */
    public static String getSignature(SmaliClass smaliClass) {
        return smaliClass.toJava() + ";";
    }

    /**
     * Get Java signature of SmaliClass from a String of SmaliClass.
     * Mostly used for testing.
     * @param smaliClass SmaliClass signature String
     * @return Java signature
     */
    public static String getSignature(String smaliClass) {
        return getSignature(new SmaliClass(smaliClass));
    }

    /**
     * Judge weather smaliClass is main class of a file
     * @param smaliClass SmaliClass object
     * @return boolean
     */
    public static boolean isMainClass(SmaliClass smaliClass) {
        Matcher matcher = classPattern.matcher(smaliClass.getSignature());
        return matcher.find() && matcher.group(20) == null;
    }

}
