package io.github.strykermutator.report;

import lombok.Value;

import java.util.ArrayList;
import java.util.List;

@Value
public class File {
    List<MutantResult> mutants = new ArrayList<>();
}
