package com.momoyeyu.smali_analyzer.element;

import com.momoyeyu.smali_analyzer.analyzers.FileAnalyzer;
import com.momoyeyu.smali_analyzer.utils.FileTraverser;
import com.momoyeyu.smali_analyzer.utils.Logger;
import com.momoyeyu.smali_analyzer.utils.PathUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class SmaliProject {

    private final Map<String, SmaliFile> files = new HashMap<>(); // <routes, SmaliFile>
    private static final SmaliProject project = new SmaliProject();

    private SmaliProject() {}

    public static SmaliProject getProject() {
        return project;
    }

    public static void addFile(String routes, SmaliFile file) {
        project.files.put(routes, file);
    }

    /**
     * Return SmaliFile object.
     * @param routes file routes that extracted only package routes.
     * @return SmaliFile object
     */
    public static SmaliFile getFile(String routes) {
        if (!project.files.containsKey(routes)) {
            addFile(routes, new SmaliFile(routes));
        }
        return project.files.get(routes);
    }

    public void load(String inputDir) throws FileNotFoundException {
        List<String> routes = FileTraverser.search(inputDir);
        for (String route : routes) {
            new FileAnalyzer(route); // analyze when create
        }
        Logger.log("[INFO] Finished Decompiling Project: " + inputDir);
        Logger.log("[INFO] Total input files: " + routes.size());
    }

    /**
     * Save decompile result at dir.
     * @param saveDir save directory
     */
    public void save(String saveDir) {
        for (SmaliFile smaliFile : files.values()) {
            File file = new File(
                    saveDir + "/" + PathUtils.route2path(smaliFile.getRoutes()) + ".java");
            File parent = file.getParentFile();
            if (!parent.exists()) {
                boolean isDirCreated = parent.mkdirs(); // 创建多级目录
                if (!isDirCreated) {
                    Logger.log("[ERROR] Fail to save: " + file.getAbsolutePath());
                    continue;
                }
            }
            try (FileWriter writer = new FileWriter(file)) {
                Scanner scanner = new Scanner(smaliFile.toString());
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    writer.write(line + System.lineSeparator());
                }
            } catch (IOException e) {
                e.printStackTrace();
                Logger.log("[ERROR] IOException occur while decompiling " + smaliFile.getRoutes());
            }
        }
        Logger.log("[INFO] Total output files: " + files.size());
        Logger.log("[INFO] Result save at: " + saveDir);
    }
}
