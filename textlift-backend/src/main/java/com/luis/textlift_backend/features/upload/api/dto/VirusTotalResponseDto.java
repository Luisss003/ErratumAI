package com.luis.textlift_backend.features.upload.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record VirusTotalResponseDto(
        Data data
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Data(
            String id,
            String type,
            Attributes attributes
    )
    {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Attributes(
            @JsonProperty("last_analysis_stats")
            LastAnalysisStats lastAnalysisStats,

            @JsonProperty("crowdsourced_yara_results")
            List<?> crowdsourcedYaraResults
    ){}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record LastAnalysisStats(
            Integer malicious,
            Integer suspicious
    ){}

}
