package com.example.plagiarismapp.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "matches")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "percentage")
    private Double percentage;

    @OneToMany(mappedBy = "match")
    private List<Tile> tiles;

    @ManyToOne
    @JoinColumn(name = "first_file_id")
    private FileProject firstFile;

    @ManyToOne
    @JoinColumn(name = "second_file_id")
    private FileProject secondFile;

    @ManyToOne
    @JoinColumn(name = "first_repository_id")
    private RepositoryProject firstRepository;

    @ManyToOne
    @JoinColumn(name = "second_repository_id")
    private RepositoryProject secondRepository;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

}
