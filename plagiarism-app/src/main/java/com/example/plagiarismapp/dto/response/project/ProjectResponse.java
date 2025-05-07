package com.example.plagiarismapp.dto.response.project;

import com.example.plagiarismapp.dto.response.repository.RepositoryResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProjectResponse {
    private Long id;
    private Long userId;
    private String name;
    private List<RepositoryResponse> repositories;
}
