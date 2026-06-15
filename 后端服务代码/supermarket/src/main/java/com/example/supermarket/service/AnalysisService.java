package com.example.supermarket.service;

import com.example.supermarket.entity.AnalysisRequest;
import com.example.supermarket.entity.AnalysisResponse;

public interface AnalysisService {
    AnalysisResponse analyze(AnalysisRequest request);
}
