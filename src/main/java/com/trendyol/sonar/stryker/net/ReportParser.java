package com.trendyol.sonar.stryker.net;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.trendyol.sonar.stryker.net.model.Mutant;
import com.trendyol.sonar.stryker.net.model.MutationReport;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.sonar.api.batch.sensor.SensorContext;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ReportParser {

    private final File strykerOutputDirectory;

    ReportParser(SensorContext context) {
        String projectReportDirectory = context.config().get(Constants.STRYKER_NET_OUTPUT_DIRECTORY_KEY)
                .orElse(Constants.DEFAULT_STRYKER_NET_OUTPUT_DIRECTORY);

        strykerOutputDirectory = new java.io.File(context.fileSystem().baseDir(), projectReportDirectory);
    }

    public List<Mutant> parseMutants() throws IOException {
        final String reportFile = readReportFileAsByteString();
        if (StringUtils.isEmpty(reportFile)) {
            throw new IOException("Could not find stryker report, not reporting issues.");
        }
        return readMutants(reportFile);
    }

    private String readReportFileAsByteString() throws IOException {

        validateDirectory();

        File jsonReportFile = getJsonReportFile();

        return new String(FileUtils.readFileToByteArray(jsonReportFile));
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

        Optional<File> lastModifiedReportFolder = Arrays.stream(reportFolders)
                .filter(f -> f.getName().matches(Constants.STRYKER_NET_REPORT_FOLDER_REGEX))
                .max((f1, f2) -> {
                    DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd.HH-mm-ss");
                    DateTime d1 = formatter.parseDateTime(f1.getName());
                    DateTime d2 = formatter.parseDateTime(f2.getName());
                    return d1.compareTo(d2);
                });


        if (lastModifiedReportFolder.isPresent()) {
            return lastModifiedReportFolder.get();
        }
        throw new IOException("Couldn't find any report folder!");
    }

    private List<Mutant> readMutants(String report) {
        final Gson gson = new Gson();

        MutationReport mutationReport = gson.fromJson(report, new TypeToken<MutationReport>() {
        }.getType());

        Map<String, com.trendyol.sonar.stryker.net.model.File> files = mutationReport.getFiles();

        // iterate through the hashmap, set it's key as a mutant's filename
        List<Mutant> mutantsWithFileName = new ArrayList<>();
        for (Map.Entry<String, com.trendyol.sonar.stryker.net.model.File> entry : files.entrySet()) {
            List<Mutant> mutants = entry.getValue().getMutants();
            if (Objects.isNull(mutants) || mutants.isEmpty()) continue;
            for (Mutant mutant : mutants) {
                mutantsWithFileName.add(
                        buildMutant(mutationReport.getProjectRoot(), entry.getKey(), mutant)
                );
            }
        }
        return mutantsWithFileName;
    }

    private Mutant buildMutant(final String projectRoot, final String filename, final Mutant mutant) {
        return Mutant.builder()
                .location(mutant.getLocation())
                .mutatorName(mutant.getMutatorName())
                .replacement(mutant.getReplacement())
                .status(mutant.getStatus())
                .fileName(String.format("%s/%s", projectRoot, filename))
                .build();
    }
}
