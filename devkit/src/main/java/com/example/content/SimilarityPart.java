package com.example.content;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SimilarityPart {
    private String similarFragmentInFirstFile;
    private String contextBeforeInFirstFile;
    private String contextAfterInFirstFile;
    private String similarFragmentInSecondFile;
    private String contextBeforeInSecondFile;
    private String contextAfterInSecondFile;
}
