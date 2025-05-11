package com.example.content;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SimilarityPart {
    private int startLineInFirstFile;
    private int startColumnInFirstFile;
    private int endLineInFirstFile;
    private int endColumnInFirstFile;
    private int startLineInSecondFile;
    private int startColumnInSecondFile;
    private int endLineInSecondFile;
    private int endColumnInSecondFile;

    private int contextLength;
    private String similarFragmentInFirstFile;
    private String similarFragmentInSecondFile;
}
