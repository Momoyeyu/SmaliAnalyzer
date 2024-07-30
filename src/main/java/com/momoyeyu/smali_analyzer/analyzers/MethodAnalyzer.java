package com.momoyeyu.smali_analyzer.analyzers;

import com.momoyeyu.smali_analyzer.element.SmaliConstructor;
import com.momoyeyu.smali_analyzer.element.SmaliMethod;
import com.momoyeyu.smali_analyzer.utils.Stepper;
import com.momoyeyu.smali_analyzer.utils.TypeTranslator;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MethodAnalyzer {

    private static final Pattern methodPattern = Pattern.compile("\\.method\\s+(((private)|(protected)|(public))\\s+)?((static)\\s+)?((abstract)\\s+)?((bridge)\\s+)?((synthetic)\\s+)?((varargs)\\s+)?(\\S+)\\((.*?)\\)([a-zA-Z/]++);?");

    /**
     * Test
     * @param args user input
     */
    public static void main(String[] args) {
        System.out.println(getSignature(".method public varargs doInBackground([Ljava/lang/Object;)Ljava/lang/Void;"));
        System.out.println(getSignature(".method public abstract setActivityChooserModel(Landroidx/appcompat/widget/ActivityChooserModel;)V;"));
        System.out.println(getSignature(".method public static get(Landroid/content/Context;Ljava/lang/String;)Landroidx/appcompat/widget/ActivityChooserModel;"));
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
            smaliMethod.setAbstractModifier(matcher.group(stepper.step(2))); // abstract?
            smaliMethod.setSyntheticModifier(matcher.group(stepper.step(4)) != null); // synthetic?
            smaliMethod.setName(matcher.group(stepper.step(3))); // name
            smaliMethod.setReturnType(TypeTranslator.getRoutes(matcher.group(stepper.step(2)))); // return type

            // set parameters
            List<String> parametersList = TypeTranslator.getJavaParameters(matcher.group(stepper.step(-1)));
            if (matcher.group(stepper.step(-2)) != null) { // varargs?
                parametersList.set(parametersList.size() - 1, parametersList.getLast() + "...");
            }
            smaliMethod.setParametersList(parametersList);
        } else {
            throw new RuntimeException("Invalid method signature: " + smaliMethod.getSignature());
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
}
