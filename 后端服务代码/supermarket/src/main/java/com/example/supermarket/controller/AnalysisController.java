package com.example.supermarket.controller;

import com.example.supermarket.common.Result;
import com.example.supermarket.entity.AnalysisRequest;
import com.example.supermarket.entity.AnalysisResponse;
import com.example.supermarket.service.AnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analysis")
@RequiredArgsConstructor
public class AnalysisController {

    private final AnalysisService analysisService;

    @PostMapping
    public Result<AnalysisResponse> analyze(@RequestBody AnalysisRequest request) {
        AnalysisResponse result = analysisService.analyze(request);
        return Result.success(result);
    }
}
