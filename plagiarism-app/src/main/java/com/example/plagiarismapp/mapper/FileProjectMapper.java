package com.example.plagiarismapp.mapper;


import com.example.plagiarismapp.dto.response.file.FileResponse;
import com.example.plagiarismapp.entity.FileProject;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface FileProjectMapper {

    @Mapping(target = "projectId", source = "fileProject.repository.project.id")
    @Mapping(target = "repositoryId", source = "fileProject.repository.id")
    FileResponse fileResponseProjectFromEntity(FileProject fileProject);
}
