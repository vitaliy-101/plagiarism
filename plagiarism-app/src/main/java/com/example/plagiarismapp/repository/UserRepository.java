package com.example.plagiarismapp.repository;

import com.example.plagiarismapp.entity.User;


import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends R2dbcRepository<User, Long> {

}

