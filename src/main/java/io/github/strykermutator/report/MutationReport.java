package io.github.strykermutator.report;

import lombok.Value;

import java.util.HashMap;
import java.util.Map;

@Value
public class MutationReport {

    String projectRoot;
    Map<String, File> files = new HashMap<>();

}
