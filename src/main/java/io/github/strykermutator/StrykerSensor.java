package io.github.strykermutator;

import io.github.strykermutator.report.MutantResult;
import io.github.strykermutator.report.MutantStatus;
import lombok.extern.slf4j.Slf4j;
import org.sonar.api.batch.fs.FilePredicate;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.rule.ActiveRules;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.batch.sensor.issue.NewIssue;
import org.sonar.api.batch.sensor.issue.NewIssueLocation;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.scanner.sensor.ProjectSensor;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static io.github.strykermutator.StrykerConstants.*;

@Slf4j
public class StrykerSensor implements ProjectSensor {
    private final FileSystem fileSystem;
    private final ActiveRules activeRules;


    public StrykerSensor(FileSystem fileSystem, ActiveRules activeRules) {
        this.fileSystem = fileSystem;
        this.activeRules = activeRules;
    }

    @Override
    public void describe(SensorDescriptor descriptor) {
        descriptor.name("Stryker Mutation testing Sensor");
    }


    @Override
    public void execute(SensorContext context) {

        if (checkIfNecessaryRulesActive()) return;
        try {
            analyzeReportAndCreateIssues(context);
        } catch (IOException e) {
            log.error("Could not read from Stryker event file.", e);
        } catch (RuntimeException runTimeEx) {
            log.error("Something went wrong.", runTimeEx);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean checkIfNecessaryRulesActive() {
        List<String> rules = Arrays.asList(SURVIVED_MUTANT_RULE_KEY, NO_COVERAGE_MUTANT_RULE_KEY);
        return rules.stream().noneMatch(this::isRuleActive);
    }

    private boolean isRuleActive(String ruleKey) {
        return activeRules.find(RuleKey.of(RULE_REPOSITORY_KEY, ruleKey)) != null;
    }

    private void analyzeReportAndCreateIssues(SensorContext context) throws IOException {
        StrykerEventsDirectory strykerEvents = new StrykerEventsDirectory(context, fileSystem);
        Optional<String> reportFile = strykerEvents.readReportFileAsByteString();
        if (reportFile.isPresent()) {
            MutantResultJsonReader reader = new MutantResultJsonReader();
            List<MutantResult> mutantResults = reader.readMutants(reportFile.get());
            createIssues(mutantResults, context);
        } else {
            log.warn("Could not find stryker report, not reporting issues.");
        }
    }

    private void createIssues(List<MutantResult> mutantResults, SensorContext context) throws IOException {
        log.info("Processing {} mutant(s).", mutantResults.size());
        //TODO: NO COVERAGE İÇİNISSUE AÇMAYA GERÇEKTEN GEREK VAR MI? PANELDE NO COVERAGE DİYE BELİRTMİYOR.
        createIssuesForMutants(mutantResults, context, MutantStatus.SURVIVED, SURVIVED_MUTANT_RULE_KEY);
        createIssuesForMutants(mutantResults, context, MutantStatus.NO_COVERAGE, NO_COVERAGE_MUTANT_RULE_KEY);
    }

    private void createIssuesForMutants(List<MutantResult> mutantResults, SensorContext context, MutantStatus targetStatus, String ruleKey) throws IOException {
        int count = 0;
        for (MutantResult mutantResult : mutantResults) {
            final String status = mutantResult.getStatus().getValue();
            if (status == targetStatus.getValue()) {
                count++;
                InputFile inputFile = locateSourceFile(mutantResult.getFileName());

                NewIssue issue = context.newIssue()
                        .forRule(RuleKey.of(RULE_REPOSITORY_KEY, ruleKey));

                NewIssueLocation location = issue.newLocation()
                        .on(inputFile)
                        .at(mutantResult.getLocation().getRange(inputFile))
                        .message(formatIssueMessage(mutantResult));

                issue.at(location);
                issue.save();
            }
        }
        log.info("Reported {} issue(s) as {}.", count, targetStatus);
    }

    private String formatIssueMessage(MutantResult mutantResult) {
        return String.format("[%s]: %s",
                mutantResult.getMutatorName(),
                mutantResult.getReplacement()
                        .replace("\r\n", "").replace("\n", ""));
    }

    private InputFile locateSourceFile(String sourceFilePath) throws IOException {
        String relativeSourceFilePath = makeRelativePath(sourceFilePath, fileSystem.baseDir());
        FilePredicate filePredicate = fileSystem.predicates().matchesPathPattern("**/" + relativeSourceFilePath);
        InputFile inputFile = null;
        try {
            inputFile = fileSystem.inputFile(filePredicate);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (inputFile == null) {
            log.warn("Could not find input file {} in {}", relativeSourceFilePath, fileSystem.baseDir().getAbsolutePath());
        }
        return inputFile;
    }

    private String makeRelativePath(String sourceFilePath, File baseDir) throws IOException {
        File sourceFile = new File(sourceFilePath);
        //burası dosya yolundan-> /Users/mustafa.yumurtaci/workspace/trendyol/integrationinterface-service/src/Domain/Global/ConfigConstants.cs
        // src/Domain/Global/ConfigConstants.cs'i dönüyor

        if (sourceFile.getCanonicalPath().startsWith(baseDir.getCanonicalPath())) {
            sourceFilePath = sourceFile.getCanonicalPath().substring(baseDir.getCanonicalPath().length() + 1);
        }
        return sourceFilePath;
    }
}
