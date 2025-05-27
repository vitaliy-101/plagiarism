package com.example.plagiarismapp.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.List;


@Table(name = "repositories")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RepositoryProject {
    @Id
    private Long id;

    @Column("url")
    private String url;

    @Column("name_repository")
    private String name;

    @Column("language")
    private String language;

    @Column("owner")
    private String owner;

    @Column("project_id")
    private Long projectId;

}
