package com.trendyol.sonar.stryker.net.model;

import lombok.*;

@Value
@Getter
@Builder
@Setter
public class Mutant {
    String mutatorName;
    String replacement;
    Location location;
    MutantStatus status;
    String fileName;
}
