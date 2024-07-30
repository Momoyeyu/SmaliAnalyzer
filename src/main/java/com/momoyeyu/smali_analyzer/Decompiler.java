package com.momoyeyu.smali_analyzer;

import com.momoyeyu.smali_analyzer.element.SmaliProject;
import com.momoyeyu.smali_analyzer.utils.Logger;
import com.momoyeyu.smali_analyzer.utils.PathUtils;

import java.io.FileNotFoundException;

public class Decompiler {

    public static void main(String[] args) {
        String inputDir = PathUtils.selectPath("Please select the input directory", PathUtils.SelectType.LOAD);
        if (inputDir == null) {
            Logger.saveLogs();
            System.exit(1);
        }
        String outputDir = PathUtils.selectPath("Please select the output directory", PathUtils.SelectType.SAVE);
        decompile(inputDir, outputDir);
        Logger.saveLogs();
        System.exit(0);
    }

    public static void decompile(String inputDir, String outputDir) {
        SmaliProject project = SmaliProject.getProject();
        try {
            project.load(inputDir);
        } catch (FileNotFoundException e) {
            Logger.log("[ERROR] " + e.getMessage());
            Logger.saveLogs();
            System.exit(1);
        }
        project.save(outputDir);
    }
}