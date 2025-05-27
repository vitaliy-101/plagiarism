package com.example.plagiarismapp.mapper;


import com.example.plagiarismapp.dto.response.statistic.StatisticRepositoryResponse;
import com.example.plagiarismapp.entity.Statistic;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import reactor.core.publisher.Mono;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface StatisticMapper {

    StatisticRepositoryResponse statisticRepositoryResponseFromEntity(Statistic statisticRepositoryResponse);

    default Mono<StatisticRepositoryResponse> statisticRepositoryResponseFromMono(
            Mono<Statistic> statisticRepositoryResponseMono){
        return statisticRepositoryResponseMono.map(this::statisticRepositoryResponseFromEntity);
    }
}
