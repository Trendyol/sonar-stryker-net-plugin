package io.github.strykermutator;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.github.strykermutator.report.File;
import io.github.strykermutator.report.Mutant;
import io.github.strykermutator.report.MutationReport;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class MutantResultJsonReader {

    private Gson gson = new Gson();

    List<Mutant> readMutants(String reportContent) {
        MutationReport mutationReport = gson.fromJson(reportContent, new TypeToken<MutationReport>() {
        }.getType());

        Map<String, File> files = mutationReport.getFiles();
        //her dosyayı sırayla iterate et, her dosyada mutants arrayi empty ise atla,
        // değilse her bir mutant fileName'i bas ve mutant arrayine pushla

        List<Mutant> mutantsWithFileName = new ArrayList<>();
        for (Map.Entry<String, File> entry : files.entrySet()) {
            List<Mutant> mutants = entry.getValue().getMutants();
            if (mutants == null || mutants.isEmpty()) continue;
            for (Mutant mutant : entry.getValue().getMutants()) {
                mutantsWithFileName.add(
                        Mutant.builder()
                                .location(mutant.getLocation())
                                .mutatorName(mutant.getMutatorName())
                                .replacement(mutant.getReplacement())
                                .status(mutant.getStatus())
                                .fileName(String.format("%s/%s", mutationReport.getProjectRoot(), entry.getKey()))
                                .build()
                );
            }
        }
        return mutantsWithFileName;
    }
}

