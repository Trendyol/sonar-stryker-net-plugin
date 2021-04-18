package io.github.strykermutator;

import lombok.extern.slf4j.Slf4j;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.internal.apachecommons.io.FileUtils;

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
        Optional<String> projectReportDirectory= context.config().get(REPORT_DIRECTORY_KEY);
        reportDirectory = new java.io.File(fs.baseDir(), projectReportDirectory.orElse(DEFAULT_REPORT_DIRECTORY));
    }

    Optional<String> readOnAllMutantsTestedFile() throws IOException {
        if (reportDirectory.exists()) {
            if (reportDirectory.isDirectory()) {
                File[] onAllMutantsTestedFiles = reportDirectory.listFiles(filter(ON_ALL_MUTANTS_TESTED_FILE_NAME));
                if (onAllMutantsTestedFiles != null && onAllMutantsTestedFiles.length == 1) {
                    if (onAllMutantsTestedFiles[0].isFile()) {
                        log.info("Reading onAllMutantsTested result file {}", onAllMutantsTestedFiles[0].getPath());
                        return Optional.of( new String(FileUtils.readFileToByteArray(onAllMutantsTestedFiles[0])));
                    }else {
                        log.info("File {} is not a file (it's a directory)", onAllMutantsTestedFiles[0].getAbsolutePath());
                    }
                }else {
                    log.info("Could not find a file with '{}' in the name in directory {}", ON_ALL_MUTANTS_TESTED_FILE_NAME, reportDirectory.getAbsolutePath());
                }
            }else {
                log.info("Report directory {} is not a directory (it's a file).", reportDirectory.getAbsolutePath());
            }
        }else {
            log.info("Report directory {} does not exist.", reportDirectory.getAbsolutePath());
        }
        return Optional.empty();
    }

    private FilenameFilter filter(String fileName) {
        return (dir, name) -> name.contains(fileName);
    }

}
