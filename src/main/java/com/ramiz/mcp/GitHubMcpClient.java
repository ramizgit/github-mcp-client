package com.ramiz.mcp;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.util.Map;

public class GitHubMcpClient {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static void main(String[] args) throws Exception {

        String githubToken = System.getenv("GITHUB_PERSONAL_ACCESS_TOKEN");

        Process process = new ProcessBuilder(
                "docker",
                "run",
                "-i",
                "--rm",
                "-e",
                "GITHUB_PERSONAL_ACCESS_TOKEN=" + githubToken,
                "ghcr.io/github/github-mcp-server"
        ).start();

        // Server logs
        Thread stderrThread = new Thread(() -> {
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

        stderrThread.setDaemon(true);
        stderrThread.start();

        BufferedWriter writer =
                new BufferedWriter(
                        new OutputStreamWriter(process.getOutputStream()));

        BufferedReader reader =
                new BufferedReader(
                        new InputStreamReader(process.getInputStream()));

        Thread.sleep(3000);

        // ---------------------------
        // initialize
        // ---------------------------

        String initializeRequest =
                MAPPER.writeValueAsString(
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
                        )
                );

        send(writer, initializeRequest);

        System.out.println("----- INITIALIZE RESPONSE -----");
        System.out.println(reader.readLine());

        // ---------------------------
        // initialized notification
        // ---------------------------

        String initializedNotification =
                MAPPER.writeValueAsString(
                        Map.of(
                                "jsonrpc", "2.0",
                                "method", "notifications/initialized"
                        )
                );

        send(writer, initializedNotification);

        // ---------------------------
        // tools/list
        // ---------------------------

        String toolsListRequest =
                MAPPER.writeValueAsString(
                        Map.of(
                                "jsonrpc", "2.0",
                                "id", 2,
                                "method", "tools/list"
                        )
                );

        send(writer, toolsListRequest);

        System.out.println("----- TOOLS LIST RESPONSE -----");
        System.out.println(reader.readLine());

        process.destroy();
    }

    private static void send(
            BufferedWriter writer,
            String message) throws IOException {

        System.out.println("CLIENT -> " + message);

        writer.write(message);
        writer.newLine();
        writer.flush();
    }
}