package io.github.strykermutator;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.sonar.api.batch.sensor.SensorContext;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import static io.github.strykermutator.StrykerConstants.*;


@Slf4j
public class StrykerEventsDirectory {

    private final File strykerOutputDirectory;


    StrykerEventsDirectory(SensorContext context) {
        String projectReportDirectory = context.config().get(STRYKER_OUTPUT_DIRECTORY_KEY).orElse(DEFAULT_STRYKER_OUTPUT_DIRECTORY);
        strykerOutputDirectory = new java.io.File(context.fileSystem().baseDir(), projectReportDirectory);
    }

    Optional<String> readReportFileAsByteString() throws IOException {

        validateDirectory();

        File jsonReportFile = getJsonReportFile();

        return Optional.of(new String(FileUtils.readFileToByteArray(jsonReportFile)));
    }

    private void validateDirectory() throws IOException {
        if (strykerOutputDirectory.exists() && strykerOutputDirectory.isDirectory()) {
            return;
        }
        throw new IOException(String.format("Report directory %s does not exist or it's not a directory (it's a file)",
                strykerOutputDirectory.getAbsolutePath()));
    }

    private File getJsonReportFile() throws IOException {
        File[] reports = strykerOutputDirectory.listFiles();

        if (Objects.isNull(reports) || ArrayUtils.isEmpty(reports)) {
            throw new IOException(String.format("Could not find any folders under %s", strykerOutputDirectory.getAbsolutePath()));
        }

        File lastAnalysisReportFolder = findLastModifiedReportFolder(reports);

        String reportFilePath = String.format("%s/reports/mutation-report.json", lastAnalysisReportFolder.getAbsolutePath());

        return new File(reportFilePath);
    }

    private File findLastModifiedReportFolder(File[] reportFolders) throws IOException {

        //TODO: last modified'a değil, folder adındaki date'e göre filtrele.

        final String REPORT_FOLDER_REGEX =
                "[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1]).(2[0-3]|[01][0-9])-[0-5][0-9]-[0-5][0-9]";

        Optional<File> lastModifiedReportFolder = Arrays.stream(reportFolders)
                .filter(f -> f.getName().matches(REPORT_FOLDER_REGEX))
                .sorted((f1, f2) ->
                        (int) (f2.lastModified() - f1.lastModified()))
                .findFirst();

        if (lastModifiedReportFolder.isPresent()) {
            return lastModifiedReportFolder.get();
        }
        throw new IOException("Couldn't find any report folder!");
    }
}
