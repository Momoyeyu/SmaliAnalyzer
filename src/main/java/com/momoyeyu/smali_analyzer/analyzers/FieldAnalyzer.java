package com.momoyeyu.smali_analyzer.analyzers;

import com.momoyeyu.smali_analyzer.element.SmaliField;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FieldAnalyzer {

    private static Pattern fieldPattern = Pattern.compile("\\.field\\s+(((private)|(protected)|(public))\\s+)?((static)\\s+)?((final)\\s+)?((synthetic)\\s+)?((\\S*):)(\\[)?((\\S+)(\\s+=\\s+(\\S+))?)");

    public static void main(String[] args) {
        String[] examples = {
                ".field static final ATTRIBUTE_ACTIVITY:Ljava/lang/String; = \"activity\"",
                ".field static final ATTRIBUTE_TIME:[Ljava/lang/String; = \"time\"",
                ".field static final ATTRIBUTE_WEIGHT:Ljava/lang/String; = \"weight\"",
                ".field static final DEBUG:Z = false",
                ".field private static final DEFAULT_ACTIVITY_INFLATION:I = 0x5",
        };
        for (String example : examples) {
            System.out.println(getSignature(example));
        }
    }

    public static void translate(SmaliField smaliField) throws RuntimeException {
        Matcher matcher = fieldPattern.matcher(smaliField.getSignature());
        if (matcher.find()) {
            smaliField.setAccessModifier(matcher.group(2)); // default?
            smaliField.setStaticModifier(matcher.group(7)); // static?
            smaliField.setFinalModifier(matcher.group(9)); // final?
            smaliField.setSynthetic(matcher.group(11) != null); // synthetic?
            smaliField.setName(matcher.group(13));
            if (matcher.group(14) != null) {
                smaliField.setArrayFlag(true);
            }
            smaliField.setType(matcher.group(16));
            smaliField.setValue(matcher.group(18));
        } else {
            throw new RuntimeException("[ERROR] Invalid field: " + smaliField.getSignature());
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
