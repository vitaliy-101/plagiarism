package com.example.content;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FileContentUtil {

    private Long id;
    private String fullFilename;
    private String filename;
    private String content;
}