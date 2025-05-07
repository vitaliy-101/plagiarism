package com.example.plagiarismapp.repository;

import com.example.plagiarismapp.entity.Tile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TileRepository extends JpaRepository<Tile, Long> {
}
