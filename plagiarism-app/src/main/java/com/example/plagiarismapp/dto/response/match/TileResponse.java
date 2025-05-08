package com.example.plagiarismapp.dto.response.match;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TileResponse {
    private Long startLineInFirstFile;
    private Long endLineInFirstFile;
    private Long startLineInSecondFile;
    private Long endLineInSecondFile;
    private String textInFirstFile;
    private String textInSecondFile;
}
