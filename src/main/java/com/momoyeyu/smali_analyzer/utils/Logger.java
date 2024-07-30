package com.momoyeyu.smali_analyzer.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class Logger {

    private static final String logDir = "C:\\Users\\antiy\\Desktop\\projects\\SmaliAnalyzer\\res\\data\\log\\";
    private static final boolean debug = false;

    private static List<String> logs = new ArrayList<String>();

    /**
     * return standard error log
     * @param type element type
     * @param element smali element signature
     * @return error log for analysis failure
     */
    public static String logAnalysisFailure(String type, String element) {
        return "// " + log("[ERROR] Fail to analyze " + type + ": " + element + "\n");
    }

    /**
     * Add a line of log to the Logger.
     * You may call saveLogs(String path) to save logs in a file.
     * @param msg message to log
     */
    public static String log(String msg) {
        logs.add(msg.strip());
        if (debug)
            System.out.println(msg.strip());
        return msg;
    }

    /**
     * Save current logs into a file
     */
    public static void saveLogs() {
        saveLogs(null);
    }

    /**
     * Save current logs into a file
     * @param outputPath output file
     */
    public static void saveLogs(String outputPath) {
        String dateInfo = new Date().toString();
        if (outputPath == null || outputPath.isBlank()) {
            outputPath = logDir + dateInfo.replaceAll("[\\s:]", "-") + ".log";
        }
        StringBuilder sb = new StringBuilder();
        for (String log : logs) {
            sb.append(log).append("\n");
        }
        try (FileWriter writer = new FileWriter(outputPath)) {
            writer.write("[INFO] This log is automatically generated at " + dateInfo + System.lineSeparator());
            Scanner scanner = new Scanner(sb.toString());
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                writer.write(line + System.lineSeparator());
            }
            System.out.println("[INFO] Logs save to: " + outputPath);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            logs.clear();
        }
    }

}
