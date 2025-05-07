package com.example.plagiarismapp.mapper;


import com.example.plagiarismapp.dto.response.match.SmallMatchResponse;
import com.example.plagiarismapp.entity.Match;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MatchMapper {
    List<SmallMatchResponse> listSmallMatchResponseFromListMatch(List<Match> matches);

    @Mapping(target = "firstFileId", source = "firstFile.id")
    @Mapping(target = "secondFileId", source = "secondFile.id")
    @Mapping(target = "firstFileName", source = "firstFile.filename")
    @Mapping(target = "secondFileName", source = "secondFile.filename")
    SmallMatchResponse smallMatchResponseFromEntity(Match match);

}
