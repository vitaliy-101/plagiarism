package com.example.plagiarismapp.mapper;


import com.example.plagiarismapp.dto.response.project.ProjectResponse;
import com.example.plagiarismapp.dto.response.project.SmallProjectResponse;
import com.example.plagiarismapp.entity.Project;
import org.mapstruct.Mapper;

import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import reactor.core.publisher.Mono;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {RepositoryProjectMapper.class})
public interface ProjectMapper {

    @Mapping(target = "userId", source = "userId")
    ProjectResponse projectResponseFromEntity(Project project);

    @Mapping(target = "id", source = "project.id")
    @Mapping(target = "status", source = "project.status")
    SmallProjectResponse smallProjectResponseFromEntity(Project project);

    List<SmallProjectResponse> listSmallProjectResponseFromEntity(List<Project> projects);

    default Mono<SmallProjectResponse> smallProjectResponseFromMono(Mono<Project> project) {
        return project.map(this::smallProjectResponseFromEntity);
    }

    default Mono<List<SmallProjectResponse>> listSmallProjectResponseFromMono(Mono<List<Project>> projects) {
        return projects.map(this::listSmallProjectResponseFromEntity);
    }
}
