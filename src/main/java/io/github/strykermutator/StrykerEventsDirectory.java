package io.github.strykermutator;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.sensor.SensorContext;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Optional;

import static io.github.strykermutator.StrykerConstants.DEFAULT_REPORT_DIRECTORY;
import static io.github.strykermutator.StrykerConstants.REPORT_DIRECTORY_KEY;


@Slf4j
public class StrykerEventsDirectory {

    public static final String ON_ALL_MUTANTS_TESTED_FILE_NAME = "onAllMutantsTested";
    private final File reportDirectory;

    StrykerEventsDirectory(SensorContext context, FileSystem fs) {
        Optional<String> projectReportDirectory = context.config().get(REPORT_DIRECTORY_KEY);
        reportDirectory = new java.io.File(fs.baseDir(), projectReportDirectory.orElse(DEFAULT_REPORT_DIRECTORY));
    }

    Optional<String> readReportFileAsByteString() throws IOException {

        validateDirectory();

        File reportFile = getReportFile();
        return Optional.of(new String(FileUtils.readFileToByteArray(reportFile)));
    }

    private void validateDirectory() throws IOException {
        if (!reportDirectory.exists() || !reportDirectory.isDirectory()) {
            throw new IOException(String.format("Report directory %s does not exist or it's not a directory (it's a file)",
                    reportDirectory.getAbsolutePath()));
        }
    }

    private File getReportFile() throws IOException {
        File[] onAllMutantsTestedFiles = reportDirectory.listFiles(filter(ON_ALL_MUTANTS_TESTED_FILE_NAME));

        if (onAllMutantsTestedFiles == null || onAllMutantsTestedFiles.length != 1) {
            throw new IOException(String.format("Could not find a file with '%s' in the name in directory %s",
                    ON_ALL_MUTANTS_TESTED_FILE_NAME, reportDirectory.getAbsolutePath()));
        }

        if (!onAllMutantsTestedFiles[0].isFile()) {
            throw new IOException(String.format("File %s is not a file (it's a directory)",
                    onAllMutantsTestedFiles[0].getAbsolutePath()));
        }
        return onAllMutantsTestedFiles[0];
    }

    private FilenameFilter filter(String fileName) {
        return (dir, name) -> name.contains(fileName);
    }

}
