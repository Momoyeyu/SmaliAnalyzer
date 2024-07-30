package com.momoyeyu.smali_analyzer.utils;

public class Stepper {

    private int step;

    public Stepper() {
        this.step = 0;
    }

    public int step(int step) {
        this.step += step;
        return this.step;
    }

}
