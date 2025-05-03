package com.example.content;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SimilarityPart {
    private Long positionInFirstFile;
    private Long positionInSecondFile;
    private Long lengthInFirstFile;
    private Long lengthInSecondFile;
}
