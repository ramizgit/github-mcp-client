package com.ramiz.mcp.searchutility.models;

public record OllamaRequest(String model,
                            String prompt,
                            boolean stream) {
}
