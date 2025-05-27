package com.example.plagiarismapp.entity;



import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.*;

@Table(name = "tiles")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Tile {
    @Id
    private Long id;

    @Column("start_line_in_first_file")
    private Long startLineInFirstFile;

    @Column("end_line_in_first_file")
    private Long endLineInFirstFile;

    @Column("start_line_in_second_file")
    private Long startLineInSecondFile;

    @Column("end_line_in_second_file")
    private Long endLineInSecondFile;

    @Column("text_in_first_file")
    private String textInFirstFile;

    @Column("text_in_second_file")
    private String textInSecondFile;

    @Column("text_after_context_in_first_file")
    private String textAfterContextInFirstFile;

    @Column("text_after_context_in_second_file")
    private String textAfterContextInSecondFile;

    @Column("text_before_context_in_first_file")
    private String textBeforeContextInFirstFile;

    @Column("text_before_context_in_second_file")
    private String textBeforeContextInSecondFile;

    @Column("match_id")
    private Long matchId;
}
