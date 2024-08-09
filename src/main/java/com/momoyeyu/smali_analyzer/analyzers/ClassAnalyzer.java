package com.momoyeyu.smali_analyzer.analyzers;

import com.momoyeyu.smali_analyzer.element.SmaliClass;
import com.momoyeyu.smali_analyzer.utils.Stepper;
import com.momoyeyu.smali_analyzer.utils.TypeUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClassAnalyzer {

    private static final Pattern classPattern = Pattern.compile("\\.((class)|(super))\\s+(((private)|(protected)|(public))\\s+)?((final)\\s+)?((interface)\\s+)?((abstract)\\s+)?((enum)\\s+)?((synthetic)\\s+)?((annotation)\\s+)?(L((\\S*)/)?((\\S*?)(\\$(\\S*))?));\\s*");

    /**
     * Testing usage of ClassAnalyzer.
     * @param args user input
     */
    public static void main(String[] args) {
        SmaliClass demoClass = new SmaliClass(".class public final abstract Landroidx/appcompat/widget/ActivityChooserModel$HistoricalRecord;");
        System.out.println(getSignature(demoClass));
        System.out.println(getRoutes(new SmaliClass(".class Landroidx/appcompat/widget/ActivityChooserModel;\n")));
        System.out.println(getRoutes(new SmaliClass(".class public enum La;")));
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
            smaliClass.setClassFileType(matcher.group(stepper.step(2))); // interface?
            smaliClass.setAbstractModifier(matcher.group(stepper.step(2)));
            stepper.step(2); // synthetic
            smaliClass.setClassFileType(matcher.group(stepper.step(2))); // enum?
            stepper.step(2); // annotation
            String pkg = matcher.group(stepper.step(3)) == null ? "" : matcher.group(stepper.step(0)) + ".";
            smaliClass.setRoutes(pkg.replaceAll("/", ".") + matcher.group(stepper.step(2)));
            if (matcher.group(stepper.step(2)) != null) {
                smaliClass.setName(matcher.group(stepper.step(0)));
            } else {
                smaliClass.setName(matcher.group(stepper.step(-2)));
            }
        } else {
            throw new RuntimeException("Unknown class: " + smaliClass.getSignature());
        }
    }

    public static String getRoutes(SmaliClass smaliClass) {
        if (!smaliClass.isAnalyzed())
            analyze(smaliClass);
        return smaliClass.getRoutes();
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
        if (!smaliClass.isAnalyzed())
            ClassAnalyzer.analyze(smaliClass);
        return smaliClass.getName().equals(TypeUtils.getObjectNameFromJava(smaliClass.getRoutes()));
    }

}
