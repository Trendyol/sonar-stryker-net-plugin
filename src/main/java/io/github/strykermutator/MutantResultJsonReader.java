package io.github.strykermutator;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.github.strykermutator.report.File;
import io.github.strykermutator.report.MutantResult;
import io.github.strykermutator.report.MutationReport;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class MutantResultJsonReader {

    private Gson gson = new Gson();

    List<MutantResult> readMutants(String reportContent) {
        MutationReport mutationReport = gson.fromJson(reportContent, new TypeToken<MutationReport>() {
        }.getType());

        Map<String, File> files = mutationReport.getFiles();
        //her dosyayı sırayla iterate et, her dosyada mutants arrayi empty ise atla,
        // değilse her bir mutant fileName'i bas ve mutant arrayine pushla

        List<MutantResult> mutantsWithFileName = new ArrayList<>();
        for (Map.Entry<String, File> entry : files.entrySet()) {
            List<MutantResult> mutantResults = entry.getValue().getMutants();
            //TODO: çalışıyor mu?
            if (mutantResults == null || mutantResults.isEmpty()) continue;
            for (MutantResult mutantResult : entry.getValue().getMutants()) {
                mutantsWithFileName.add(
                        MutantResult.builder()
                                .location(mutantResult.getLocation())
                                .mutatorName(mutantResult.getMutatorName())
                                .replacement(mutantResult.getReplacement())
                                .status(mutantResult.getStatus())
                                .fileName(String.format("%s/%s", mutationReport.getProjectRoot(), entry.getKey()))
                                .build()
                );
            }
        }
        return mutantsWithFileName;
    }
}

