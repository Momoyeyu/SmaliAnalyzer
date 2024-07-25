package com.momoyeyu.smali_analyzer.utils;


import com.momoyeyu.smali_analyzer.analyzers.ConstructorAnalyzer;
import com.momoyeyu.smali_analyzer.element.SmaliClass;
import com.momoyeyu.smali_analyzer.element.SmaliConstructor;
import com.momoyeyu.smali_analyzer.element.SmaliMethod;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Scan and analyze smali source file structure to
 * construct a SmaliClass object with well structure.
 *
 * @author momoyeyu
 */
public class SmaliFileReader {

    private SmaliClass smaliClass;
    private SmaliClass currentSmaliClass;

    public SmaliFileReader(String fileName) {
        // create a new class instance
        smaliClass = new SmaliClass();
        File file = new File(fileName);
        Scanner scanner = null;
        try {
            scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().strip();
                // find a class, let currentSmaliClass = this class
                if (line.startsWith(".class")) {
                    if (line.contains("$") || smaliClass.isInit()) {
                        currentSmaliClass = new SmaliClass(line);
                        smaliClass.addSubClass(currentSmaliClass);
                    } else if (!smaliClass.isInit()) {
                        smaliClass.init(line);
                        currentSmaliClass = smaliClass;
                    }
                }
                if (line.startsWith(".super")) {
                    try {
                        currentSmaliClass.setSuperClass(new SmaliClass(line));
                    } catch (RuntimeException e) {
                        e.printStackTrace();
                    }
                }
                if (line.startsWith(".method")) {
                    String signature = line;
                    List<String> body = new ArrayList<>();
                    // read the whole method
                    while (scanner.hasNextLine()) {
                        line = scanner.nextLine().strip();
                        if (line.startsWith(".end method")) {
                            break;
                        }
                        body.add(line);
                    }
                    // add method to the current class
                    if (ConstructorAnalyzer.isConstructor(line)) {
                        currentSmaliClass.addSmaliMethod(new SmaliConstructor(signature, currentSmaliClass, body));
                    } else {
                        currentSmaliClass.addSmaliMethod(new SmaliMethod(signature, currentSmaliClass, body));
                    }
                }
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
