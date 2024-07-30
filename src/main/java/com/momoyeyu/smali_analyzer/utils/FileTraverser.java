package com.momoyeyu.smali_analyzer.utils;

import com.momoyeyu.smali_analyzer.analyzers.FileAnalyzer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

public class FileTraverser {

    private static final FilenameFilter filter = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {
            return name.endsWith(".smali");
        }
    };

    public static void main(String[] args) {
        String base = "/path/to/base/dir";
        String path = "/path/to/base/dir/subdir/targetfile.txt";
        String relativePath = PathUtils.getRelativePath(base, path);
        System.out.println("Relative path: " + relativePath);
    }

    /**
     * Search all smali files in the base directory.
     * @param base base directory
     */
    public static List<String> search(String base) throws FileNotFoundException {
        File dir = new File(base);
        if (!dir.exists()) {
            Logger.log("[ERROR] The directory does not exist: " + dir);
            throw new FileNotFoundException("The directory does not exist: " + dir);
        }
        return search(dir);
    }

    /**
     * Search all smali files in the base dir and sub-dir.
     * @param dir current base dir
     */
    private static List<String> search(File dir) {
        List<String> routes = new ArrayList<>();
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && filter.accept(file.getParentFile(), file.getName())) {
                    routes.add(file.getAbsolutePath());
                }
                else if (file.isDirectory()) {
                    routes.addAll(search(file));
                }
            }
        }
        return routes;
    }

    private static String getOutputPath(String base, String path) {
        File baseFile = new File(base);
        return PathUtils.getRelativePath(
                baseFile.getParentFile().getAbsolutePath(), path.substring(0, path.length() - 5)) + "java";
    }

    private void analyze(String filepath) {
        new FileAnalyzer(filepath);
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
