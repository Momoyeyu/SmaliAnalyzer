package com.momoyeyu.smali_analyzer.analyzers;

import com.momoyeyu.smali_analyzer.element.SmaliField;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FieldAnalyzer extends Analyzer {

    private static Pattern fieldPattern = Pattern.compile("\\.field\\s+(((private)|(protected)|(public))\\s+)?((static)\\s+)?((final)\\s+)?(\\S*:)(\\[)?((\\S+)(\\s+=\\s+(\\S+))?)");

    public static void translate(SmaliField smaliField) {
        Matcher matcher = fieldPattern.matcher(smaliField.toString());
        while (matcher.find()) {
            smaliField.setAccessModifier(matcher.group(2) == null ? "default" : matcher.group(2)); // default?
            smaliField.setStaticModifier(matcher.group(7)); // static?
            smaliField.setStaticModifier(matcher.group(7)); // final?
        }
    }

}
