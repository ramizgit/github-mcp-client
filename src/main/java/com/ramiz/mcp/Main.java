package com.ramiz.mcp;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Main {

    public static void main(String[] args) throws Exception {

        /*Process process = new ProcessBuilder(
                "docker",
                "--version"
        ).start();*/

        Process process = new ProcessBuilder(
                "docker",
                "run",
                "-i",
                "--rm",
                "-e",
                "GITHUB_PERSONAL_ACCESS_TOKEN="+ System.getenv("GITHUB_TOKEN"),
                "ghcr.io/github/github-mcp-server"
        ).start();

        try (BufferedReader reader =
                     new BufferedReader(
                             new InputStreamReader(process.getInputStream()))) {

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }

        process.waitFor();
    }
}