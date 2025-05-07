package com.example.plagiarismapp.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "files")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FileProject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_filename")
    private String fullFilename;

    @Column(name = "filename")
    private String filename;

    @Column(name = "content")
    private String content;

    @ManyToOne
    @JoinColumn(name = "repository_id")
    private RepositoryProject repository;

}
