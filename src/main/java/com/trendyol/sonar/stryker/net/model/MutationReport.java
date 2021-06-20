package com.trendyol.sonar.stryker.net.model;

import lombok.Value;

import java.util.*;

@Value
public class MutationReport {

    String projectRoot;
    Map<String, File> files = new HashMap<>();

}
