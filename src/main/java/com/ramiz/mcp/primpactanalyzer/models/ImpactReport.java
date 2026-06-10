package com.ramiz.mcp.primpactanalyzer.models;

import java.util.Set;

public record ImpactReport(Set<ChangedFile> changedFiles,
                           Set<String> affectedModules) {
}
