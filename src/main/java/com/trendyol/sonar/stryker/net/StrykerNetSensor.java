package com.trendyol.sonar.stryker.net;

import com.trendyol.sonar.stryker.net.model.Mutant;
import lombok.extern.slf4j.Slf4j;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.rule.ActiveRules;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.scanner.sensor.ProjectSensor;

import java.util.List;

@Slf4j
public class StrykerNetSensor implements ProjectSensor {

    private final RulesProcessor rulesProcessor;

    public StrykerNetSensor(final ActiveRules rulesProfile, final FileSystem
            fileSystem) {
        this.rulesProcessor = new RulesProcessor(rulesProfile, fileSystem);
    }


    @Override
    public void describe(SensorDescriptor descriptor) {
        descriptor.name("Stryker.NET Mutation Analysis");
    }


    @Override
    public void execute(final SensorContext context) {

        try {
            ReportParser reportParser = new ReportParser(context);
            List<Mutant> mutants = reportParser.parseMutants();

            this.rulesProcessor.processRules(context, mutants);
        } catch (Exception e) {
            log.error("Could not read mutants", e);
        }
    }
}
