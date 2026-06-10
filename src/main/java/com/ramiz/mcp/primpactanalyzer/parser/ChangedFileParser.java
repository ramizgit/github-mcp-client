package com.ramiz.mcp.primpactanalyzer.parser;

import com.ramiz.mcp.primpactanalyzer.models.ChangedFile;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChangedFileParser {

    private static final Pattern PATTERN =
            Pattern.compile("\"filename\":\"([^\"]+)\"");

    public Set<ChangedFile> extractChangedFiles(
            String response) {

        Set<ChangedFile> files =
                new HashSet<>();

        Matcher matcher =
                PATTERN.matcher(response);

        while (matcher.find()) {

            String path =
                    matcher.group(1);

            files.add(
                    new ChangedFile(
                            path,
                            extractModule(path)
                    )
            );
        }

        return files;
    }

    private String extractModule(
            String path) {

        String[] parts =
                path.split("/");

        if(parts.length >= 3) {

            return parts[1]
                    + "/"
                    + parts[2];
        }

        return "unknown";
    }
}
