package com.trendyol.sonar.stryker.net.model;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MutantStatus {

    @SerializedName("NotRun")
    NOT_RUN("NotRun"),

    @SerializedName("Killed")
    KILLED("Killed"),

    @SerializedName("Survived")
    SURVIVED("Survived"),

    @SerializedName("Timeout")
    TIMEOUT("Timeout"),

    @SerializedName("CompileError")
    COMPILE_ERROR("CompileError"),

    @SerializedName("Ignored")
    IGNORED("Ignored"),

    @SerializedName("NoCoverage")
    NO_COVERAGE("NoCoverage");

    private final String value;

}
