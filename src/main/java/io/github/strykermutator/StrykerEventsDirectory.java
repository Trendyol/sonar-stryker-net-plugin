package io.github.strykermutator;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.sensor.SensorContext;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

import static io.github.strykermutator.StrykerConstants.*;


@Slf4j
public class StrykerEventsDirectory {

    private final File reportDirectory;
    private static final String REPORT_FOLDER_REGEX =
            "[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1]).(2[0-3]|[01][0-9])-[0-5][0-9]-[0-5][0-9]";

    StrykerEventsDirectory(SensorContext context, FileSystem fs) {
        String projectReportDirectory = context.config().get(STRYKER_OUTPUT_DIRECTORY_KEY).orElse(DEFAULT_STRYKER_OUTPUT_DIRECTORY);
        reportDirectory = new java.io.File(fs.baseDir(), projectReportDirectory);
    }

    Optional<String> readReportFileAsByteString() throws IOException {

        validateDirectory();

        File reportFileJson = getReportFile();

        return Optional.of(new String(FileUtils.readFileToByteArray(reportFileJson)));
    }

    private void validateDirectory() throws IOException {
        if (!reportDirectory.exists() || !reportDirectory.isDirectory()) {
            throw new IOException(String.format("Report directory %s does not exist or it's not a directory (it's a file)",
                    reportDirectory.getAbsolutePath()));
        }
    }

    private File getReportFile() throws IOException {
        // folders under stryker output folder
        File[] reportFolders = reportDirectory.listFiles();

        if (reportFolders == null || reportFolders.length == 0) {
            throw new IOException(String.format("Could not find any folders under %s", reportDirectory.getAbsolutePath()));
        }

        File lastModifiedReportFolder = findLastModifiedReportFolder(reportFolders);

        String reportFilePath = String.format("%s/reports/mutation-report.json", lastModifiedReportFolder.getAbsolutePath());

        return new File(reportFilePath);
    }

    private File findLastModifiedReportFolder(File[] reportFolders) {

        Stream<File> sortedFolderList = Arrays.stream(reportFolders)
                .filter(f -> f.getName().matches(REPORT_FOLDER_REGEX))
                .sorted((f1, f2) ->
                        //TODO: BURADA EN ESKİ DOSYAYI GETİRİYOR, TAM TERSİ OLMALI!
                        (int) (f2.lastModified() - f1.lastModified()));
        //TODO: last modified'a değil, folder adındaki date'e göre filtrele.

        //bunlardan sadece reports'un gelmesi lazım
        File latestModifiedReportFolder = sortedFolderList.findFirst().get();

        return latestModifiedReportFolder;

    }
}
