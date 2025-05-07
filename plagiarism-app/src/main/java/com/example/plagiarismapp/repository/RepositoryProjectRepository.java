package com.example.plagiarismapp.repository;

import com.example.plagiarismapp.entity.RepositoryProject;
import org.springframework.data.jpa.repository.JpaRepository;


public interface RepositoryProjectRepository extends JpaRepository<RepositoryProject, Long> {
}
