package com.momoyeyu.smali_analyzer.element;

import com.momoyeyu.smali_analyzer.analyzers.MethodAnalyzer;
import com.momoyeyu.smali_analyzer.element.instructions.*;
import com.momoyeyu.smali_analyzer.entity.RegisterMap;
import com.momoyeyu.smali_analyzer.entity.RegisterTable;
import com.momoyeyu.smali_analyzer.utils.Formatter;
import com.momoyeyu.smali_analyzer.utils.Logger;
import com.momoyeyu.smali_analyzer.utils.TypeUtils;

import java.util.*;

public class SmaliMethod extends SmaliElement {
    protected final List<Instruction> body;
    protected SmaliClass ownerClass;
    protected List<String> parametersList;
    protected List<String> tags;
    protected boolean abstractModifier;
    protected boolean synchronizedModifier;
    private boolean nativeModifier;
    private String annotation;
    private String returnType;

    private final Map<String, String> tryMap = new HashMap<>();

    private RegisterTable registerTable;

    public RegisterTable getRegisterTable() {
        return registerTable;
    }

    public SmaliMethod(String signature) {
        this(signature, null, new ArrayList<>());
    }

    public SmaliMethod(String signature, SmaliClass ownerClass, List<String> instructions) {
        super(signature);
        this.ownerClass = ownerClass;
        this.abstractModifier = false;
        this.synchronizedModifier = false;
        this.body = new ArrayList<>();
        registerTable = new RegisterMap(this);
        for (String instruction : instructions) {
            if (MovArrayInstruction.isArrayMovInstruction(instruction)) {
                body.add(new MovArrayInstruction(instruction, this));
            } else if (InvokeInstruction.isCallInstruction(instruction)) {
                body.add(new InvokeInstruction(instruction, this));
            } else if (ConditionInstruction.isConditionInstruction(instruction)) {
                body.add(new ConditionInstruction(instruction, this));
            } else if (ConstInstruction.isConstInstruction(instruction)) {
                body.add(new ConstInstruction(instruction, this));
            } else if (MovPropertyInstruction.isMovPropertyInstruction(instruction)) {
                body.add(new MovPropertyInstruction(instruction, this));
            } else if (NewInstruction.isNewInstruction(instruction)) {
                body.add(new NewInstruction(instruction, this));
            } else if (ResultInstruction.isResultInstruction(instruction)) {
                body.add(new ResultInstruction(instruction, this));
            } else if (ReturnInstruction.isReturnInstruction(instruction)) {
                body.add(new ReturnInstruction(instruction, this));
            } else if (Label.isLabel(instruction)) {
                body.add(new Label(instruction, this));
            } else if (GotoInstruction.isGoto(instruction)) {
                body.add(new GotoInstruction(instruction, this));
            } else if (Tag.isTag(instruction)) {
                body.add(new Tag(instruction, this));
            } else if (CatchInstruction.isCatchInstruction(instruction)) {
                body.add(new CatchInstruction(instruction, this));
            } else if (ThrowInstruction.isThrowInstruction(instruction)) {
                body.add(new ThrowInstruction(instruction, this));
            } else if (SynchronizedInstruction.isSynchronizedInstruction(instruction)) {
                body.add(new SynchronizedInstruction(instruction, this));
            } else if (ExceptionInstruction.isExceptionInstruction(instruction)) {
                body.add(new ExceptionInstruction(instruction, this));
            } else if (OperationInstruction.isOperationInstruction(instruction)) {
                body.add(new OperationInstruction(instruction, this));
            } else if (CastInstruction.isCastInstruction(instruction)) {
                body.add(new CastInstruction(instruction, this));
            } else if (MovInstruction.isMovInstruction(instruction)) {
                body.add(new MovInstruction(instruction, this));
            } else if (InstanceOfInstruction.isInstanceOfInstruction(instruction)) {
                body.add(new InstanceOfInstruction(instruction, this));
            } else if (ArrayLengthInstruction.isArrayLengthInstruction(instruction)) {
                body.add(new ArrayLengthInstruction(instruction, this));
            } else if (CompareInstruction.isCompareInstruction(instruction)) {
                body.add(new CompareInstruction(instruction, this));
            } else if (NopInstruction.isNopInstruction(instruction)) {
                body.add(new NopInstruction(instruction, this));
            } else if (ArrayData.isArrayData(instruction)) {
                body.add(new ArrayData(instruction, this));
            } else if (FillArrayDataInstruction.isFillArrayDataInstruction(instruction)) {
                body.add(new FillArrayDataInstruction(instruction, this));
            } else if (SwitchInstruction.isSwitchInstruction(instruction)) {
                body.add(new SwitchInstruction(instruction, this));
            } else if (SwitchPack.isSwitchPack(instruction)) {
                body.add(new SwitchPack(instruction, this));
            } else if (LocalInstruction.isLocalInstruction(instruction)) {
                body.add(new LocalInstruction(instruction, this));
            } else {
                body.add(new Instruction(instruction, this));
            }
            this.toJava();
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        try {
            sb.append(this.toJava());
        } catch (RuntimeException e) {
            return Logger.logAnalysisFailure("method", signature);
        }
        registerTable.storeParams();
        if ((ownerClass != null && ownerClass.getClassFileType().equals("interface") && this.body.isEmpty()) || nativeModifier) {
            return sb.append(";").toString();
        }
        sb.append(" {\n");
        INSTRUCTION_TYPE lastSubType = INSTRUCTION_TYPE.DEFAULT;
        INSTRUCTION_TYPE lastType = INSTRUCTION_TYPE.DEFAULT;
        int indentLevel = 1;
        Stack<Instruction> stack = new Stack<>();
        Stack<String> labelStack = new Stack<>();
        for (Instruction instruction : this.body) {
            INSTRUCTION_TYPE subType = instruction.getSubType();
            INSTRUCTION_TYPE type = instruction.getType();
            instruction.updateTable();
            if (subType == INSTRUCTION_TYPE.INVOKE_CONSTRUCTOR) {
                if (lastSubType == INSTRUCTION_TYPE.NEW_INSTANCE) {
                    sb.append("\t".repeat(indentLevel)).append(stack.pop()).append(" ");
                    String instance = instruction.toString();
                    sb.append(instance.substring(instance.indexOf('='))).append(";\n");
                } else {
                    stack.push(instruction);
                    lastType = type;
                    lastSubType = subType;
                    continue;
                }
            } else if (type == INSTRUCTION_TYPE.INVOKE) {
                if (!instruction.toString().contains("=")) {
                    sb.append("\t".repeat(indentLevel)).append(instruction).append(";\n");
                } else {
                    stack.push(instruction);
                    lastType = type;
                    lastSubType = subType;
                    continue;
                }
            } else if (subType == INSTRUCTION_TYPE.NEW_INSTANCE) {
                stack.push(instruction);
                lastType = type;
                lastSubType = subType;
                continue;
            } else if (type == INSTRUCTION_TYPE.RETURN) {
                sb.append("\t".repeat(indentLevel)).append(instruction).append(";\n");
                lastType = type;
                lastSubType = subType;
                continue;
            } else if (subType == INSTRUCTION_TYPE.TAG_END_METHOD) {
                if (indentLevel > 1) {
                    String tmp = "";
                    if (lastType == INSTRUCTION_TYPE.RETURN) {
                        int idx = sb.lastIndexOf("\t".repeat(indentLevel) + "return");
                        tmp = sb.substring(idx + indentLevel);
                        sb.delete(idx, sb.length());
                    }
                    while (indentLevel > 1) {
                        indentLevel--;
                        sb.append("\t".repeat(indentLevel)).append("}\n");
                    }
                    sb.append("\t".repeat(indentLevel)).append(tmp);
                }
            } else if (subType == INSTRUCTION_TYPE.LABEL_TRY_START) {
                Label label = (Label) instruction;
                sb.append("\t".repeat(indentLevel)).append("try {\n");
                labelStack.push(label.toString());
                indentLevel++;
            } else if (subType == INSTRUCTION_TYPE.LABEL_TRY_END) {
                String start = Objects.requireNonNullElse(tryMap.get(labelStack.peek()), "");
                if (start.equals(instruction.toString())) {
                    labelStack.pop();
                    indentLevel--;
                    sb.append("\t".repeat(indentLevel)).append("}\n");
                    lastSubType = INSTRUCTION_TYPE.LABEL_TRY_END;
                    lastType = INSTRUCTION_TYPE.LABEL;
                    continue;
                }
            } else if (Instruction.equalType(type, INSTRUCTION_TYPE.CONDITION)) {
                ConditionInstruction condition = (ConditionInstruction) instruction;
                sb.append("\t".repeat(indentLevel)).append(condition.reverseCondition()).append(" {\n");
                labelStack.push(condition.getConditionLabel());
                indentLevel++;
            } else if (Instruction.equalType(subType, INSTRUCTION_TYPE.LABEL_CONDITION)) {
                if (!labelStack.isEmpty() && labelStack.peek().equals(instruction.toString())) {
                    labelStack.pop();
                    indentLevel--;
                    sb.append("\t".repeat(indentLevel)).append("}\n");
                }
            } else if (subType == INSTRUCTION_TYPE.RESULT && lastType == INSTRUCTION_TYPE.INVOKE) {
                InvokeInstruction invokeInstruction = (InvokeInstruction) stack.pop();
                ((ResultInstruction) instruction).setResultType(invokeInstruction.getReturnType());
                instruction.updateTable();
                sb.append("\t".repeat(indentLevel)).append(Formatter.replacePattern(
                        invokeInstruction.toString(),
                        "(.*?) ret = (.*)",
                        "$1 " + instruction + " $2")).append(";\n");
            } else if (Instruction.equalType(type, INSTRUCTION_TYPE.DEFAULT)) {
                sb.append("\t".repeat(indentLevel)).append(instruction).append("\n");
            } else if (Instruction.equalType(type, INSTRUCTION_TYPE.TAG,
                    INSTRUCTION_TYPE.SYNCHRONIZED, INSTRUCTION_TYPE.NOP,
                    INSTRUCTION_TYPE.ARRAT_DATA, INSTRUCTION_TYPE.SWITCH_PACK)) {
                continue;
            } else { // other
                sb.append("\t".repeat(indentLevel)).append(instruction).append(";\n");
            }
            lastSubType = INSTRUCTION_TYPE.DEFAULT;
            lastType = INSTRUCTION_TYPE.DEFAULT;
        }
        sb.append("}\n");
        return sb.toString();
    }

    @Override
    public String toJava() {
        if (!analyzed) {
            try {
                MethodAnalyzer.analyze(this);
            } catch (Exception e) {
                analyzed = true; // analyzed, but fail
                String info = null;
                if (ownerClass != null)
                    info = "error localtion:\n\tsource: " + ownerClass.getSource() + "\n\tclass: " + ownerClass.getSignature();
                return Logger.logAnalysisFailure("method signature",
                        signature + "\n\tannotation: " + annotation, info);
            }
        }
        StringBuilder sb = new StringBuilder();
        if (tags != null) {
            for (String tag : tags) {
                if (!tag.endsWith("method"))
                    sb.append(tag).append('\n');
            }
        }
        if (!accessModifier.equals("default"))
            sb.append(accessModifier).append(" ");
        if (staticModifier)
            sb.append("static ");
        if (finalModifier)
            sb.append("final ");
        if (nativeModifier)
            sb.append("native ");
        if (synchronizedModifier)
            sb.append("synchronizedModifier ");
        if (abstractModifier)
            sb.append("abstract ");
        sb.append(TypeUtils.getNameFromJava(returnType));
        sb.append(" ").append(name).append("(");
        sb.append(listParameters(parametersList)).append(")");
        return sb.toString();
    }

    // getter
    /**
     * Return a list of method's parameters (type only)
     * @return parameters list
     */
    public List<String> getParametersList() {
        return parametersList;
    }

    public String getParameter(int index) {
        if (index >= parametersList.size())
            return null;
        return parametersList.get(index);
    }

    public String getAnnotation() {
        return annotation;
    }

    public List<String> getTags() {
        return tags;
    }

    public String getOwnerClassType() {
        return ownerClass.getClassType();
    }

    // setter
    public void setParametersList(List<String> parametersList) {
        this.parametersList = parametersList;
    }

    public void alterParameter(int index, String param) {
        if (index >= parametersList.size())
            return;
        parametersList.set(index, param);
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public void setNativeModifier(String nativeModifier) {
        this.nativeModifier = nativeModifier != null;
    }

    public void setNativeModifier(boolean nativeModifier) {
        this.nativeModifier = nativeModifier;
    }

    public void setAbstractModifier(String abstractModifier) {
        this.abstractModifier = abstractModifier != null;
    }

    public void setAbstractModifier(boolean abstractModifier) {
        this.abstractModifier = abstractModifier;
    }

    public void setSynchronizedModifier(String synchronizedModifier) {
        this.synchronizedModifier = synchronizedModifier != null;
    }

    public void setSynchronizedModifier(boolean synchronizedModifier) {
        this.synchronizedModifier = synchronizedModifier;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    // adder
    public void addTryPeer(String try_start, String try_end) {
        tryMap.put(try_start, try_end);
    }
}
