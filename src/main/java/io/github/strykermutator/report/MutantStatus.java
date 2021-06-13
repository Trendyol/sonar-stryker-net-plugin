package io.github.strykermutator.report;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MutantStatus {
    NOT_RUN("NotRun"),
    KILLED("Killed"),
    SURVIVED("Survived"),
    TIMEOUT("Timeout"),
    COMPILE_ERROR("CompileError"),
    IGNORED("Ignored"),
    NO_COVERAGE("NoCoverage");

    private final String value;

}
