package com.example.plagiarismapp.repository;

import com.example.plagiarismapp.entity.Statistic;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface StatisticRepository extends R2dbcRepository<Statistic, Long> {

    @Query("SELECT * FROM statistics WHERE project_id = :projectId")
    Mono<Statistic> findByProjectId(Long projectId);
}
