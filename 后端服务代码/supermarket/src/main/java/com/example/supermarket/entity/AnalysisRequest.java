package com.example.supermarket.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AnalysisRequest {
    @JsonProperty("focusArea") private String focusArea;
}
