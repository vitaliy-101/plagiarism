package com.example.plagiarismapp.repository;

import com.example.plagiarismapp.entity.FileProject;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface FileProjectRepository extends R2dbcRepository<FileProject, Long> {

    @Query("SELECT * FROM files WHERE id = :id")
    Mono<FileProject> findById(Long id);

    @Query("SELECT * FROM files WHERE repository_id = :id")
    Flux<FileProject> findByRepositoryId(Long id);

    @Query("SELECT COUNT(*) FROM files f JOIN repositories r ON f.repository_id = r.id WHERE r.project_id = :projectId")
    Mono<Long> countByProjectId(Long projectId);
}
