package com.ramiz.mcp.primpactanalyzer;

import com.ramiz.mcp.primpactanalyzer.models.ChangedFile;
import com.ramiz.mcp.primpactanalyzer.models.ImpactReport;
import com.ramiz.mcp.primpactanalyzer.parser.ChangedFileParser;
import com.ramiz.mcp.primpactanalyzer.service.GithubPrService;

import java.util.Set;
import java.util.stream.Collectors;

public class PrImpactAnalyzer {

    private final GithubPrService githubPrService;
    private final ChangedFileParser parser;

    public PrImpactAnalyzer(
            GithubPrService githubPrService,
            ChangedFileParser parser) {

        this.githubPrService = githubPrService;
        this.parser = parser;
    }

    public ImpactReport analyze(
            String owner,
            String repo,
            int prNumber) {

        String response =
                githubPrService.getChangedFiles(
                        owner,
                        repo,
                        prNumber
                );

        Set<ChangedFile> changedFiles =
                parser.extractChangedFiles(
                        response
                );

        Set<String> affectedModules =
                changedFiles.stream()
                        .map(ChangedFile::module)
                        .collect(Collectors.toSet());

        return new ImpactReport(
                changedFiles,
                affectedModules
        );
    }
}