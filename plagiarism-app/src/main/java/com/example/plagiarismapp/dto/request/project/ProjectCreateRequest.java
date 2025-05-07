package com.example.plagiarismapp.dto.request.project;

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
public class ProjectCreateRequest {
    private Long userId;
    private String name;
    private List<String> repositoryUrls;
    private Language language;
}