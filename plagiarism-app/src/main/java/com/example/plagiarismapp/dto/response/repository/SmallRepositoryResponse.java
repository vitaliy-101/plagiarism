package com.example.plagiarismapp.dto.response.repository;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SmallRepositoryResponse {
    private Long id;
    private String url;
    private String name;
    private String language;
    private String owner;
}
