package com.example.plagiarismapp.entity;



import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;


@Table(name = "files")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FileProject {

    @Id
    private Long id;

    @Column("full_filename")
    private String fullFilename;

    @Column("filename")
    private String filename;

    @Column("content")
    private String content;

    @Column("repository_id")
    private Long repositoryId;
}
