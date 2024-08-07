package com.momoyeyu.smali_analyzer.analyzers;

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
public class FileAnalyzer {

    private SmaliFile smaliFile;
    private SmaliClass currentSmaliClass;
    private static Pattern pathPattern = Pattern.compile("((.*)\\\\input\\\\)?((\\S+).smali)");

    public static void main(String[] args) {
        // TODO: test SmaliFileReader
        String inputPath = "C:\\Users\\antiy\\Desktop\\projects\\SmaliAnalyzer\\res\\data\\input\\ActivityChooserModel.smali";
        FileAnalyzer smaliFileReader = new FileAnalyzer(inputPath);
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
     * @param filepath file routes
     */
    public FileAnalyzer(String filepath) {
        this(new File(filepath));
    }

    /**
     * Constructor
     * @param file file to be analyzed
     */
    public FileAnalyzer(File file) {
        SmaliField curSmaliField = null;
        String lastFlag = null;
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
                    String annotation = null;
                    List<String> instructions = new ArrayList<>();
                    // read the whole method
                    while (scanner.hasNextLine()) {
                        line = scanner.nextLine().strip();
                        if (line.isBlank()) continue;
                        if (line.startsWith(".annotation")) {
                            StringBuilder sb = new StringBuilder(line);
                            while (scanner.hasNextLine()) {
                                line = scanner.nextLine().strip();
                                sb.append(line);
                                if (line.startsWith(".end annotation")) {
                                    annotation = sb.toString();
                                    break;
                                }
                            }
                        } else
                            instructions.add(line);
                        if (line.startsWith(".end method")) break;
                    }
                    // add method to the current class
                    if (ConstructorAnalyzer.isConstructor(signature)) {
                        SmaliConstructor constructor = new SmaliConstructor(signature, currentSmaliClass, instructions);
                        constructor.setAnnotation(annotation);
                        currentSmaliClass.addSmaliMethod(constructor);
                    } else {
                        SmaliMethod method = new SmaliMethod(signature, currentSmaliClass, instructions);
                        method.setAnnotation(annotation);
                        currentSmaliClass.addSmaliMethod(method);
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
