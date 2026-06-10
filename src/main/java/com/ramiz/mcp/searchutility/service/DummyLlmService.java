package com.ramiz.mcp.searchutility.service;

public class DummyLlmService
        implements LlmService {

    @Override
    public String ask(String prompt) {

        return """
               LLM would receive:

               """ + prompt.substring(
                0,
                Math.min(500, prompt.length())
        );
    }
}
