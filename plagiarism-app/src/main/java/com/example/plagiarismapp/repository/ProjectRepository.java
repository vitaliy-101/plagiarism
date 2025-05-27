package com.example.plagiarismapp.repository;

import com.example.plagiarismapp.entity.Project;


import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ProjectRepository extends R2dbcRepository<Project, Long> {

    @Query("SELECT * FROM projects WHERE user_id = :userId")
    Flux<Project> findAllByUserId(Long userId);
}

