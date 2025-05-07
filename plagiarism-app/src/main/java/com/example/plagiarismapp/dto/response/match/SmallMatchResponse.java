package com.example.plagiarismapp.dto.response.match;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SmallMatchResponse {
    private Long id;
    private Double percentage;
    private Long firstFileId;
    private String firstFileName;
    private Long secondFileId;
    private String secondFileName;
}
