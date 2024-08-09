package com.momoyeyu.smali_analyzer.entity;

public interface RegisterTable {
    /**
     * Create a new variable for later reference.
     * You should only use this method at {@code new} and {@code const} instruction.
     *
     * @param register the register for the new variable
     * @param type the datatype of new variable
     */
    void storeVariable(String register, String type);

    /**
     * Return the name of the variable inside the register.
     * @param register register
     * @return variable's name
     */
    String getVariableName(String register);

    String getVariableType(String register);

    void storeParams();
}
