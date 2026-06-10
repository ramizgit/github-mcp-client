package com.ramiz.mcp;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class GitHubMcpClientMock {

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

        Thread stdoutThread = new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("[STDOUT] " + line);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Thread stderrThread = new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getErrorStream()))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    System.err.println("[STDERR] " + line);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        stdoutThread.start();
        stderrThread.start();

        System.out.println("GitHub MCP server started...");
        System.out.println("Sleeping for 10 seconds...");

        Thread.sleep(10_000);

        System.out.println("Destroying process...");

        process.destroy();

        int exitCode = process.waitFor();

        System.out.println("Exit code = " + exitCode);
    }
}
