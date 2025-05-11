package com.example.plagiarismapp.entity;


import com.example.content.Language;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "repositories")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RepositoryProject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "url")
    private String url;

    @Column(name = "name_repository")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "language")
    private Language language;

    @Column(name = "owner")
    private String owner;

    @OneToMany(mappedBy = "repository")
    private List<FileProject> files;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

}
