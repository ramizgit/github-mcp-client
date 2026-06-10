package com.ramiz.mcp.sdk;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.ServerParameters;
import io.modelcontextprotocol.client.transport.StdioClientTransport;
import io.modelcontextprotocol.json.McpJsonMapper;
import io.modelcontextprotocol.json.jackson3.JacksonMcpJsonMapper;
import io.modelcontextprotocol.spec.McpSchema;
import tools.jackson.databind.json.JsonMapper;

import java.util.Map;

public class GitHubMcpSdkClient {

    public static void main(String[] args) {

        ServerParameters params =
                ServerParameters.builder("docker")
                        .args(
                                "run",
                                "-i",
                                "--rm",
                                "-e",
                                "GITHUB_PERSONAL_ACCESS_TOKEN="+ System.getenv("GITHUB_PERSONAL_ACCESS_TOKEN"),
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

        McpSyncClient client =
                McpClient.sync(transport)
                        .build();

        // MCP initialize handshake
        client.initialize();

        // List tools
        var toolsResult = client.listTools();

        System.out.println("===== LIST TOOLS =====");
        System.out.println(toolsResult);

        // Call GitHub MCP tool
        McpSchema.CallToolRequest request =
                new McpSchema.CallToolRequest(
                        "get_commit",
                        Map.of(
                                "owner", "ramizgit",
                                "repo", "data-structures-and-algo",
                                "sha", "main"
                        )
                );

        McpSchema.CallToolResult result =
                client.callTool(request);

        System.out.println("===== COMMIT RESULT =====");
        System.out.println(result);

        client.closeGracefully();
    }
}