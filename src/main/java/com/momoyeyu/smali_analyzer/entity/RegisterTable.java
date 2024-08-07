package com.momoyeyu.smali_analyzer.entity;

public interface RegisterTable {
    public void storeVariable(String register, String name, Variable var);

    public void storeVariable(String instruction);

    public Variable getVariable(String register);

    public String getValue(String domain);

    public void updateVariable(String domain, String value);
}
