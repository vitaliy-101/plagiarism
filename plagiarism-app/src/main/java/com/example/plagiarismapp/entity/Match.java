package com.example.plagiarismapp.entity;



import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;



@Table(name = "matches")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Match {

    @Id
    private Long id;

    @Column("percentage")
    private Double percentage;


    @Column("first_file_id")
    private Long firstFileId;

    @Column("first_file_name")
    private String firstFileName;

    @Column("second_file_id")
    private Long secondFileId;

    @Column("second_file_name")
    private String secondFileName;

    @Column("first_repository_id")
    private Long firstRepositoryId;


    @Column("second_repository_id")
    private Long secondRepositoryId;

    @Column("project_id")
    private Long projectId;

}
