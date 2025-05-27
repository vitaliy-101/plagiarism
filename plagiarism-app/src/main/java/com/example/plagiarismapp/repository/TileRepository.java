package com.example.plagiarismapp.repository;

import com.example.plagiarismapp.entity.Tile;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;


@Repository
public interface TileRepository extends R2dbcRepository<Tile, Long> {

    @Query("SELECT * FROM tiles WHERE match_id = :id")
    Flux<Tile> findByMatchId(Long id);
}
