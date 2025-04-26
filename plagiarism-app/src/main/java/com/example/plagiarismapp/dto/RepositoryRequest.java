package com.example.plagiarismapp.dto;

import com.example.content.Language;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RepositoryRequest {
    private List<String> repositoryUrls;
    private Language language;
}