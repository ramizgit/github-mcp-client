package com.ramiz.mcp;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.util.Map;

public class GetCommitsMcpClient {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static void main(String[] args) throws Exception {

        String githubToken = System.getenv("GITHUB_TOKEN");

        Process process = new ProcessBuilder(
                "docker",
                "run",
                "-i",
                "--rm",
                "-e",
                "GITHUB_PERSONAL_ACCESS_TOKEN=" + githubToken,
                "ghcr.io/github/github-mcp-server"
        ).start();

        // MCP server logs
        Thread.ofPlatform().start(() -> {
            try (BufferedReader reader =
                         new BufferedReader(
                                 new InputStreamReader(process.getErrorStream()))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    System.err.println("[SERVER] " + line);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        BufferedWriter writer =
                new BufferedWriter(
                        new OutputStreamWriter(process.getOutputStream()));

        BufferedReader reader =
                new BufferedReader(
                        new InputStreamReader(process.getInputStream()));

        Thread.sleep(3000);

        //----------------------------------
        // initialize
        //----------------------------------

        send(writer,
                Map.of(
                        "jsonrpc", "2.0",
                        "id", 1,
                        "method", "initialize",
                        "params", Map.of(
                                "protocolVersion", "2025-03-26",
                                "capabilities", Map.of(),
                                "clientInfo", Map.of(
                                        "name", "java-mcp-client",
                                        "version", "1.0"
                                )
                        )
                ));

        System.out.println("INITIALIZE RESPONSE:");
        System.out.println(reader.readLine());

        //----------------------------------
        // initialized notification
        //----------------------------------

        send(writer,
                Map.of(
                        "jsonrpc", "2.0",
                        "method", "notifications/initialized"
                ));

        //----------------------------------
        // get_commit tool call
        //----------------------------------

        send(writer,
                Map.of(
                        "jsonrpc", "2.0",
                        "id", 2,
                        "method", "tools/call",
                        "params", Map.of(
                                //"name", "get_commit",
                                "name", "list_commits",
                                "arguments", Map.of(
                                        "owner", "ramizgit",
                                        "repo", "data-structures-and-algo",
                                        "sha", "main"
                                )
                        )
                ));

        System.out.println();
        System.out.println("GET COMMIT RESPONSE:");
        System.out.println(reader.readLine());

        process.destroy();
    }

    private static void send(
            BufferedWriter writer,
            Object request) throws IOException {

        String json = MAPPER.writeValueAsString(request);

        System.out.println();
        System.out.println("CLIENT -> " + json);

        writer.write(json);
        writer.newLine();
        writer.flush();
    }
}