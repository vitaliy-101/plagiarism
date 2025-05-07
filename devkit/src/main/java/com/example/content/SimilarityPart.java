package com.example.content;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SimilarityPart {
    private Long positionInFirstFile;
    private Long positionInSecondFile;
    private Long length;
    private String textInFirstFile;
    private String textInSecondFile;
}
