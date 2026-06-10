package com.ramiz.mcp.searchutility.models;

import java.util.List;

public record SearchCodeResponse(int total_count,
                                 List<SearchItem> items) {
}
