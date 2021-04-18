package io.github.strykermutator.report;

import lombok.Value;

@Value
public class Position {
    private int line;
    private int column;

}