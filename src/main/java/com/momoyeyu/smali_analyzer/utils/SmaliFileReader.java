package com.momoyeyu.smali_analyzer.utils;

import com.momoyeyu.smali_analyzer.analyzers.ClassAnalyzer;
import com.momoyeyu.smali_analyzer.analyzers.ConstructorAnalyzer;
import com.momoyeyu.smali_analyzer.element.*;
import com.momoyeyu.smali_analyzer.repository.ClassRepository;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Scan and analyze smali source file structure to
 * construct a SmaliClass object with well structure.
 *
 * @author momoyeyu
 */
public class SmaliFileReader {

    private SmaliFile smaliFile;
    private SmaliClass currentSmaliClass;
    private static Pattern pathPattern = Pattern.compile("((.*)\\\\input\\\\)?((\\S+).smali)");

    public static void main(String[] args) {
        // TODO: test SmaliFileReader
        String inputPath = "C:\\Users\\antiy\\Desktop\\projects\\SmaliAnalyzer\\res\\data\\input\\ActivityChooserModel.smali";
        SmaliFileReader smaliFileReader = new SmaliFileReader(inputPath);
        System.out.println(smaliFileReader.smaliFile.toString());
    }

    public static String getOutputPath(String inputPath) {
        Matcher matcher = pathPattern.matcher(inputPath);
        StringBuffer sb = new StringBuffer();
        if (matcher.find()) {
            sb.append(matcher.group(2)).append("\\output\\");
            sb.append(matcher.group(4)).append(".java");
            return sb.toString();
        }
        return null;
    }

    public SmaliFile getFile() {
        return smaliFile;
    }

    /**
     * Constructor
     * @param routes file routes
     */
    public SmaliFileReader(String routes) {
        SmaliField curSmaliField = null;
        String lastFlag = null;
        File file = new File(routes);
        Scanner scanner = null;
        try {
            scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().strip();
                if (lastFlag != null && lastFlag.equals(".field") && curSmaliField != null) {
                    if (line.startsWith(".annotation")) {
                        curSmaliField.addAnnotation(line);
                        while (scanner.hasNextLine()) {
                            line = scanner.nextLine().strip();
                            curSmaliField.addAnnotation(line);
                            if (line.startsWith(".end annotation")) break;
                        }
                    } else {
                        curSmaliField = null;
                    }
                }
                if (line.isEmpty() || line.startsWith("#")) continue;
                if (line.startsWith(".class")) {
                    currentSmaliClass = new SmaliClass(line);
                    if (smaliFile == null) {
                        smaliFile = SmaliProject.getFile(ClassAnalyzer.getRoutes(currentSmaliClass));
                    }
                    smaliFile.addClass(currentSmaliClass);
                    ClassRepository.addClass(currentSmaliClass);
                    line = scanner.nextLine().strip();
                    if (line.startsWith(".super")) {
                        try {
                            currentSmaliClass.setSuperClass(ClassRepository.getClass(line));
                        } catch (RuntimeException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (line.startsWith(".method")) {
                    String signature = line;
                    List<String> body = new ArrayList<>();
                    // read the whole method
                    while (scanner.hasNextLine()) {
                        line = scanner.nextLine().strip();
                        if (line.isBlank()) continue;
                        body.add(line);
                        if (line.startsWith(".end method")) break;
                    }
                    // add method to the current class
                    if (ConstructorAnalyzer.isConstructor(signature)) {
                        currentSmaliClass.addSmaliMethod(new SmaliConstructor(signature, currentSmaliClass, body));
                    } else {
                        currentSmaliClass.addSmaliMethod(new SmaliMethod(signature, currentSmaliClass, body));
                    }
                }
                if (line.startsWith(".field")) {
                    curSmaliField = new SmaliField(line);
                    currentSmaliClass.addSmaliField(curSmaliField);
                }
                lastFlag = line.split(" ")[0];
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }
    }
}
