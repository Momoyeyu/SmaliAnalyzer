package com.momoyeyu.smali_analyzer.analyzers;

import com.momoyeyu.smali_analyzer.element.SmaliConstructor;
import com.momoyeyu.smali_analyzer.element.SmaliMethod;
import com.momoyeyu.smali_analyzer.utils.TypeTranslator;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MethodAnalyzer {

    private static final Pattern methodPattern = Pattern.compile("\\.method\\s+(((private)|(protected)|(public))\\s+)?((static)\\s+)?((varargs)\\s+)?(\\w+)\\((.*?)\\)(\\S+?);?");

    public static void main(String[] args) {
        List<String> parametersList = TypeTranslator.getJavaParameters("Ljava/io/OutputStream;Ljava/lang/String;");
        System.out.println(listParameters(parametersList));  // src/main/java/com/momoyeyu/smali_analyzer/utils/MethodSignature.java
        System.out.println(getSignature(".method public varargs doInBackground([Ljava/lang/Object;)Ljava/lang/Void;"));
    }

    /**
     * Turn a smali method signature into Java method signature
     *
     * @test pass
     * @param smaliMethod SmaliMethod object
     */
    public static void translate(SmaliMethod smaliMethod) throws RuntimeException {
        Matcher matcher = methodPattern.matcher(smaliMethod.getSignature());
        if (matcher.find()) {
            smaliMethod.setAccessModifier(matcher.group(2)); // access?
            smaliMethod.setStaticModifier(matcher.group(7)); // static?
            smaliMethod.setName(matcher.group(10)); // name
            smaliMethod.setReturnType(TypeTranslator.getType(matcher.group(12))); // return type

            // set parameters
            List<String> parametersList = TypeTranslator.getJavaParameters(matcher.group(11));
            if (matcher.group(9) != null) { // varargs?
                parametersList.set(parametersList.size() - 1, parametersList.getLast() + "...");
            }
            smaliMethod.setParametersList(parametersList);
        } else {
            throw new RuntimeException("Invalid method signature: " + smaliMethod);
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
