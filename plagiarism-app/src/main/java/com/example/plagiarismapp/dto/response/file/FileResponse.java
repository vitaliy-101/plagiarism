package com.example.plagiarismapp.dto.response.file;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FileResponse {
    private Long id;
    private Long repositoryId;
    private Long projectId;
    private String fullFilename;
    private String filename;
    private String content;

}

