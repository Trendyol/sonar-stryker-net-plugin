package com.trendyol.sonar.stryker.net.model;

import lombok.Value;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.TextRange;

@Value
public class Location {
    Position start;
    Position end;

    public TextRange getRange(InputFile file) {
        return file.newRange(getStart().getLinePointer(), getStart().getColumnPointer(), getEnd().getLinePointer(), getEnd().getColumnPointer());
    }
}
