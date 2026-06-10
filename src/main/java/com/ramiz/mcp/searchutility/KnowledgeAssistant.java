package com.ramiz.mcp.searchutility;

import com.ramiz.mcp.searchutility.models.RetrievedFile;
import com.ramiz.mcp.searchutility.service.DummyLlmService;
import com.ramiz.mcp.searchutility.service.LlmService;

import java.util.ArrayList;
import java.util.List;

public class KnowledgeAssistant {

    private final GithubMcpService github;
    private final SearchResultParser parser;
    private final ContextBuilder contextBuilder;

    private final LlmService llmService;

    public KnowledgeAssistant(
            GithubMcpService github,
            SearchResultParser parser,
            ContextBuilder contextBuilder,
            LlmService llmService) {

        this.github = github;
        this.parser = parser;
        this.contextBuilder = contextBuilder;
        this.llmService = llmService;
    }

    public String answerQuestion(
            String owner,
            String repo,
            String question) {

        // Step 1: Search repo
        String query =
                question
                        + " repo:"
                        + owner
                        + "/"
                        + repo;

        String searchResult =
                github.searchCode(query);

        // Step 2: Extract file paths
        List<String> paths =
                parser.extractPaths(searchResult);

        System.out.println("Found paths:");

        paths.stream()
                .limit(3)
                .forEach(System.out::println);

        // Step 3: Fetch file contents
        List<RetrievedFile> files =
                new ArrayList<>();

        for (String path :
                paths.stream()
                        .filter(path -> path.endsWith(".java"))
                        .limit(3)
                        .toList()) {

            String content =
                    github.getFileContents(
                            owner,
                            repo,
                            path
                    );

            files.add(
                    new RetrievedFile(
                            path,
                            content
                    )
            );
        }

        // Step 4: Build context
        String context =
                contextBuilder.build(
                        question,
                        files
                );

        //return context;
        return llmService.ask(context);
    }
}