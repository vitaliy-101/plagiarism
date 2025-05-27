package com.example.plagiarismapp.mapper;


import com.example.plagiarismapp.dto.response.file.FileResponse;
import com.example.plagiarismapp.entity.FileProject;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import reactor.core.publisher.Mono;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface FileProjectMapper {

    @Mapping(target = "repositoryId", source = "repositoryId")
    FileResponse fileResponseProjectFromEntity(FileProject fileProject);

    default Mono<FileResponse> fileResponseFromMono(Mono<FileProject> fileProjectMono) {
        return fileProjectMono.map(this::fileResponseProjectFromEntity);
    }
}
