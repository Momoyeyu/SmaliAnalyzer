package com.momoyeyu.smali_analyzer.analyzers;

import com.momoyeyu.smali_analyzer.entity.SmaliClass;
import com.momoyeyu.smali_analyzer.entity.SmaliConstructor;
import com.momoyeyu.smali_analyzer.entity.SmaliMethod;
import com.momoyeyu.smali_analyzer.utils.TypeTranslator;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MethodAnalyzer {

    private static final Pattern methodPattern = Pattern.compile("\\.method\\s+(((private)|(protected)|(public))\\s+)?((static)\\s+)?((varargs)\\s+)?(\\w+)\\((.*?)\\)(.+?);");
    private static final Pattern constructorPattern = Pattern.compile("\\.method\\s+(((private)|(protected)|(public))\\s+)?((static)\\s+)?constructor\\s+((<init>)|(<clinit>))\\((.*?)\\)V");

    public static void main(String[] args) {
        List<String> parametersList = TypeTranslator.getJavaParameters("Ljava/io/OutputStream;Ljava/lang/String;");
        System.out.println(listParameters(parametersList));  // src/main/java/com/momoyeyu/smali_analyzer/utils/MethodSignature.java
        MethodAnalyzer methodSignature = new MethodAnalyzer();
        System.out.println(methodSignature.getJavaMethod(".method public varargs doInBackground([Ljava/lang/Object;)Ljava/lang/Void;"));
        System.out.println(methodSignature.getJavaConstructor(".method static constructor <clinit>()V",
                ".class public final Landroidx/appcompat/widget/ActivityChooserModel$HistoricalRecord;\n"));
    }

    /**
     * Automatically return signature of method or constructor
     * @param smaliMethod
     * @return
     */
    public static String getJavaSignature(SmaliMethod smaliMethod) {
        if (isConstructor(smaliMethod)) {
            return getJavaConstructor((SmaliConstructor) smaliMethod);
        }
        return getJavaMethod(smaliMethod);
    }

    /**
     * Turn a smali method signature into Java method signature
     *
     * @test pass
     * @param smaliMethod SmaliMethod object
     * @return Java method signature
     */
    public static String getJavaMethod(SmaliMethod smaliMethod) {
        String smaliSignature = smaliMethod.getSignature().strip();
        Matcher matcher = methodPattern.matcher(smaliSignature);

        if (matcher.find()) {
            smaliMethod.setAccessModifier(matcher.group(2) == null ? "default" : matcher.group(2).strip()); // access?
            smaliMethod.setStaticModifier(matcher.group(7) == null ? "instance" : matcher.group(7).strip()); // static?
            smaliMethod.setMethodName(matcher.group(10)); // name
            smaliMethod.setReturnType(TypeTranslator.getType(matcher.group(12)).strip()); // ret type

            // set parameters
            List<String> parametersList = TypeTranslator.getJavaParameters(matcher.group(11));
            if (matcher.group(9) != null) { // varargs?
                parametersList.set(parametersList.size() - 1, parametersList.getLast() + "...");
            }
            smaliMethod.setParametersList(parametersList);
        }
        return smaliMethod.getJavaSignature();
    }

    public static String getJavaMethod(String smaliSignature) {
        SmaliMethod smaliMethod = new SmaliMethod(smaliSignature);
        return getJavaMethod(smaliMethod);
    }

    /**
     * Get parameters list in Java style
     *
     * @test pass
     * @param parametersList a List of java parameters type
     * @return Java method signature
     */
    public static String listParameters(List<String> parametersList) {
        if (parametersList == null || parametersList.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder(parametersList.getFirst() + " p1");
        for (int i = 1; i < parametersList.size(); i++) {
            sb.append(String.format(", %s p%d",parametersList.get(i), i + 1));
        }
        return sb.toString();
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

    public static boolean isConstructor(SmaliMethod smaliMethod) {
        return isConstructor(smaliMethod.getSignature());
    }

    public static String getJavaConstructor(SmaliConstructor smaliConstructor) {
        Matcher matcher = constructorPattern.matcher(smaliConstructor.getSignature());
        if (matcher.find()) {
            smaliConstructor.setAccessModifier(matcher.group(2) == null ? "default" : matcher.group(2)); // access?
            smaliConstructor.setStaticModifier(matcher.group(7) == null ? "instance" : matcher.group(7)); // static?
            smaliConstructor.setInitType(matcher.group(8)); // init type
            smaliConstructor.setParametersList(TypeTranslator.getJavaParameters(matcher.group(11))); // params?
        }
        return smaliConstructor.getJavaSignature();
    }

    private static String getJavaConstructor(String smaliConstructor, String onwerClass) {
        return getJavaConstructor(new SmaliConstructor(smaliConstructor, new SmaliClass(onwerClass)));
    }
}
