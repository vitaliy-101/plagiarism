package com.example.plagiarismapp.repository;

import com.example.plagiarismapp.entity.RepositoryProject;


import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface RepositoryProjectRepository extends R2dbcRepository<RepositoryProject, Long> {

    @Query("SELECT * FROM repositories WHERE project_id = :projectId")
    Flux<RepositoryProject> findByProjectId(Long projectId);
}
