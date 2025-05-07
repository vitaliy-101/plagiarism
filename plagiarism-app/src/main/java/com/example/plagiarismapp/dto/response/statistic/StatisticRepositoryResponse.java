package com.example.plagiarismapp.dto.response.statistic;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StatisticRepositoryResponse {
    private Long numberOfRepositories;
    private Long numberOfFiles;
    private Long numberOfSuspiciousFiles;
    private Double maxSimilarity;
    private Double averageSimilarity;
}
