package com.momoyeyu.smali_analyzer.analyzers;

import com.momoyeyu.smali_analyzer.entity.SmaliMethod;
import com.momoyeyu.smali_analyzer.utils.TypeMap;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MethodAnalyzer {

    private static final Pattern constructorPattern = Pattern.compile(".method\\s+(((private)|(protected)|(public))\\s+)?((static)\\s+)?constructor\\s+((<init>)|(<clinit>))\\((.*?)\\)V");
    private static final Pattern methodPattern = Pattern.compile(".method\\s+(((private)|(protected)|(public))\\s+)?((static)\\s+)?((varargs)\\s+)?(\\w+)\\((.*?)\\)(.+?);");

    public static void main(String[] args) {
        List<String> parametersList = getParameters("Ljava/io/OutputStream;Ljava/lang/String;");
        System.out.println(listParameters(parametersList));  // src/main/java/com/momoyeyu/smali_analyzer/utils/MethodSignature.java
        List<String> body = new ArrayList<>();
        SmaliMethod smaliMethod = new SmaliMethod(
                ".method public varargs doInBackground([Ljava/lang/Object;)Ljava/lang/Void;", body
                );
        MethodAnalyzer methodSignature = new MethodAnalyzer();
        System.out.println(methodSignature.smali2Java(".method public varargs doInBackground([Ljava/lang/Object;)Ljava/lang/Void;"));
        System.out.println(methodSignature.smali2Java(".method public bridge synthetic doInBackground([Ljava/lang/Object;)Ljava/lang/Object;"));
    }

    /**
     * Get all Java method signature of a smali file
     *
     * @param path file path of the smali source file to be analyzed
     * @return all method signature in Java
     */
    public List<String> analyzerFile(String path) {
        List<String> smaliMethods = smaliMethodReader(path);
        return getJavaMethod(smaliMethods);
    }

    /**
     * read the smali source file and extract all .method lines
     * @param path the smali source file path
     * @return
     */
    private static List<String> smaliMethodReader(String path) {
        File file = new File(path);
        Scanner scanner = null;
        List<String> smaliMethods = new ArrayList<>();
        try {
            scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.startsWith(".method")) {
                    smaliMethods.add(line);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }
        return smaliMethods;
    }

    private List<String> getJavaMethod(List<String> smaliMethods) {
        List<String> javaMethods = new ArrayList<>();
        for (String smaliMethod : smaliMethods) {
            javaMethods.add(smali2Java(smaliMethod));
        }
        return javaMethods;
    }

    /**
     * Turn a smali method signature into Java method signature
     *
     * @test pass
     * @param smaliSignature smali method signature
     * @return Java method signature
     */
    private static String smali2Java(String smaliSignature) {
        smaliSignature = smaliSignature.strip();
        Matcher matcher = methodPattern.matcher(smaliSignature);

        if (matcher.find()) {
            String accessModifier = matcher.group(2); // access?
            String staticModifier = matcher.group(7); // static?
            String varargs = matcher.group(9); // varargs?
            String methodName = matcher.group(10); // name
            String parameters = matcher.group(11); // params?
            String returnType = matcher.group(12); // ret type

            accessModifier = accessModifier == null ? "default" : accessModifier.strip();
            staticModifier = staticModifier == null ? "instance" : staticModifier.strip();
            returnType = TypeMap.getType(returnType).strip();

            List<String> parametersList = getParameters(parameters);
            if (varargs != null) {
                parametersList.set(parametersList.size() - 1, parametersList.getLast() + "...");
            }

            JavaMethodSignature signature = new JavaMethodSignature(
                    methodName, accessModifier, staticModifier, parametersList, returnType);
            return signature.toString();
        }
        return "[ERROR] fail to analyze signature: \"" + smaliSignature + "\"";
    }

    /**
     * Turn a line of smali parameters into a list of java parameters
     *
     * @test pass
     * @param parameters a line of smali parameter list string
     * @return a list of java type parameters
     */
    private static List<String> getParameters(String parameters) {
        List<String> parametersList = new ArrayList<>();
        String[] parameterArray = parameters.split("[;]");
        for (String parameter : parameterArray) {
            parametersList.add(TypeMap.getType(parameter.trim()));
        }
        return parametersList;
    }

    private static class JavaMethodSignature {
        private String methodName;
        private String accessModifier;
        private String staticModifier;
        private List<String> parametersList;
        private String returnType;

        public JavaMethodSignature(String methodName, String accessModifier,
               String staticMofidier, List<String> parametersList, String returnType) {
            this.methodName = methodName;
            this.accessModifier = accessModifier;
            this.staticModifier = staticMofidier;
            this.parametersList = parametersList;
            this.returnType = returnType;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            if (!accessModifier.equals("default")) {
                sb.append(accessModifier).append(" ");
            }
            if (!staticModifier.equals("instance")) {
                sb.append(staticModifier).append(" ");
            }
            sb.append(returnType).append(" ");
            sb.append(methodName).append("(");
            sb.append(listParameters(parametersList)).append(");");
            return sb.toString();
        }
    }

    /**
     * Get parameters list in Java style
     *
     * @test pass
     * @param parametersList
     * @return
     */
    private static String listParameters(List<String> parametersList) {
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
    private static boolean isConstructor(String smaliSignature) {
        return constructorPattern.matcher(smaliSignature).matches();
    }

    private static String smaliConstructorToJavaConstructor(String smaliConstructor) {
        Matcher matcher = constructorPattern.matcher(smaliConstructor);
        StringBuilder sb = new StringBuilder();
        if (matcher.find()) {
            sb.append(matcher.group(2) == null ? "" : matcher.group(2) + " "); // access?
            sb.append(matcher.group(7) == null ? "" : matcher.group(7) + " "); // static?
//            sb.append(className).append("(");
            sb.append(matcher.group(9) == null ? "" : matcher.group(9)).append(")");
            String initType = matcher.group(8); // init type
            String parameters = matcher.group(11); // params?
        }
        return null;
    }
}
