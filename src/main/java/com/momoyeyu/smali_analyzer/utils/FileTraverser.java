package com.momoyeyu.smali_analyzer.utils;

import com.momoyeyu.smali_analyzer.analyzers.FileAnalyzer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileTraverser {

    private final String base;
    private final List<String> routes = new ArrayList<>();
    private static final FilenameFilter filter = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {
            return name.endsWith(".smali");
        }
    };

    public static void main(String[] args) {
        FileTraverser traverser;
        try {
            traverser = new FileTraverser("C:\\Users\\antiy\\Desktop\\apks\\招商银行实例\\CMBMobileBank");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
        String base = "/path/to/base/dir";
        String path = "/path/to/base/dir/subdir/targetfile.txt";
        String relativePath = PathUtils.getRelativePath(base, path);
        System.out.println("Relative path: " + relativePath);
    }

    /**
     * Automatically search all smali files in the base directory.
     * @param base base directory
     */
    public FileTraverser(String base) throws FileNotFoundException {
        if (base.endsWith("/") || base.endsWith("\\")) {
            base = base.substring(0, base.length() - 1);
        }
        this.base = base;
        search();
    }

    /**
     * Search all smali files in the base dir and sub-dir.
     */
    private void search() throws FileNotFoundException {
        File dir = new File(base);
        if (!dir.exists()) {
            Logger.log("[ERROR] The directory does not exist: " + dir);
            throw new FileNotFoundException("The directory does not exist: " + dir);
        }
        search(dir);
    }

    /**
     * Search all smali files in the base dir and sub-dir.
     * @param dir current base dir
     */
    private void search(File dir) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && filter.accept(file.getParentFile(), file.getName())) {
                    this.routes.add(file.getAbsolutePath());
                }
                else if (file.isDirectory()) {
                    search(file);
                }
            }
        }
    }

    /**
     * Save decompile result at dir.
     * @param dir save directory
     */
    public void save(String dir) {
        if (dir.endsWith("/") || dir.endsWith("\\")) {
            dir = dir.substring(0, dir.length() - 1);
        }
        for (String route : this.routes) {
            File file = new File(dir + File.separator +
                    PathUtils.getRelativePath(base, route.substring(0, route.length() - 5) + "java"));
            File parent = file.getParentFile();
            if (!parent.exists()) {
                boolean isDirCreated = parent.mkdirs(); // 创建多级目录
                if (!isDirCreated) {
                    Logger.log("[ERROR] Fail to save: " + file.getAbsolutePath());
                    continue;
                }
            }
            try (FileWriter writer = new FileWriter(file)) {
                Scanner scanner = new Scanner(decompile(route));
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    writer.write(line + System.lineSeparator());
                }
            } catch (IOException e) {
                e.printStackTrace();
                Logger.log("[ERROR] IOException occur while decompiling " + route);
            }
        }
        Logger.log("[INFO] Finished Decompiling Project: " + base);
        Logger.log("[INFO] Total files: " + routes.size());
        Logger.log("[INFO] Result save at: " + dir);
    }

    /**
     * Decompile a single smali file.
     * @param filepath smali file path
     * @return corresponding Java source code
     */
    private String decompile(String filepath) {
        return new FileAnalyzer(filepath).getFile().toString();
    }

}
