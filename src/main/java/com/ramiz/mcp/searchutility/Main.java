package com.ramiz.mcp.searchutility;

import com.ramiz.mcp.searchutility.service.LlmService;
import com.ramiz.mcp.searchutility.service.OllamaLlmService;
import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.ServerParameters;
import io.modelcontextprotocol.client.transport.StdioClientTransport;
import io.modelcontextprotocol.json.McpJsonMapper;
import io.modelcontextprotocol.json.jackson3.JacksonMcpJsonMapper;
import tools.jackson.databind.json.JsonMapper;

public class Main {

    public static void main(String[] args) {

        String token = System.getenv("GITHUB_PERSONAL_ACCESS_TOKEN");

        ServerParameters params =
                ServerParameters.builder("docker")
                        .args(
                                "run",
                                "-i",
                                "--rm",
                                "-e",
                                "GITHUB_PERSONAL_ACCESS_TOKEN=" + token,
                                "ghcr.io/github/github-mcp-server"
                        )
                        .build();

        McpJsonMapper mapper =
                new JacksonMcpJsonMapper(
                        JsonMapper.builder().build()
                );

        StdioClientTransport transport =
                new StdioClientTransport(
                        params,
                        mapper
                );

        McpSyncClient client = McpClient.sync(transport).build();

        client.initialize();

        GithubMcpService github = new GithubMcpService(client);

        SearchResultParser parser = new SearchResultParser();

        ContextBuilder contextBuilder = new ContextBuilder();

        LlmService llmService = new OllamaLlmService();

        KnowledgeAssistant assistant =
                new KnowledgeAssistant(
                        github,
                        parser,
                        contextBuilder,
                        llmService
                );

        String context = assistant.answerQuestion(
                "ramizgit",
                "data-structures-and-algo",
                //"islands",
                "Compare all island-related algorithms in this repository."
        );

        System.out.println(context);

        client.closeGracefully();
    }
}
