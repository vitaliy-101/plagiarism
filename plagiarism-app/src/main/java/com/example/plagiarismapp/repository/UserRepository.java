package com.example.plagiarismapp.repository;

import com.example.plagiarismapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
