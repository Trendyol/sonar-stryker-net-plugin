package com.trendyol.sonar.stryker.net;

public final class Constants {
    private Constants() {
    }

    public static final String RULE_REPOSITORY_KEY = "stryker.net.mutation.analysis";
    public static final String RULE_REPOSITORY_NAME = "StrykerNetMutationAnalysis";
    public static final String SURVIVED_MUTANT_RULE_KEY = "mutant.survived";
    public static final String NO_COVERAGE_MUTANT_RULE_KEY = "mutant.lurking";
    public static final String CSHARP = "cs";
    public static final String STRYKER_NET_REPORT_FOLDER_REGEX =
            "[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1]).(2[0-3]|[01][0-9])-[0-5][0-9]-[0-5][0-9]";
    public static final String STRYKER_NET_OUTPUT_DIRECTORY_KEY = "sonar.stryker.net.report.dir";
    public static final String DEFAULT_STRYKER_NET_OUTPUT_DIRECTORY = "tests/StrykerOutput";
}
