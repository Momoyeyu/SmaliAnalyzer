package com.momoyeyu.smali_analyzer.analyzers;

import com.momoyeyu.smali_analyzer.element.SmaliConstructor;
import com.momoyeyu.smali_analyzer.element.SmaliMethod;
import com.momoyeyu.smali_analyzer.utils.TypeTranslator;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MethodAnalyzer {

    private static final Pattern methodPattern = Pattern.compile("\\.method\\s+(((private)|(protected)|(public))\\s+)?((static)\\s+)?((varargs)\\s+)?(\\w+)\\((.*?)\\)(.+?);");

    public static void main(String[] args) {
        List<String> parametersList = TypeTranslator.getJavaParameters("Ljava/io/OutputStream;Ljava/lang/String;");
        System.out.println(listParameters(parametersList));  // src/main/java/com/momoyeyu/smali_analyzer/utils/MethodSignature.java
        System.out.println(getJavaMethod(".method public varargs doInBackground([Ljava/lang/Object;)Ljava/lang/Void;"));
    }

    /**
     * Automatically return signature of method or constructor.
     *
     * @author momoyeyu
     * @param smaliMethod SmaliMethod Object
     * @return Java style method signature of smaliMethod
     */
    public static String getJavaMethod(SmaliMethod smaliMethod) {
        if (ConstructorAnalyzer.isConstructor(smaliMethod)) {
            return ConstructorAnalyzer.getJavaConstructor((SmaliConstructor) smaliMethod);
        }
        return smaliMethod.toJava();
    }

    /**
     * Automatically return signature of method or constructor.
     * It is String version, mostly used for testing.
     *
     * @author momoyeyu
     * @param smaliMethod SmaliMethod Object
     * @return Java style method signature of smaliMethod
     */
    public static String getJavaMethod(String smaliMethod) {
        return getJavaMethod(new SmaliMethod(smaliMethod));
    }

    /**
     * Turn a smali method signature into Java method signature
     *
     * @test pass
     * @param smaliMethod SmaliMethod object
     * @return Java method signature
     */
    public static void translate(SmaliMethod smaliMethod) throws RuntimeException {
        Matcher matcher = methodPattern.matcher(smaliMethod.getSignature().strip());
        if (matcher.find()) {
            smaliMethod.setAccessModifier(matcher.group(2) == null ? "default" : matcher.group(2).strip()); // access?
            smaliMethod.setStaticModifier(matcher.group(7) == null ? "instance" : matcher.group(7).strip()); // static?
            smaliMethod.setName(matcher.group(10)); // name
            smaliMethod.setReturnType(TypeTranslator.getType(matcher.group(12)).strip()); // ret type

            // set parameters
            List<String> parametersList = TypeTranslator.getJavaParameters(matcher.group(11));
            if (matcher.group(9) != null) { // varargs?
                parametersList.set(parametersList.size() - 1, parametersList.getLast() + "...");
            }
            smaliMethod.setParametersList(parametersList);
        } else {
            throw new RuntimeException("Invalid method signature: " + smaliMethod.getSignature());
        }
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
}
