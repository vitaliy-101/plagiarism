package com.example.plagiarismapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "statistics")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Statistic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nummer_of_repositories")
    private Long nummerOfRepositories;
    @Column(name = "number_of_files")
    private Long numberOfFiles;
    @Column(name = "number_of_suspicious_files")
    private Long numberOfSuspiciousFiles;
    @Column(name = "max_similarity")
    private Double maxSimilarity;
    @Column(name = "average_similarity")
    private Double averageSimilarity;

    @OneToOne
    @JoinColumn(name = "project_id")
    private Project project;

}
