package com.example.plagiarismapp.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;


@Table(name = "statistics")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Statistic {
    @Id
    private Long id;

    @Column("number_of_repositories")
    private Long numberOfRepositories;
    @Column("number_of_files")
    private Long numberOfFiles;
    @Column("number_of_suspicious_files")
    private Long numberOfSuspiciousFiles;
    @Column("max_similarity")
    private Double maxSimilarity;
    @Column("average_similarity")
    private Double averageSimilarity;

    @Column("project_id")
    private Long projectId;

}
