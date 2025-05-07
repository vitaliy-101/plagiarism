package com.example.plagiarismapp.mapper;


import com.example.plagiarismapp.dto.response.statistic.StatisticRepositoryResponse;
import com.example.plagiarismapp.entity.Statistic;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface StatisticMapper {

    StatisticRepositoryResponse statisticRepositoryResponseFromEntity(Statistic statisticRepositoryResponse);
}
