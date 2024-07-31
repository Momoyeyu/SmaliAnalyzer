package com.momoyeyu.smali_analyzer.analyzers;

import com.momoyeyu.smali_analyzer.element.SmaliClass;
import com.momoyeyu.smali_analyzer.element.SmaliConstructor;
import com.momoyeyu.smali_analyzer.element.SmaliMethod;
import com.momoyeyu.smali_analyzer.utils.Stepper;
import com.momoyeyu.smali_analyzer.utils.TypeUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConstructorAnalyzer extends MethodAnalyzer {

    private static final Pattern constructorPattern = Pattern.compile("\\.method\\s+(((private)|(protected)|(public))\\s+)?((static)\\s+)?((final)\\s+)?((varargs)\\s+)?((synthetic)\\s+)?constructor\\s+((<init>)|(<clinit>))\\((\\S*?)\\)V");

    /**
     * Testing ConstructorAnalyzer/
     * @param args user input
     */
    public static void main(String[] args) {
        System.out.println(getSignature(".method public constructor <init>(Landroid/content/ComponentName;JF)V",
                ".class public final Landroidx/appcompat/widget/ActivityChooserModel$HistoricalRecord;"));
    }

    /**
     * Translate SmaliConstructor's signature into Java signature.
     * The translation result will be stored in the param object.
     * @param smaliConstructor SmaliConstructor object to be translated
     * @throws RuntimeException constructor signature mismatch regex.
     */
    public static void analyze(SmaliConstructor smaliConstructor) throws RuntimeException {
        Matcher matcher = constructorPattern.matcher(smaliConstructor.getSignature());
        if (matcher.find()) {
            Stepper stepper = new Stepper();
            smaliConstructor.setAccessModifier(matcher.group(stepper.step(2))); // access?
            smaliConstructor.setStaticModifier(matcher.group(stepper.step(5))); // static?
            smaliConstructor.setFinalModifier(matcher.group(stepper.step(2))); // final?
            String varargs = matcher.group(stepper.step(2)); // varargs?
            stepper.step(2); // synthetic
            smaliConstructor.setInitType(matcher.group(stepper.step(1))); // init type
//            smaliConstructor.setParametersList(TypeTranslator.getJavaParameters(matcher.group(stepper.step(3)))); // params?

            // set parameters
            List<String> parametersList = TypeUtils.getJavaParameters(matcher.group(stepper.step(3)));
            if (varargs != null) { // varargs?
                parametersList.set(parametersList.size() - 1, parametersList.getLast() + "...");
            }
            smaliConstructor.setParametersList(parametersList);
        } else {
            throw new RuntimeException("Unknown constructor: " + smaliConstructor.getSignature());
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
}
