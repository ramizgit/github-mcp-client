package com.ramiz.mcp.searchutility;

import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.Map;

public class GithubMcpService {

    private final McpSyncClient client;

    public GithubMcpService(McpSyncClient client) {
        this.client = client;
    }

    public String searchCode(String query) {

        McpSchema.CallToolRequest request =
                new McpSchema.CallToolRequest(
                        "search_code",
                        Map.of("query", query)
                );

        McpSchema.CallToolResult result = client.callTool(request);

        if (result.content().isEmpty()) {
            return "";
        }

        McpSchema.TextContent textContent = (McpSchema.TextContent) result.content().get(0);
        return textContent.text();
        //return client.callTool(request).toString();
    }

    public String getFileContents(
            String owner,
            String repo,
            String path) {

        McpSchema.CallToolRequest request =
                new McpSchema.CallToolRequest(
                        "get_file_contents",
                        Map.of(
                                "owner", owner,
                                "repo", repo,
                                "path", path
                        )
                );

        McpSchema.CallToolResult result =
                client.callTool(request);

        for (McpSchema.Content content : result.content()) {

            if (content instanceof McpSchema.EmbeddedResource resource) {

                McpSchema.TextResourceContents textResource =
                        (McpSchema.TextResourceContents) resource.resource();

                return textResource.text();
            }
        }

        return "";
    }
}