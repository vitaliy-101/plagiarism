package com.example.plagiarismapp.dto.response.file;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class SuspiciousFileResponse {
    private Long id;
    private String name;
}
