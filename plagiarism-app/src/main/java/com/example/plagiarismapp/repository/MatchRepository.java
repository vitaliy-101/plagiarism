package com.example.plagiarismapp.repository;

import com.example.plagiarismapp.entity.Match;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Long> {

    List<Match> findAllByFirstFileId(Long fileId);
}
