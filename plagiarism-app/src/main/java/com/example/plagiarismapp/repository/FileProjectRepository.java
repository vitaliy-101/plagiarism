package com.example.plagiarismapp.repository;

import com.example.plagiarismapp.entity.FileProject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileProjectRepository extends JpaRepository<FileProject, Long> {
}
