package com.example.content;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CompareTwoFilesDto {
    private Long idFirstFile;
    private Long idSecondFile;
    private Double similarity;
    private List<SimilarityPart> similarityParts;
}