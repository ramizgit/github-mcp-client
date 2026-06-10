package com.ramiz.mcp.primpactanalyzer;

import com.ramiz.mcp.primpactanalyzer.models.ImpactReport;
import com.ramiz.mcp.primpactanalyzer.parser.ChangedFileParser;
import com.ramiz.mcp.primpactanalyzer.service.GithubPrService;
import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.ServerParameters;
import io.modelcontextprotocol.client.transport.StdioClientTransport;
import io.modelcontextprotocol.json.jackson3.JacksonMcpJsonMapper;
import tools.jackson.databind.json.JsonMapper;

public class Main {

    public static void main(String[] args) {

        ServerParameters params =
                ServerParameters.builder("docker")
                        .args(
                                "run",
                                "-i",
                                "--rm",
                                "-e",
                                "GITHUB_PERSONAL_ACCESS_TOKEN="+ System.getenv("GITHUB_TOKEN"),
                                "ghcr.io/github/github-mcp-server"
                        )
                        .build();

        StdioClientTransport transport =
                new StdioClientTransport(
                        params,
                        new JacksonMcpJsonMapper(
                                JsonMapper.builder().build()
                        )
                );

        McpSyncClient client =
                McpClient.sync(transport)
                        .build();

        try {

            client.initialize();

            GithubPrService githubPrService =
                    new GithubPrService(client);

            ChangedFileParser parser =
                    new ChangedFileParser();

            PrImpactAnalyzer analyzer =
                    new PrImpactAnalyzer(
                            githubPrService,
                            parser
                    );

            ImpactReport report =
                    analyzer.analyze(
                            "ramizgit",
                            "data-structures-and-algo",
                            1
                    );

            System.out.println(
                    "===== CHANGED FILES ====="
            );

            report.changedFiles()
                    .forEach(file ->
                            System.out.println(
                                    file.path()
                                            + " -> "
                                            + file.module()
                            )
                    );

            System.out.println();

            System.out.println(
                    "===== AFFECTED MODULES ====="
            );

            report.affectedModules()
                    .forEach(System.out::println);

        }
        finally {

            client.closeGracefully();
        }
    }
}