package io.github.strykermutator.report;

import lombok.Builder;
import lombok.Getter;
import lombok.Value;

@Value
@Getter
@Builder
public class Mutant {
    String mutatorName;
    String replacement;
    Location location;
    MutantStatus status;
    String fileName;
}
