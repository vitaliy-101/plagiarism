package com.example.content;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FileContent {
    private String fullFilename;
    private String filename;
    private String content;
}
