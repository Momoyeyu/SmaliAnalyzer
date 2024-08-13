package com.momoyeyu.smali_analyzer.analyzers;

import com.momoyeyu.smali_analyzer.element.SmaliConstructor;
import com.momoyeyu.smali_analyzer.element.SmaliMethod;
import com.momoyeyu.smali_analyzer.utils.Stepper;
import com.momoyeyu.smali_analyzer.utils.TypeUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MethodAnalyzer {
    private static final Pattern systemAnnotationPattern = Pattern.compile("^\\.annotation\\s+system\\s+Ldalvik\\/annotation\\/Signature;value\\s*=\\s*\\{\"\\(\",\"(\\S+)\\)(\\S+)\"\\}\\.end\\s+annotation");
    private static final Pattern tagAnnotationPattern = Pattern.compile("^\\.annotation\\s+\\S+\\s+(\\S+);?\\.end\\s+annotation\\s+(\\S+)");
    private static final Pattern methodPattern = Pattern.compile("\\.method\\s+((private|protected|public)\\s+)?((static)\\s+)?((final)\\s+)?((declared-synchronized|synchronized)\\s+)?((bridge)\\s+)?((varargs)\\s+)?((native)\\s+)?((abstract)\\s+)?((synthetic)\\s+)?(\\S+)\\((.*?)\\)([a-zA-Z/\\[]++);?");

    /**
     * Test
     * @param args user input
     */
    public static void main(String[] args) {
        SmaliMethod smaliMethod = new SmaliMethod(".method public sort(Landroid/content/Intent;Ljava/util/List;Ljava/util/List;)V");
        smaliMethod.setAnnotation(".annotation system Ldalvik/annotation/Signature;value = {\"(\",\"Landroid/content/Intent;\",\"Ljava/util/List<\",\"Landroidx/appcompat/widget/ActivityChooserModel$ActivityResolveInfo;\",\">;\",\"Ljava/util/List<\",\"Landroidx/appcompat/widget/ActivityChooserModel$HistoricalRecord;\",\">;)V\"}.end annotation");
        SmaliMethod smaliMethod2 = new SmaliMethod(".method public onItemClick(Landroid/widget/AdapterView;Landroid/view/View;IJ)V");
        smaliMethod2.setAnnotation(".annotation system Ldalvik/annotation/Signature;value = {\"(\",\"Landroid/widget/AdapterView<\",\"*>;\",\"Landroid/view/View;\",\"IJ)V\"}.end annotation");
        SmaliMethod smaliMethod3 = new SmaliMethod(".method private addCustomViewsWithGravity(Ljava/util/List;I)V");
        smaliMethod3.setAnnotation(".annotation system Ldalvik/annotation/Signature;value = {\"(\",\"Ljava/util/List<\",\"Landroid/view/View;\",\">;I)V\"}.end annotation");
        System.out.println(smaliMethod.toJava());
        System.out.println(smaliMethod2.toJava());
        System.out.println(smaliMethod3.toJava());
        System.out.println(getSignature(".method public varargs doInBackground([Ljava/lang/Object;)Ljava/lang/Void;"));
        System.out.println(getSignature(".method public abstract setActivityChooserModel(Landroidx/appcompat/widget/ActivityChooserModel;)V;"));
        System.out.println(getSignature(".method public static get(Landroid/content/Context;Ljava/lang/String;I)Landroidx/appcompat/widget/ActivityChooserModel;"));
        System.out.println(getSignature(".method public onItemSelected(Landroid/widget/AdapterView;Landroid/view/View;IJ)V"));
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
            smaliMethod.setStaticModifier(matcher.group(stepper.step(2))); // static?
            smaliMethod.setFinalModifier(matcher.group(stepper.step(2))); // final?
            smaliMethod.setSynchronizedModifier(matcher.group(stepper.step(2))); // synchronized?
            stepper.step(2); // bridge?
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
        String annotation = smaliMethod.getAnnotation();
        if (annotation != null) {
            matcher = systemAnnotationPattern.matcher(annotation);
            if (matcher.matches()) {
                List<String> generic = splitGeneric(matcher.group(1));
                List<String> params = smaliMethod.getParametersList();
                smaliMethod.setParametersList(analyzeAnnotations(params, generic));
            }
            smaliMethod.setAnnotation("[DONE] " + annotation);
        }
        List<String> tags = smaliMethod.getTags();
        if (tags != null && !tags.isEmpty()) {
            List<String> newTags = new ArrayList<>();
            for (String tag : tags) {
                matcher = tagAnnotationPattern.matcher(tag);
                if (matcher.matches()) {
                    String newTag = "@" + TypeUtils.getNameFromSmali(matcher.group(1));
                    String tagType = matcher.group(2);
                    if (tagType.equals("method"))
                        newTags.add(newTag);
                    else {
                        int idx = Integer.parseInt(tagType.substring(1)) -
                                (smaliMethod.isStaticModifier() ? 0 : 1);
                        smaliMethod.alterParameter(idx, newTag + " " + smaliMethod.getParameter(idx));
                    }
                }
            }
            smaliMethod.setTags(newTags);
        }
    }

    private static List<String> analyzeAnnotations(List<String> params, List<String> genericParams) {
        List<String> result = new ArrayList<>();
        int i = 0;
        for (String type : params) {
            String tmp = genericParams.get(i++);
            if (tmp.endsWith("<")) {
                List<String> generic = new ArrayList<>();
                int count = 0;
                while (true) {
                    tmp = genericParams.get(i++);
                    if (tmp.endsWith("<"))
                        count++;
                    if (tmp.endsWith(">;")) {
                        if (count == 0) break;
                        count--;
                    }
                    generic.add(tmp);
                } // end while
                result.add(FieldAnalyzer.analyzeAnnotations(type, generic));
            } else {
                result.add(type);
            }
        }
        return result;
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
            sb.append(String.format("%s arg%d, ", TypeUtils.getNameFromJava(parametersList.get(idx)), isStatic ? idx : idx + 1));
        }
        return sb.delete(sb.length() - 2, sb.length()).toString();
    }

    public static String listArguments(List<String> arguments, boolean isStatic) {
        if (arguments == null || arguments.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        int offset = isStatic ? 0 : 1;
        for (int idx = offset; idx < arguments.size(); idx++) {
            sb.append(arguments.get(idx)).append(", ");
        }
        return sb.delete(Math.max(sb.length() - 2, 0), sb.length()).toString();
    }

    public static List<String> splitGeneric(String generic) {
        List<String> ret = new ArrayList<>();
        if (generic == null || generic.isEmpty() || generic.equals("*"))
            return ret;
        String tmp;
        for (String s : Arrays.stream(generic.split("(\",\\s*\")")).toList()) {
            if (s.contains(">;") && s.length() > 2) {
                int idx = s.indexOf(">;");
                ret.addAll(splitGeneric(s.substring(0, idx)));
                ret.add(">;");
                ret.addAll(splitGeneric(s.substring(idx + 2)));
            } else if (TypeUtils.isSmaliBasicType(s.substring(0, 1)))
                ret.addAll(TypeUtils.splitSmaliParameters(s));
            else
                ret.add(s);
        }
        return ret;
    }
}
