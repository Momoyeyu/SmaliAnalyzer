package com.momoyeyu.smali_analyzer.element;

import com.momoyeyu.smali_analyzer.analyzers.MethodAnalyzer;
import com.momoyeyu.smali_analyzer.element.instructions.*;
import com.momoyeyu.smali_analyzer.entity.*;
import com.momoyeyu.smali_analyzer.enumeration.COMMENT;
import com.momoyeyu.smali_analyzer.enumeration.INSTRUCTION_TYPE;
import com.momoyeyu.smali_analyzer.utils.Formatter;
import com.momoyeyu.smali_analyzer.utils.Logger;
import com.momoyeyu.smali_analyzer.utils.TypeUtils;

import java.util.*;

public class SmaliMethod extends SmaliElement {
    protected final List<Instruction> instructions;
    protected SmaliClass ownerClass;
    protected List<String> parametersList;
    protected List<String> tags;
    protected boolean abstractModifier;
    protected boolean synchronizedModifier;
    protected boolean nativeModifier;
    protected String annotation;
    protected String returnType;
    protected IndentationTable indentationTable = new IndentationTable(1);

    protected BlockTable blockTable = new BlockMap(this);
    protected LabelTable labelTable = new LabelMap(this);

    protected final Map<String, String> tryMap = new HashMap<>();

    protected RegisterTable registerTable;

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
        this.instructions = new ArrayList<>();
        registerTable = new RegisterMap(this);
        // generate instructions
        for (String instruction : instructions) {
            if (MovArrayInstruction.isArrayMovInstruction(instruction)) {
                this.instructions.add(new MovArrayInstruction(instruction, this));
            } else if (InvokeInstruction.isCallInstruction(instruction)) {
                this.instructions.add(new InvokeInstruction(instruction, this));
            } else if (ConditionInstruction.isConditionInstruction(instruction)) {
                this.instructions.add(new ConditionInstruction(instruction, this));
            } else if (ConstInstruction.isConstInstruction(instruction)) {
                this.instructions.add(new ConstInstruction(instruction, this));
            } else if (MovPropertyInstruction.isMovPropertyInstruction(instruction)) {
                this.instructions.add(new MovPropertyInstruction(instruction, this));
            } else if (NewInstruction.isNewInstruction(instruction)) {
                this.instructions.add(new NewInstruction(instruction, this));
            } else if (ResultInstruction.isResultInstruction(instruction)) {
                this.instructions.add(new ResultInstruction(instruction, this));
            } else if (ReturnInstruction.isReturnInstruction(instruction)) {
                this.instructions.add(new ReturnInstruction(instruction, this));
            } else if (Label.isLabel(instruction)) {
                this.instructions.add(new Label(instruction, this));
            } else if (GotoInstruction.isGoto(instruction)) {
                this.instructions.add(new GotoInstruction(instruction, this));
            } else if (Tag.isTag(instruction)) {
                this.instructions.add(new Tag(instruction, this));
            } else if (CatchInstruction.isCatchInstruction(instruction)) {
                this.instructions.add(new CatchInstruction(instruction, this));
            } else if (ThrowInstruction.isThrowInstruction(instruction)) {
                this.instructions.add(new ThrowInstruction(instruction, this));
            } else if (SynchronizedInstruction.isSynchronizedInstruction(instruction)) {
                this.instructions.add(new SynchronizedInstruction(instruction, this));
            } else if (ExceptionInstruction.isExceptionInstruction(instruction)) {
                this.instructions.add(new ExceptionInstruction(instruction, this));
            } else if (OperationInstruction.isOperationInstruction(instruction)) {
                this.instructions.add(new OperationInstruction(instruction, this));
            } else if (CastInstruction.isCastInstruction(instruction)) {
                this.instructions.add(new CastInstruction(instruction, this));
            } else if (MovInstruction.isMovInstruction(instruction)) {
                this.instructions.add(new MovInstruction(instruction, this));
            } else if (InstanceOfInstruction.isInstanceOfInstruction(instruction)) {
                this.instructions.add(new InstanceOfInstruction(instruction, this));
            } else if (ArrayLengthInstruction.isArrayLengthInstruction(instruction)) {
                this.instructions.add(new ArrayLengthInstruction(instruction, this));
            } else if (CompareInstruction.isCompareInstruction(instruction)) {
                this.instructions.add(new CompareInstruction(instruction, this));
            } else if (NopInstruction.isNopInstruction(instruction)) {
                this.instructions.add(new NopInstruction(instruction, this));
            } else if (ArrayData.isArrayData(instruction)) {
                this.instructions.add(new ArrayData(instruction, this));
            } else if (FillArrayDataInstruction.isFillArrayDataInstruction(instruction)) {
                this.instructions.add(new FillArrayDataInstruction(instruction, this));
            } else if (SwitchInstruction.isSwitchInstruction(instruction)) {
                this.instructions.add(new SwitchInstruction(instruction, this));
            } else if (SwitchPack.isSwitchPack(instruction)) {
                this.instructions.add(new SwitchPack(instruction, this));
            } else if (LocalInstruction.isLocalInstruction(instruction)) {
                this.instructions.add(new LocalInstruction(instruction, this));
            } else {
                this.instructions.add(new Instruction(instruction, this));
            }
        }
        // blocking
        blocking();
        // analysis
        this.toJava();
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
        if ((ownerClass != null && ownerClass.getClassFileType().equals("interface") && this.instructions.isEmpty()) || nativeModifier) {
            return sb.append(";").toString();
        }
        sb.append(" {\n");
        INSTRUCTION_TYPE lastSubType = INSTRUCTION_TYPE.DEFAULT;
        INSTRUCTION_TYPE lastType = INSTRUCTION_TYPE.DEFAULT;
        COMMENT lastComment = COMMENT.DEFAULT;
        int indentLevel = 1;
        Stack<Instruction> stack = new Stack<>();
        Stack<String> labelStack = new Stack<>();
        for (Instruction instruction : this.instructions) {
            INSTRUCTION_TYPE subType = instruction.getSubType();
            INSTRUCTION_TYPE type = instruction.getType();
            instruction.updateTable();
            COMMENT comment = instruction.getComment();
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
                if (comment == COMMENT.IF) {
                    sb.append("\t".repeat(indentLevel)).append(instruction).append(" {\n");
                    indentLevel++;
                } else if (comment == COMMENT.IF_CONTINUE) {
                    sb.append("\t".repeat(indentLevel)).append(instruction).append(";\n");
                } else if (comment == COMMENT.IF_BREAK) {
                    sb.append("\t".repeat(indentLevel)).append(instruction).append(";\n");
                } else if (comment == COMMENT.END_DO_WHILE) {
                    indentLevel--;
                    sb.append("\t".repeat(indentLevel)).append(instruction).append(";\n");
                }
            } else if (Instruction.equalType(subType, INSTRUCTION_TYPE.LABEL_CONDITION)) {
                if (comment == COMMENT.END_IF) {
                    if (lastComment != COMMENT.ELSE) {
                        indentLevel -= labelTable.getLabel(instruction.toString()).getReferences().size();
                        sb.append("\t".repeat(indentLevel)).append("}\n");
                    }
                } else if (comment == COMMENT.DO_WHILE) {
                    sb.append("\t".repeat(indentLevel)).append("do {\n");
                    indentLevel++;
                } // else ?
            } else if (Instruction.equalType(type, INSTRUCTION_TYPE.GOTO)) {
                if (comment == COMMENT.ELSE) {
                    sb.append("\t".repeat(indentLevel - 1)).append("} else {\n");
                    lastComment = COMMENT.ELSE;
                } else if (comment == COMMENT.CONTINUE) {
                    sb.append("\t".repeat(indentLevel)).append("continue;\n");
                } else if (comment == COMMENT.END_WHILE) {
                    indentLevel--;
                    sb.append("\t".repeat(indentLevel)).append("}\n");
                } // else ?
            } else if (Instruction.equalType(subType, INSTRUCTION_TYPE.LABEL_GOTO)) {
                if (comment == COMMENT.END_ELSE) {
                    indentLevel -= 1;
                    sb.append("\t".repeat(indentLevel)).append("}\n");
                } else if (comment == COMMENT.WHILE) {
                    sb.append("\t".repeat(indentLevel)).append("while (true) {\n");
                    indentLevel++;
                } // else ?
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

    // separate instructions into blocks
    public void blocking() {
        String currentBlockName = "start";
        Block currentBlock = blockTable.newBlock(currentBlockName);
        // separate all instructions into blocks
        for (int line = 0; line < instructions.size(); line++) {
            Instruction instruction = instructions.get(line);
            instruction.setLine(line);
            if (instruction instanceof ReturnInstruction) {
                // add instruction
                currentBlock.addInstruction(instruction);
                // iter until 'label' or 'end method'
                while(++line < instructions.size()) {
                    instruction = instructions.get(line);
                    if (instruction instanceof Label)
                        break;
                }
            }
            if (instruction instanceof Label) {
                // new block name :label
                currentBlockName = instruction.toString();
                labelTable.addLabel(currentBlockName, line, instruction.getSubType());
                // set next block :label
                currentBlock.addNextBlock(currentBlockName);
                currentBlock = blockTable.newBlock(currentBlockName);
                // add instruction
                currentBlock.addInstruction(instruction);
            } else if (instruction instanceof ConditionInstruction) {
                // add instruction
                currentBlock.addInstruction(instruction);
                // new block name not:label
                currentBlockName = ((ConditionInstruction) instruction).getLabel();
                labelTable.useLabel(currentBlockName, line);
                // set next block not:label, :label
                currentBlock.addNextBlock(currentBlockName);
                currentBlockName = blockTable.newName("not" + currentBlockName);
                currentBlock.addNextBlock(currentBlockName);
                currentBlock = blockTable.newBlock(currentBlockName);
            } else if (instruction instanceof GotoInstruction) {
                // add instruction
                currentBlock.addInstruction(instruction);
                // create a new block, name not:label
                currentBlockName = ((GotoInstruction) instruction).getLabel();
                labelTable.useLabel(currentBlockName, line);
                // set next block not:label, :label
                currentBlock.addNextBlock(currentBlockName);
                currentBlockName = blockTable.newName("not" + currentBlockName);
                currentBlock.addNextBlock(currentBlockName);
                currentBlock = blockTable.newBlock(currentBlockName);
            } else {
                // add instruction
                currentBlock.addInstruction(instruction);
            }
        }
        // comment Instructions
        labelTable.arrangeInstruction(instructions);
        // set previous block
        blockTable.computeBlockPath();
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

    public String getSource() {
        if (ownerClass == null)
            return "testing";
        return ownerClass.getSource();
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
