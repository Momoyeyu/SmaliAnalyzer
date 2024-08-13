package com.momoyeyu.smali_analyzer.element.instructions;

import com.momoyeyu.smali_analyzer.element.SmaliMethod;
import com.momoyeyu.smali_analyzer.enumeration.INSTRUCTION_TYPE;
import com.momoyeyu.smali_analyzer.utils.TypeUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CatchInstruction extends Instruction{

    private static final Pattern catchallPattern = Pattern.compile(
            "\\.catchall\\s+\\{(:\\S+)\\s+\\.\\.\\s+(:\\S+)}\\s+(:catchall_(\\S+))");
    private static final Pattern catchPattern = Pattern.compile(
            "\\.catch\\s+(\\S+);\\s+\\{(:\\S+)\\s+\\.\\.\\s+(:\\S+)}\\s+(:catch_(\\S+))");

    private String catchType;
    private String exceptionType;
    private String tryStartLabels;
    private String tryEndLabels;
    private String catchLabel;

    public static void main(String[] args) {
        System.out.println(new CatchInstruction(".catch Ljava/io/IOException; {:try_start_a .. :try_end_34} :catch_87"));
        System.out.println(new CatchInstruction(".catchall {:try_start_87 .. :try_end_aa} :catchall_85"));
    }

    private CatchInstruction(String instruction) {
        this(instruction, null);
    }

    public CatchInstruction(String instruction, SmaliMethod parentMethod) {
        super(instruction, parentMethod);
        this.analyze();
    }

    @Override
    protected void analyze() {
        if (!analyzed) {
            if (signature.startsWith(".catchall")) {
                Matcher matcher = catchallPattern.matcher(signature);
                if (matcher.matches()) {
                    catchType = ".catchall";
                    tryStartLabels = matcher.group(1);
                    tryEndLabels = matcher.group(2);
                    catchLabel = matcher.group(3);
                    if (parentMethod != null) {
                        parentMethod.addTryPeer(tryStartLabels, tryEndLabels);
                    }
                }
            } else {
                Matcher matcher = catchPattern.matcher(signature);
                if (matcher.matches()) {
                    catchType = ".catch";
                    exceptionType = TypeUtils.getTypeFromSmali(matcher.group(1));
                    tryStartLabels = matcher.group(2);
                    tryEndLabels = matcher.group(3);
                    catchLabel = matcher.group(4);
                }
            }
            super.analyze();
        }
    }

    @Override
    public INSTRUCTION_TYPE getType() {
        return INSTRUCTION_TYPE.CATCH;
    }

    @Override
    public INSTRUCTION_TYPE getSubType() {
        if (catchType.equals(".catchall"))
            return INSTRUCTION_TYPE.CATCHALL;
        return INSTRUCTION_TYPE.CATCH;
    }

    @Override
    public String toString() {
        if (!analyzed)
            return analysisFail("catch");
        StringBuilder sb = new StringBuilder();
        if (catchType.equals(".catchall")) {
            sb.append("catch (Exception e)");
        } else {
            sb.append("catch (" + TypeUtils.getNameFromJava(exceptionType) + " e)");
        }
        return sb.toString();
    }

    // getter
    public String getCatchLabel() {
        return catchLabel;
    }

    public static boolean isCatchInstruction(String instruction) {
        if (instruction == null)
            return false;
        return catchPattern.matcher(instruction).matches() || catchallPattern.matcher(instruction).matches();
    }

}
