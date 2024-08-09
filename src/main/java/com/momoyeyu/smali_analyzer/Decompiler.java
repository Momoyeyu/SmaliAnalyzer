package com.momoyeyu.smali_analyzer;

import com.momoyeyu.smali_analyzer.element.SmaliProject;
import com.momoyeyu.smali_analyzer.utils.Logger;
import com.momoyeyu.smali_analyzer.utils.PathUtils;

import java.io.File;
import java.io.FileNotFoundException;

public class Decompiler {

    public static void main(String[] args) {
        // C:\Users\antiy\Desktop\apks\招商银行实例\CMBMobileBank\smali\androidx\appcompat\widget
        String inputDir = null;
        inputDir = "C:\\Users\\antiy\\Desktop\\apks\\招商银行实例\\CMBMobileBank\\smali\\androidx\\appcompat\\widget";
        if (inputDir == null)
            inputDir = PathUtils.selectPath("Please select the input directory", PathUtils.SelectType.LOAD);
        if (inputDir == null) {
            System.err.println("User stop decompiling");
            System.exit(1);
        }
        Logger.log("[INFO] Input directory: " + inputDir, true);
        String outputDir = null;
//        outputDir = "C:\Users\antiy\Desktop";
        if (outputDir == null)
            outputDir = PathUtils.selectPath("Please select the output directory", PathUtils.SelectType.SAVE);
        if (outputDir == null) {
            System.err.println("User stop decompiling");
            System.exit(1);
        }
        outputDir = outputDir.replace("/", "\\");
        if (outputDir.equals(PathUtils.DEFAULT_SAVE.replace("/", "\\"))) {
            File directory = new File(outputDir);
            cleanOutputDir(directory);
        } else {
            outputDir += "/output";
            if (new File(outputDir).exists()) {
                int i = 1;
                while (new File(outputDir + "(" + i +")").exists()) {
                    i += 1;
                }
                outputDir += "(" + i + ")";
            }
        }
        Logger.log("[INFO] Output directory: " + outputDir, true);
        decompile(inputDir, outputDir);
        Logger.loadTodo();
        Logger.log("[INFO] Total logs: " + Logger.getTotal());
        Logger.saveLogs();
        System.exit(0);
    }

    public static void decompile(String inputDir, String outputDir) {
        SmaliProject project = SmaliProject.getProject();
        try {
            project.load(inputDir);
        } catch (FileNotFoundException e) {
            Logger.logException(e.getMessage());
            Logger.saveLogs();
            System.exit(1);
        }
        project.save(outputDir);
    }

    private static void cleanOutputDir(File directory) {
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    cleanOutputDir(file);
                }
            }
        }
        directory.delete();
    }
}