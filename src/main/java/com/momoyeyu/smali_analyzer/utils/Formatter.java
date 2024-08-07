package com.momoyeyu.smali_analyzer.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Formatter {

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
    }

    public static String replacePattern(String input, String regex, String replacement) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        return matcher.replaceAll(replacement);
    }

    /**
     * Remove space and comment from smali source.
     * @param line a line of smali source code
     * @return smali instruction without space and comment
     */
    public static String getInstruction(String line) {
        line = line.strip();
        return line.substring(0, Math.max(line.indexOf('#'), line.length())).strip();
    }

}
