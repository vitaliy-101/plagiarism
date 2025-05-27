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
    private String textInFirstFile;
    private String textAfterInFirstFile;
    private String textBeforeInFirstFile;
    private String textInSecondFile;
    private String textAfterInSecondFile;
    private String textBeforeInSecondFile;
}
