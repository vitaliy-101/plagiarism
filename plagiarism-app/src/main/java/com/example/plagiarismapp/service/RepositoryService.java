package com.example.plagiarismapp.service;


import com.example.plagiarismapp.dto.response.file.SuspiciousFileResponse;
import com.example.plagiarismapp.entity.Project;
import com.example.plagiarismapp.entity.RepositoryProject;
import com.example.plagiarismapp.exception.NotFoundByIdException;
import com.example.plagiarismapp.exception.NotFoundResourceByIdException;
import com.example.plagiarismapp.repository.ProjectRepository;
import com.example.plagiarismapp.repository.RepositoryProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RepositoryService {

    private final ProjectRepository projectRepository;
    private final RepositoryProjectRepository repositoryProjectRepository;

    public List<SuspiciousFileResponse> getSuspiciousFiles(Long projectId, Long repositoryId) {
        var project = projectRepository.findById(projectId).orElseThrow(
                () -> new NotFoundByIdException(Project.class, projectId));
        repositoryProjectRepository.findById(repositoryId).orElseThrow(
                () -> new NotFoundResourceByIdException(RepositoryProject.class, projectId,
                        RepositoryProject.class, repositoryId));

        Set<SuspiciousFileResponse> result = new HashSet<>();

        project.getMatches()
                .stream()
                .filter(x -> x.getPercentage() >= 0.8 &&
                        ((x.getFirstFile().getRepository().getId().equals(repositoryId)
                                && !x.getSecondFile().getRepository().getId().equals(repositoryId)) ||
                        (x.getSecondFile().getRepository().getId().equals(repositoryId)
                                && !x.getFirstFile().getRepository().getId().equals(repositoryId))))
                .map(x -> {
                    SuspiciousFileResponse response = new SuspiciousFileResponse();
                    if (x.getFirstFile().getRepository().getId().equals(repositoryId)) {
                        response.setId(x.getFirstFile().getId());
                        response.setName(x.getFirstFile().getFilename());
                    } else {
                        response.setId(x.getSecondFile().getId());
                        response.setName(x.getSecondFile().getFilename());
                    }

                    return response;
                })
                .forEach(result::add);

        return result.stream().toList();
    }

    public void deleteRepository(Long projectId, Long repositoryId) {
        var repository = repositoryProjectRepository.findById(repositoryId).orElseThrow(
                () -> new NotFoundByIdException(RepositoryProject.class, repositoryId));

        var project = projectRepository.findById(projectId).orElseThrow(
                () -> new NotFoundByIdException(Project.class, projectId));

        if (repository.getProject().getId().equals(project.getId())) {
            repositoryProjectRepository.delete(repository);
        } else {
            throw new NotFoundResourceByIdException(Project.class, projectId, RepositoryProject.class, repositoryId);
        }

    }
}
