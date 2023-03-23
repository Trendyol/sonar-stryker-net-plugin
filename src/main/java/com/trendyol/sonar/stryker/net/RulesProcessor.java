package com.trendyol.sonar.stryker.net;

import com.trendyol.sonar.stryker.net.model.Mutant;
import com.trendyol.sonar.stryker.net.model.MutantStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sonar.api.batch.fs.FilePredicate;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.rule.ActiveRule;
import org.sonar.api.batch.rule.ActiveRules;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.issue.NewIssue;
import org.sonar.api.batch.sensor.issue.NewIssueLocation;
import org.sonar.api.rule.RuleKey;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
public class RulesProcessor {
    private final ActiveRules rulesProfile;
    private final FileSystem fileSystem;

    public void processRules(SensorContext context, List<Mutant> mutants) throws IllegalStateException, IOException {
        final Collection<ActiveRule> activeRules = this.rulesProfile.findByRepository(Constants.RULE_REPOSITORY_KEY);

        if (activeRules.isEmpty()) {
            throw new IllegalStateException("At least one Mutation Analysis rule needs to be activated in the current profile.");
        }
        applyRules(mutants, context);
    }


    private void applyRules(List<Mutant> mutants, SensorContext context) throws IOException {
        log.info("Processing {} mutant(s).", mutants.size());

        applyRule(mutants, context, MutantStatus.SURVIVED, Constants.SURVIVED_MUTANT_RULE_KEY);
        applyRule(mutants, context, MutantStatus.NO_COVERAGE, Constants.NO_COVERAGE_MUTANT_RULE_KEY);
    }

    private void applyRule(List<Mutant> mutants, SensorContext context, MutantStatus targetStatus, String ruleKey) throws IOException {
        int count = 0;
        for (Mutant mutant : mutants) {
            final String status = mutant.getStatus().getValue();
            if (status.equals(targetStatus.getValue())) {
                count++;
                InputFile inputFile = locateSourceFile(mutant.getAbsoluteFilePath());

                NewIssue issue = context.newIssue()
                        .forRule(RuleKey.of(Constants.RULE_REPOSITORY_KEY, ruleKey));

                NewIssueLocation location = issue.newLocation()
                        .on(inputFile)
                        .at(mutant.getLocation().getRange(inputFile))
                        .message(formatIssueMessage(mutant));

                issue.at(location);
                issue.save();
            }
        }
        log.info("Reported {} issue(s) as {}.", count, targetStatus);
    }

    private String formatIssueMessage(Mutant mutant) {
        return String.format("[%s]: %s",
                mutant.getMutatorName(),
                mutant.getReplacement()
                        .replace("\r\n", "").replace("\n", ""));
    }

    private InputFile locateSourceFile(String sourceFilePath) throws IOException {
        String relativeSourceFilePath = makeRelativePath(sourceFilePath, fileSystem.baseDir());
        FilePredicate filePredicate = fileSystem.predicates().matchesPathPattern("**/" + relativeSourceFilePath);
        InputFile inputFile = fileSystem.inputFile(filePredicate);
        if (Objects.isNull(inputFile)) {
            log.warn("Could not find input file {} in {}", relativeSourceFilePath, fileSystem.baseDir().getAbsolutePath());
        }
        return inputFile;
    }

    private String makeRelativePath(String sourceFilePath, File baseDir) throws IOException {
        File sourceFile = new File(sourceFilePath);
        // Users/mustafa.yumurtaci/workspace/trendyol/test-service/src/Domain/Global/ConfigConstants.cs to
        // src/Domain/Global/ConfigConstants.cs

        if (sourceFile.getCanonicalPath().startsWith(baseDir.getCanonicalPath())) {
            sourceFilePath = sourceFile.getCanonicalPath().substring(baseDir.getCanonicalPath().length() + 1);
        }
        return sourceFilePath;
    }
}
