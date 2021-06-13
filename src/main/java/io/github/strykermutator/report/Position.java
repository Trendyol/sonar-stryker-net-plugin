package io.github.strykermutator.report;

import lombok.Value;

@Value
public class Position {
    int line;
    int column;

    public int getLinePointer() {
        return this.line;
    }

    public int getColumnPointer() {
        return this.column - 1;
    }
}