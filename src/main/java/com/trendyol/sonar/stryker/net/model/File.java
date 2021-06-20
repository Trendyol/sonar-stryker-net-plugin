package com.trendyol.sonar.stryker.net.model;

import lombok.Value;

import java.util.ArrayList;
import java.util.List;

@Value
public class File {
    List<Mutant> mutants = new ArrayList<>();
}
