package com.momoyeyu.smali_analyzer.element.instructions;

public enum INSTRUCTION_TYPE {
    DEFAULT,
    CAST, CHECK_CAST, BASIC_CAST,
    CATCH, CATCHALL,
    CONDITION,
    CONST, CONST_STRING, CONST_CLASS,
    EXCEPTION,
    GOTO,
    INSTANCE_OF,
    INVOKE, INVOKE_STATIC, INVOKE_DIRECT, INVOKE_VIRTUAL, INVOKE_SUPER, INVOKE_INTERFACE, INVOKE_CONSTRUCTOR,
    LABEL, LABEL_CONDITION, LABEL_GOTO, LABEL_TRY_START, LABEL_TRY_END,
    MOV, MOV_ARRAY, MOV_PROPERTY,
    NEW, NEW_INSTANCE, NEW_ARRAY,
    OPERATION, BIN_OPERATION, TRI_OPERATION,
    RESULT,
    RETURN, RETURN_VOID,
    SYNCHRONIZED,
    TAG, TAG_END_METHOD,
    THROW;
}