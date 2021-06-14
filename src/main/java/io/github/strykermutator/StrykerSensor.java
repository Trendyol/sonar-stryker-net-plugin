package io.github.strykermutator;

import io.github.strykermutator.report.Mutant;
import lombok.extern.slf4j.Slf4j;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.rule.ActiveRules;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.scanner.sensor.ProjectSensor;

import java.util.List;

@Slf4j
public class StrykerSensor implements ProjectSensor {

    private final ReportCollector reportCollector;
    private final RulesProcessor rulesProcessor;

    public StrykerSensor(final ActiveRules rulesProfile, final FileSystem
            fileSystem) {
        this.rulesProcessor = new RulesProcessor(rulesProfile, fileSystem);
        this.reportCollector = new ReportCollector();
    }


    @Override
    public void describe(SensorDescriptor descriptor) {
        descriptor.name("Stryker Mutation Analysis");
    }


    @Override
    public void execute(SensorContext context) {

        try {
            List<Mutant> mutants = this.reportCollector.collectMutants(context);
            this.rulesProcessor.processRules(context, mutants);
        } catch (Exception e) {
            log.error("Could not read mutants", e);
        }
    }
}
