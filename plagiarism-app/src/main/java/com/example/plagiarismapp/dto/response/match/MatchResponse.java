package com.example.plagiarismapp.dto.response.match;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MatchResponse {

    private Long id;
    private Double percentage;
    private Long firstFileId;
    private Long firstRepositoryId;
    private String firstFileName;
    private Long secondFileId;
    private String secondFileName;
    private Long secondRepositoryId;
    private List<TileResponse> tiles;
}
