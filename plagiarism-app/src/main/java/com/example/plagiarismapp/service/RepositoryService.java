package com.example.plagiarismapp.service;

import com.example.plagiarismapp.dto.response.file.SuspiciousFileResponse;
import com.example.plagiarismapp.entity.Match;
import com.example.plagiarismapp.entity.Project;
import com.example.plagiarismapp.entity.RepositoryProject;
import com.example.plagiarismapp.exception.NotFoundByIdException;
import com.example.plagiarismapp.exception.NotFoundResourceByIdException;
import com.example.plagiarismapp.repository.MatchRepository;
import com.example.plagiarismapp.repository.ProjectRepository;
import com.example.plagiarismapp.repository.RepositoryProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.service.CoreService;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class RepositoryService {
    private final GitService gitService;
    private final CoreService coreService;

    private final ProjectRepository projectRepository;
    private final RepositoryProjectRepository repositoryProjectRepository;
    private final MatchRepository matchRepository;



    public List<SuspiciousFileResponse> getSuspiciousFiles(Long projectId, Long firstRepositoryId, Long secondRepositoryId) {
        projectRepository.findById(projectId).orElseThrow(
                () -> new NotFoundByIdException(Project.class, projectId));
        repositoryProjectRepository.findById(firstRepositoryId).orElseThrow(
                () -> new NotFoundResourceByIdException(RepositoryProject.class, projectId,
                        RepositoryProject.class, firstRepositoryId));
        repositoryProjectRepository.findById(secondRepositoryId).orElseThrow(
                () -> new NotFoundResourceByIdException(RepositoryProject.class, projectId,
                        RepositoryProject.class, secondRepositoryId));

        Set<SuspiciousFileResponse> result = new HashSet<>();

        List<Match> matches = matchRepository.findByFirstRepositoryIdAndSecondRepositoryId(
                firstRepositoryId, secondRepositoryId);
        matches
                .stream()
                .filter(x -> x.getPercentage() >= 0.8)
                        .map(x -> {
                            SuspiciousFileResponse response = new SuspiciousFileResponse();

                            response.setId(x.getFirstFile().getId());
                            response.setName(x.getFirstFile().getFilename());

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
