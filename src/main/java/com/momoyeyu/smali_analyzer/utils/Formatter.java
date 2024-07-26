package com.momoyeyu.smali_analyzer.utils;

public class Formatter {

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

}
