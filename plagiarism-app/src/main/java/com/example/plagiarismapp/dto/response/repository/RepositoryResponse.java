package com.example.plagiarismapp.dto.response.repository;


import com.example.plagiarismapp.dto.response.file.FileResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RepositoryResponse {
    private Long id;
    private String url;
    private String name;
    private String language;
    private String owner;
    private List<FileResponse> files;
}


