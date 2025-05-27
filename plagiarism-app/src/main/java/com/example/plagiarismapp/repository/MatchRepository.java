package com.example.plagiarismapp.repository;

import com.example.plagiarismapp.entity.Match;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;


import org.springframework.data.r2dbc.repository.Query;


@Repository
public interface MatchRepository extends R2dbcRepository<Match, Long> {

    @Query("SELECT * FROM matches WHERE first_file_id = :fileId")
    Flux<Match> findAllByFirstFileId(Long fileId);

    @Query("SELECT * FROM matches WHERE first_repository_id = :firstRepositoryId AND second_repository_id = :secondRepositoryId")
    Flux<Match> findByFirstRepositoryIdAndSecondRepositoryId(Long firstRepositoryId, Long secondRepositoryId);

    @Query("SELECT * FROM matches WHERE project_id = :projectId")
    Flux<Match> findAllByProjectId(Long projectId);
}
