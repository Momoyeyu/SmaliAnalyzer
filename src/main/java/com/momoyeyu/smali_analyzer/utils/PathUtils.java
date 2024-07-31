package com.momoyeyu.smali_analyzer.utils;

import javax.swing.*;
import java.io.File;

public class PathUtils {

    public static final String DEFAULT = PathUtils.getProjectRoot() + File.separator + "res/data";
    public static final String DEFAULT_LOAD = DEFAULT + "/input";
    public static final String DEFAULT_SAVE = DEFAULT + "/output";
    public static final String DEFAULT_LOG = DEFAULT + "/log";
    /**
     * Get relative path from base to path.
     * @param base base directory
     * @param path target path
     * @return relative path from base to path
     */
    public static String getRelativePath(String base, String path) {
        File baseFile = new File(base);
        File pathFile = new File(path);
        File parentFile = baseFile.getParentFile();
        if (parentFile == null) {
            parentFile = new File(File.separator);
        }
        File basePath = new File(parentFile, baseFile.getName());
        File relativePathFile = basePath.toPath().relativize(pathFile.toPath()).toFile();
        return relativePathFile.getPath();
    }

    public static String getProjectRoot() {
        return System.getProperty("user.dir");
    }

    public static String selectPath(String title) {
        return selectPath(title, SelectType.DEFAULT);
    }

    public static String selectPath(String title, SelectType type) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setDialogTitle(title);
        if (type == SelectType.SAVE) {
            new File(DEFAULT_SAVE).mkdirs();
            fileChooser.setCurrentDirectory(new File(DEFAULT_SAVE));
        } else if (type == SelectType.LOAD) {
            fileChooser.setCurrentDirectory(new File(DEFAULT_LOAD));
        } else {
            fileChooser.setCurrentDirectory(new File(DEFAULT));
        }

        int result = fileChooser.showOpenDialog(new JFrame());

        // 检查用户是否选择了目录
        if (result == JFileChooser.APPROVE_OPTION) {
            // 获取用户选择的目录
            File selectedDirectory = fileChooser.getSelectedFile();
            return selectedDirectory.getAbsolutePath();
        }
        Logger.log("[WARN] User didn't select a directory");
        if (type == SelectType.SAVE)
            return DEFAULT_SAVE;
        return null;
    }

    public static String route2path(String route) {
        return route.replaceAll("\\.", "/");
    }

    public enum SelectType {
        DEFAULT, LOAD, SAVE
    }

}
