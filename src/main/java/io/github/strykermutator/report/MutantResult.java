package io.github.strykermutator.report;

import lombok.Value;

import java.util.List;

@Value
public class MutantResult {
    private String fileName;
    private String mutatorName;
    private MutantStatus status;
    private String replacement;
    private Location location;
    private List<Integer> range;
}
