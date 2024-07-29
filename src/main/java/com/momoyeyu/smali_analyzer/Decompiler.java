package com.momoyeyu.smali_analyzer;

import com.momoyeyu.smali_analyzer.utils.Logger;
import com.momoyeyu.smali_analyzer.utils.SmaliFileReader;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Decompiler {

    public static void main(String[] args) {
        String inputPath = "C:\\Users\\antiy\\Desktop\\projects\\SmaliAnalyzer\\res\\data\\input\\ActivityChooserModel.smali";
        decompile(inputPath);
    }

    public static void decompile(String inputPath) {
        String outputPath = SmaliFileReader.getOutputPath(inputPath);
        decompile(inputPath, outputPath);
    }

    public static void decompile(String inputPath, String outputPath) {
        if (inputPath == null) {
            throw new NullPointerException("Input path is null");
        }
        if (outputPath == null) {
            outputPath = SmaliFileReader.getOutputPath(inputPath);
        }
        SmaliFileReader smaliFileReader = new SmaliFileReader(inputPath);
        String content = smaliFileReader.getFileClass().toString();
        try (FileWriter writer = new FileWriter(outputPath)) {
            Scanner scanner = new Scanner(content);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                writer.write(line + System.lineSeparator());
            }
        } catch (IOException e) {
            e.printStackTrace();
            Logger.log("[ERROR] IOException occur while decompiling " + inputPath);
        }
        System.out.println("[INFO] Finished Decompiling: " + inputPath);
        System.out.println("[INFO] Result save to: " + outputPath);
        Logger.saveLogs();
    }

}