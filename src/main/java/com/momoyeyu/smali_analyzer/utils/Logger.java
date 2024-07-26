package com.momoyeyu.smali_analyzer.utils;

public class Logger {

    public static String failToAnalyze(String type, String element) {
        StringBuilder sb = new StringBuilder();
        sb.append("// [ERROR] Fail to analyze ").append(type).append(": ").append(element).append("\n");
        return sb.toString();
    }

    public static void log(String msg) {
        // TODO: write WARN and ERROR into log
    }

}
