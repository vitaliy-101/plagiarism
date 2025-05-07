package com.example.plagiarismapp.dto.response.project;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SmallProjectResponse {
    private Long id;
    private String name;
    private String status;
}
