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
public class RepositoryContentUtil {

    private Long id;
    private String repositoryUrl;
    private String repositoryName;
    private String owner;
    private List<FileContentUtil> files;
    private Language language;
}