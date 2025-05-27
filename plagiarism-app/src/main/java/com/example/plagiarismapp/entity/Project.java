package com.example.plagiarismapp.entity;


import com.example.plagiarismapp.entity.status.ProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.List;


@Table(name = "projects")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Project {

    @Id
    private Long id;

    @Column("name_project")
    private String name;

    @Column("status")
    private ProjectStatus status;

    @Column("user_id")
    private Long userId;

}
