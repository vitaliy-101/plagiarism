package com.example.plagiarismapp.dto.request.project;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RepositoryMatchResponse {
    private Long firstRepositoryId;
    private String firstRepositoryOwner;
    private Long secondRepositoryId;
    private String secondRepositoryOwner;
    private Double percentage;
}
