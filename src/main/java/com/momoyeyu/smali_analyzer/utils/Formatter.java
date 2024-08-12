package com.momoyeyu.smali_analyzer.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Formatter {

    public static void main(String[] args) {
        String example = "public final class HistoricalRecord {\n" +
                "\tpublic final ComponentName activity;\n" +
                "\tpublic final long time;\n" +
                "\tpublic final float weight;\n" +
                "\tpublic HistoricalRecord(ComponentName p1, null p2);\n" +
                "\n" +
                "\tpublic HistoricalRecord(String p1, null p2);\n" +
                "\n" +
                "\tpublic boolean equals(Object p1);\n" +
                "\n" +
                "\tpublic int hashCode();\n" +
                "\n" +
                "\tpublic  toString();\n" +
                "\n" +
                "}";
        System.out.println(addIndent(example, 0));
        System.out.println(addIndent(example, 1));
        System.out.println(getAnnotation(".annotation system Ldalvik/annotation/Signature;\n" +
                "        value = {\n" +
                "            \"(\",\n" +
                "            \"Landroid/widget/AdapterView<\",\n" +
                "            \"*>;\",\n" +
                "            \"Landroid/view/View;\",\n" +
                "            \"IJ)V\"\n" +
                "        }\n" +
                "    .end annotation"));
    }

    /**
     * Add {@code indent} number of {@code \t} before each line of {@code str}.
     * @param str String with multiple lines
     * @param indent the number of indentation to add before {@code str}
     * @return {@code str} with {@code indent} before
     */
    public static String addIndent(String str, int indent) {
        String[] lines = str.split("\n");
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            if (!line.isBlank()) {
                sb.append("\t".repeat(Math.max(0, indent)));
            }
            sb.append(line).append("\n");
        }
        return sb.toString();
    }

    private static String getAnnotation(String annotation) {
        String[] lines = annotation.split("\n");
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            if (!line.isBlank()) {
                sb.append(line.strip());
            }
        }
        return sb.toString();
    }

    public static String replacePattern(String input, String regex, String replacement) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        return matcher.replaceAll(replacement);
    }

}
