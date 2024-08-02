package com.momoyeyu.smali_analyzer.analyzers;

import com.momoyeyu.smali_analyzer.element.SmaliConstructor;
import com.momoyeyu.smali_analyzer.element.SmaliMethod;
import com.momoyeyu.smali_analyzer.utils.Stepper;
import com.momoyeyu.smali_analyzer.utils.TypeUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MethodAnalyzer {

    private static final Pattern methodPattern = Pattern.compile("\\.method\\s+(((private)|(protected)|(public))\\s+)?((static)\\s+)?((final)\\s+)?(((declared-synchronized)|(synchronized))\\s+)?((bridge)\\s+)?((varargs)\\s+)?((native)\\s+)?((abstract)\\s+)?((synthetic)\\s+)?(\\S+)\\((.*?)\\)([a-zA-Z/\\[]++);?");

    /**
     * Test
     * @param args user input
     */
    public static void main(String[] args) {
        System.out.println(getSignature(".method public varargs doInBackground([Ljava/lang/Object;)Ljava/lang/Void;"));
        System.out.println(getSignature(".method public abstract setActivityChooserModel(Landroidx/appcompat/widget/ActivityChooserModel;)V;"));
        System.out.println(getSignature(".method public static get(Landroid/content/Context;Ljava/lang/String;I)Landroidx/appcompat/widget/ActivityChooserModel;"));
    }

    /**
     * Turn a smali method signature into Java method signature
     *
     * @test pass
     * @param smaliMethod SmaliMethod object
     */
    public static void analyze(SmaliMethod smaliMethod) throws RuntimeException {
        Matcher matcher = methodPattern.matcher(smaliMethod.getSignature());
        if (matcher.find()) {
            Stepper stepper = new Stepper();
            smaliMethod.setAccessModifier(matcher.group(stepper.step(2))); // access?
            smaliMethod.setStaticModifier(matcher.group(stepper.step(5))); // static?
            smaliMethod.setFinalModifier(matcher.group(stepper.step(2))); // final?
            smaliMethod.setSynchronizedModifier(matcher.group(stepper.step(2))); // synchronized?
            stepper.step(4); // bridge?
            String varargs = matcher.group(stepper.step(2)); // varargs? It would be handled in later code. Don't do it here.
            smaliMethod.setNativeModifier(matcher.group(stepper.step(2))); // native?
            smaliMethod.setAbstractModifier(matcher.group(stepper.step(2))); // abstract?
            smaliMethod.setSyntheticModifier(matcher.group(stepper.step(2)) != null); // synthetic?
            smaliMethod.setName(matcher.group(stepper.step(1))); // name
            smaliMethod.setReturnType(TypeUtils.getTypeFromSmali(matcher.group(stepper.step(2)))); // return type

            // set parameters
            List<String> parametersList = TypeUtils.getJavaParametersFromSmali(matcher.group(stepper.step(-1)));
            if (varargs != null) { // varargs?
                parametersList.set(parametersList.size() - 1, parametersList.getLast() + "...");
            }
            smaliMethod.setParametersList(parametersList);
        } else {
            throw new RuntimeException("Unknown method: " + smaliMethod.getSignature());
        }
    }

    /**
     * Return Java style method signature.
     *
     * @author momoyeyu
     * @param smaliMethod SmaliMethod Object
     * @return Java style method signature of smaliMethod
     */
    public static String getSignature(SmaliMethod smaliMethod) {
        if (ConstructorAnalyzer.isConstructor(smaliMethod)) {
            return ConstructorAnalyzer.getSignature((SmaliConstructor) smaliMethod);
        }
        return smaliMethod.toJava() + ";";
    }

    /**
     * Return Java style method signature.
     * It is the String version for basic testing.
     *
     * @author momoyeyu
     * @param smaliMethod SmaliMethod Object
     * @return Java style method signature of smaliMethod
     */
    public static String getSignature(String smaliMethod) {
        return getSignature(new SmaliMethod(smaliMethod));
    }

    /**
     * Get parameters list in Java style
     *
     * @test pass
     * @param parametersList a List of java parameters type
     * @return Java method signature
     */
    public static String listParameters(List<String> parametersList, boolean isStatic) {
        if (parametersList == null || parametersList.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int idx = 0; idx < parametersList.size(); idx++) {
            sb.append(String.format("%s p%d, ", TypeUtils.getNameFromJava(parametersList.get(idx)), isStatic ? idx : idx + 1));
        }
        return sb.delete(sb.length() - 2, sb.length()).toString();
    }

    public static String listParameters(List<String> parametersList, boolean isStatic, List<String> arguments) {
        if (parametersList == null || parametersList.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int idx = 0; idx < parametersList.size(); idx++) {
            sb.append(TypeUtils.getNameFromJava(parametersList.get(idx))).append(" ");
            sb.append(arguments.get(isStatic ? idx : idx + 1)).append(", ");
        }
        return sb.delete(sb.length() - 2, sb.length()).toString();
    }
}
