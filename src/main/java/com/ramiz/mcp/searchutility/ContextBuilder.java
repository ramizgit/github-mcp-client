package com.ramiz.mcp.searchutility;

import com.ramiz.mcp.searchutility.models.RetrievedFile;

import java.util.List;

public class ContextBuilder {

    public String build(
            String question,
            List<RetrievedFile> files) {

        StringBuilder sb =
                new StringBuilder();

        sb.append(""" 
            You are a senior software engineer.
            
            Analyze ALL provided files before answering.

            For your answer:

            1. Summarize the overall topic.
            2. Mention every relevant file.
            3. Explain the role of each file.
            4. If multiple approaches exist, compare them.
            5. Do not ignore files provided in the context.
            
            ====================================
            
            """
        );

        sb.append("QUESTION:\n");
        sb.append(question);

        sb.append("\n\n");

        for (RetrievedFile file : files) {

            sb.append("FILE: ");
            sb.append(file.path());

            sb.append("\n\n");

            sb.append(file.content());

            sb.append("\n\n");
            sb.append("====================================");
            sb.append("\n\n");
        }

        return sb.toString();
    }
}