package com.momoyeyu.smali_analyzer.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static com.momoyeyu.smali_analyzer.utils.PathUtils.DEFAULT_LOG;

public class Logger {

    /**
     * Switch of the Logger.
     * Decide weather save logs or not.
     */
    private static final boolean SWITCH = true;

    private static final boolean debug = false;
    private static final boolean trace = false;
    private static String savePath = null;
    private static int total = 0;

    private static List<String> logs = new ArrayList<>();
    private static Set<String> todo = new HashSet<>();

    /**
     * return standard error log
     * @param type element type
     * @param element smali element signature
     * @return error log for analysis failure
     */
    public static String logAnalysisFailure(String type, String element) {
        return "// " + log("[ERROR] Fail to analyze " + type + ": " + element + "\n");
    }

    public static void logTodo(String type, String element) {
        todo.add("[TODO] unknown " + type + ": " + element);
    }

    public static void loadTodo() {
        logs.addAll(todo);
        total += todo.size();
    }

    /**
     * Add a line of log to the Logger.
     * You may call saveLogs(String path) to save logs in a file.
     * @param msg message to log
     */
    public static String log(String msg) {
        return log(msg, debug);
    }

    /**
     * Add a line of log to the Logger.
     * You may call saveLogs(String path) to save logs in a file.
     * @param msg message to log
     */
    public static String log(String msg, boolean print) {
        logs.add(msg.strip());
        total += 1;
        if (logs.size() > 200) {
            saveLogs();
        }
        if (print)
            System.out.println(msg.strip());
        return msg;
    }

    public static void logException(String msg) {
        logException(msg, trace);
    }

    public static void logException(String msg, boolean print) {
        logs.add("[EXCEPTION] " + msg);
        total += 1;
        if (logs.size() > 200) {
            saveLogs();
        }
        if (print)
            System.err.println("[EXCEPTION] " + msg);
    }

    public static int getTotal() {
        return total;
    }

    /**
     * Save current logs into a file
     */
    public static void saveLogs() {
        if (!SWITCH) {
            System.out.println("[WARN] Logger is OFF, logs were not saved. " +
                    "You may turn it on by setting Logger.SWITCH to true.");
            return; // switch off
        }
        if (savePath == null) {
            if (!new File(DEFAULT_LOG).exists())
                new File(DEFAULT_LOG).mkdirs();
            String default_log = DEFAULT_LOG + File.separator + DateUtils.getLocalDate();
            Date date = new Date();
            if (!new File(default_log).exists())
                new File(default_log).mkdirs();
            savePath = default_log + File.separator + date.toString().replaceAll("[\\s:]", "-") + ".log";
            logs.add(0, "[INFO] This log is automatically generated at " + date + System.lineSeparator());
        }
        saveLogs(savePath);
    }

    /**
     * Save current logs into a file
     * @param outputPath output file
     */
    private static void saveLogs(String outputPath) {
        StringBuilder sb = new StringBuilder();
        for (String log : logs) {
            sb.append(log).append("\n");
        }
        try (FileWriter writer = new FileWriter(outputPath, true)) {
            Scanner scanner = new Scanner(sb.toString());
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                writer.write(line + System.lineSeparator());
            }
            System.out.println("[INFO] Logs save to: " + outputPath);
            total += logs.size();
            logs.clear();
        } catch (IOException e) {
            Logger.logException(e.getMessage(), true);
        }
    }

    public static void logMulti(Object[] msgs) {
        logMulti(msgs, debug);
    }

    public static void logMulti(Object[] msgs, boolean print) {
        StringBuilder sb = new StringBuilder();
        for (Object msg : msgs) {
            sb.append('\t').append(msg.toString()).append(System.lineSeparator());
        }
        log(sb.toString(), print);
    }


}
