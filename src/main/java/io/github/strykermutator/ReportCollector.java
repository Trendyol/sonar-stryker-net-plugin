package io.github.strykermutator;

import io.github.strykermutator.report.Mutant;
import org.sonar.api.batch.sensor.SensorContext;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class ReportCollector {

    public List<Mutant> collectMutants(SensorContext context) throws IOException {
        StrykerEventsDirectory strykerEvents = new StrykerEventsDirectory(context);
        Optional<String> reportFile = strykerEvents.readReportFileAsByteString();
        if (reportFile.isPresent()) {
            MutantResultJsonReader reader = new MutantResultJsonReader();
            return reader.readMutants(reportFile.get());
        } else {
            throw new IOException("Could not find stryker report, not reporting issues.");
        }
    }
}
