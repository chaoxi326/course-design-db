package com.example.supermarket.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AnalysisResponse {
    @JsonProperty("content") private String content;
    @JsonProperty("model") private String model;
    @JsonProperty("inputTokens") private int inputTokens;
    @JsonProperty("outputTokens") private int outputTokens;
}
