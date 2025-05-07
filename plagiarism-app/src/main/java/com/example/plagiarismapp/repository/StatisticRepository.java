package com.example.plagiarismapp.repository;

import com.example.plagiarismapp.entity.Statistic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatisticRepository extends JpaRepository<Statistic, Long> {
}
