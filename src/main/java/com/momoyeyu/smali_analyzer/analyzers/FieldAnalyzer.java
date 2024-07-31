package com.momoyeyu.smali_analyzer.analyzers;

import com.momoyeyu.smali_analyzer.element.SmaliField;
import com.momoyeyu.smali_analyzer.utils.Stepper;
import com.momoyeyu.smali_analyzer.utils.TypeUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FieldAnalyzer {

    private static final Pattern fieldPattern = Pattern.compile("\\.field\\s+(((private)|(protected)|(public))\\s+)?((static)\\s+)?((final)\\s+)?((transient)\\s+)?((volatile)\\s+)?((enum)\\s+)?((synthetic)\\s+)?((\\S*):)(\\[)?((\\S+)(\\s+=\\s+(\\S+))?)");
    private static final Pattern annotationPattern = Pattern.compile("\\.annotation\\s+system\\s+Ldalvik\\/annotation\\/Signature;value\\s*=\\s*\\{\\\"\\S+<\\\",\\\"(\\S+)\\\",\\\">;\\\"\\}\\.end\\s+annotation");

    /**
     * Test
     * @param args user input
     */
    public static void main(String[] args) {
//        String[] examples = {
//                ".field static final ATTRIBUTE_ACTIVITY:Ljava/lang/String; = \"activity\"",
//                ".field static final ATTRIBUTE_TIME:[Ljava/lang/String; = \"time\"",
//                ".field static final ATTRIBUTE_WEIGHT:Ljava/lang/String; = \"weight\"",
//                ".field static final DEBUG:Z = false",
//                ".field private static final DEFAULT_ACTIVITY_INFLATION:I = 0x5",
//                ".field final producers:Ljava/util/concurrent/atomic/AtomicReference;",
//        };
//        for (String example : examples) {
//            System.out.println(getSignature(example));
//        }
        SmaliField smaliField = new SmaliField(
                ".field final producers:Ljava/util/concurrent/atomic/AtomicReference;",
                ".annotation system Ldalvik/annotation/Signature;value = {\"Ljava/util/concurrent/atomic/AtomicReference<\",\"[\",\"Lrx/internal/operators/OperatorReplay$InnerProducer;\",\">;\"}.end annotation");
        System.out.println(getSignature(smaliField));
    }

    /**
     * Analyze smaliField's signature and store its Java properties.
     * The translation result will be stored in the param object.
     * @param smaliField smaliField object to be translated
     * @throws RuntimeException field signature mismatch regex.
     */
    public static void analyze(SmaliField smaliField) throws RuntimeException {
        Matcher matcher = fieldPattern.matcher(smaliField.getSignature());
        if (matcher.find()) {
            Stepper stepper = new Stepper();
            smaliField.setAccessModifier(matcher.group(stepper.step(2))); // default?
            smaliField.setStaticModifier(matcher.group(stepper.step(5))); // static?
            smaliField.setFinalModifier(matcher.group(stepper.step(2))); // final?
            smaliField.setTransientModifier(matcher.group(stepper.step(2))); // transient?
            smaliField.setVolatileModifier(matcher.group(stepper.step(2))); // volatile?
            stepper.step(2); // enum
            smaliField.setSyntheticModifier(matcher.group(stepper.step(2)) != null); // synthetic?
            smaliField.setName(matcher.group(stepper.step(2)));
            if (matcher.group(stepper.step(1)) != null) {
                smaliField.setType(TypeUtils.getRoutes(matcher.group(stepper.step(2))) + "[]");
            } else {
                smaliField.setType(TypeUtils.getRoutes(matcher.group(stepper.step(2))));
            }
            smaliField.setValue(matcher.group(stepper.step(2)));
        } else {
            throw new RuntimeException("Unknown field: " + smaliField.getSignature());
        }
        matcher = annotationPattern.matcher(smaliField.getAnnotations());
        if (matcher.find()) {
            String type = smaliField.getType();
            StringBuilder sb = new StringBuilder();
            sb.append(type.endsWith("[]") ? type.substring(0, type.length() - 2) : type).append("<");
            for (String t : matcher.group(1).split("(\",\\s*\")")) {
                sb.append(TypeUtils.getTypeFromSmali(t)).append(", ");
            }
            sb.delete(sb.length() - 2, sb.length()).append(">").append(type.endsWith("[]") ? "[]" : "");
            smaliField.setType(sb.toString());
        }
    }

    /**
     * Return Java style field signature.
     * Only support simple type.
     *
     * @author momoyeyu
     * @param smaliField SmaliMethod Object
     * @return Java style method signature of smaliMethod
     */
    public static String getSignature(SmaliField smaliField) {
        return smaliField.toJava();
    }

    /**
     * Return Java style field signature.
     * Only support simple type.
     * This is the String version for basic testing
     *
     * @author momoyeyu
     * @param smaliField SmaliMethod Object
     * @return Java style method signature of smaliMethod
     */
    public static String getSignature(String smaliField) {
        return getSignature(new SmaliField(smaliField));
    }

}
