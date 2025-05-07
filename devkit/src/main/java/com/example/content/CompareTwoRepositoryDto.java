package com.example.content;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CompareTwoRepositoryDto {
    private Long idFirstRepository;
    private Long idSecondRepository;
    private List<CompareTwoFilesDto> compareFiles;
}
