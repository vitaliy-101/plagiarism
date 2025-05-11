package com.example.plagiarismapp.repository;

import com.example.plagiarismapp.entity.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import reactor.core.publisher.Flux;

import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Long> {

    List<Match> findAllByFirstFileId(Long fileId);

    List<Match> findByFirstRepositoryIdAndSecondRepositoryId(Long firstRepositoryId, Long secondRepositoryId);

    List<Match> findAllByProjectId(Long projectId);
}
