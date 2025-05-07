package com.example.plagiarismapp.mapper;


import com.example.plagiarismapp.dto.response.repository.RepositoryResponse;
import com.example.plagiarismapp.dto.response.repository.SmallRepositoryResponse;
import com.example.plagiarismapp.entity.RepositoryProject;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {FileProjectMapper.class})
public interface RepositoryProjectMapper {

    RepositoryResponse repositoryProjectFromEntity(RepositoryProject repositoryProject);

    SmallRepositoryResponse smallRepositoryResponseFromEntity(RepositoryProject repositoryProject);

    List<SmallRepositoryResponse> listSmallRepositoryResponseFromListEntity(List<RepositoryProject> repositoryProjects);
}
