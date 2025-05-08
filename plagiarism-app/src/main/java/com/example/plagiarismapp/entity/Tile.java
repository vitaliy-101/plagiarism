package com.example.plagiarismapp.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tiles")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Tile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_line_in_first_file")
    private Long startLineInFirstFile;

    @Column(name = "end_line_in_first_file")
    private Long endLineInFirstFile;

    @Column(name = "start_line_in_second_file")
    private Long startLineInSecondFile;

    @Column(name = "end_line_in_second_file")
    private Long endLineInSecondFile;

    @Column(name = "text_in_first_file")
    private String textInFirstFile;

    @Column(name = "text_in_second_file")
    private String textInSecondFile;

    @ManyToOne
    @JoinColumn(name = "match_id")
    private Match match;
}
