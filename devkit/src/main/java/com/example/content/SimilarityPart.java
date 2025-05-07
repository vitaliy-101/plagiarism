package com.example.content;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SimilarityPart {
    private Long startLineInFirstFile;
    private Long startColumnInFirstFile;
    private Long endLineInFirstFile;
    private Long endColumnInFirstFile;
    private Long startLineInSecondFile;
    private Long startColumnInSecondFile;
    private Long endLineInSecondFile;
    private Long endColumnInSecondFile;

    private String similarFragmentInFirstFile;
    private String similarFragmentInSecondFile;
}
