package com.momoyeyu.smali_analyzer.entity;

import java.util.HashMap;
import java.util.Map;

public class IndentationTable {

    private final Map<String, Integer> indentationTable = new HashMap<>();
    private int offset;

    public IndentationTable(int offset) {
        this.offset = offset;
    }

    public int addIndentationOf(String indentation) {
        if (!indentationTable.containsKey(indentation)) {
            indentationTable.put(indentation, 0);
        }
        changeIndentationOf(indentation, 1);
        return getIndentation();
    }

    public int subIndentationOf(String indentation) {
        changeIndentationOf(indentation, -1);
        return getIndentation();
    }

    public int changeIndentationOf(String indentation, int step) {
        assert indentationTable.containsKey(indentation);
        indentationTable.put(indentation, indentationTable.get(indentation) + step);
        return getIndentation();
    }

    public int resetIndentationOf(String indentation) {
        indentationTable.put(indentation, 0);
        return getIndentation();
    }

    public int getIndentation() {
        if (indentationTable.isEmpty())
            return offset;
        return indentationTable.values().stream().reduce(0, Integer::sum) + offset;
    }

    public int getIndentationOf(String indentation) {
        return indentationTable.get(indentation);
    }

}
