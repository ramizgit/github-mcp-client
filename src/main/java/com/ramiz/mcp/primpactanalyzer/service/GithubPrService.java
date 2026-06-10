package com.ramiz.mcp.primpactanalyzer.service;

import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.Map;

public class GithubPrService {

    private final McpSyncClient client;

    public GithubPrService(McpSyncClient client) {
        this.client = client;
    }

    public String getChangedFiles(
            String owner,
            String repo,
            int pullNumber) {

        McpSchema.CallToolRequest request =
                new McpSchema.CallToolRequest(
                        "pull_request_read",
                        Map.of(
                                "method", "get_files",
                                "owner", owner,
                                "repo", repo,
                                "pullNumber", pullNumber
                        )
                );

        return client.callTool(request)
                .toString();
    }
}
