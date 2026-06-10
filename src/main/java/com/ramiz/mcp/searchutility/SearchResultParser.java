package com.ramiz.mcp.searchutility;

import com.ramiz.mcp.searchutility.models.SearchCodeResponse;
import com.ramiz.mcp.searchutility.models.SearchItem;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

public class SearchResultParser {

    private final ObjectMapper objectMapper =
            new ObjectMapper();

    public List<String> extractPaths(
            String json) {

        try {

            SearchCodeResponse response =
                    objectMapper.readValue(
                            json,
                            SearchCodeResponse.class
                    );

            return response.items()
                    .stream()
                    .map(SearchItem::path)
                    .toList();

        }
        catch (Exception ex) {

            throw new RuntimeException(ex);
        }
    }
}
