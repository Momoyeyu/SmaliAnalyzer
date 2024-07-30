package com.momoyeyu.smali_analyzer;

import com.momoyeyu.smali_analyzer.utils.FileTraverser;
import com.momoyeyu.smali_analyzer.utils.Logger;
import com.momoyeyu.smali_analyzer.utils.PathUtils;

import java.io.File;
import java.io.IOException;

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
        File inputDirFile = new File(inputDir);
        if (!inputDirFile.exists()) {
            Logger.log("[ERROR] Input directory does not exist: " + inputDir);
            throw new RuntimeException("Input directory does not exist: " + inputDir);
        }
        FileTraverser project;
        try {
            project = new FileTraverser(inputDir);
            project.save(outputDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}