package com.momoyeyu.smali_analyzer.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    public static String getLocalDate() {
        return LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    public static void main(String[] args) {
        System.out.println(getLocalDate());
    }
}