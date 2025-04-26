package com.example.content;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class RepositoryContent {
    private String repositoryUrl;
    private String repositoryName;
    private String owner;
    private List<FileContent> files;
    private Language language;
}
