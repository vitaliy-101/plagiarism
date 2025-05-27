package com.example.plagiarismapp.service;

import com.example.plagiarismapp.dto.response.file.SuspiciousFileResponse;
import com.example.plagiarismapp.entity.FileProject;
import com.example.plagiarismapp.entity.Project;
import com.example.plagiarismapp.entity.RepositoryProject;
import com.example.plagiarismapp.exception.NotFoundByIdException;
import com.example.plagiarismapp.exception.NotFoundResourceByIdException;
import com.example.plagiarismapp.repository.FileProjectRepository;
import com.example.plagiarismapp.repository.MatchRepository;
import com.example.plagiarismapp.repository.ProjectRepository;
import com.example.plagiarismapp.repository.RepositoryProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RepositoryService {
    private final ProjectRepository projectRepository;
    private final RepositoryProjectRepository repositoryProjectRepository;
    private final MatchRepository matchRepository;
    private final FileProjectRepository fileRepository;



    public Mono<List<SuspiciousFileResponse>> getSuspiciousFiles(Long projectId, Long firstRepositoryId, Long secondRepositoryId) {
        return projectRepository.findById(projectId)
                .switchIfEmpty(Mono.error(new NotFoundByIdException(Project.class, projectId)))
                .flatMap(project -> Mono.zip(
                        repositoryProjectRepository.findById(firstRepositoryId)
                                .switchIfEmpty(Mono.error(
                                        new NotFoundByIdException(RepositoryProject.class, firstRepositoryId))),
                        repositoryProjectRepository.findById(secondRepositoryId)
                                .switchIfEmpty(Mono.error(
                                        new NotFoundByIdException(RepositoryProject.class, secondRepositoryId))),
                        (firstRepo, secondRepo) -> new Object[]{project, firstRepo, secondRepo}
                )).flatMap(objects -> {
                    RepositoryProject firstRepository = (RepositoryProject) objects[1];
                    RepositoryProject secondRepository = (RepositoryProject) objects[2];

                    if (!firstRepository.getProjectId().equals(projectId)) {
                        return Mono.error(new NotFoundResourceByIdException(
                                Project.class, projectId,
                                RepositoryProject.class, firstRepositoryId));
                    }
                    if (!secondRepository.getProjectId().equals(projectId)) {
                        return Mono.error(new NotFoundResourceByIdException(
                                Project.class, projectId,
                                RepositoryProject.class, secondRepositoryId));
                    }

                    return matchRepository.
                            findByFirstRepositoryIdAndSecondRepositoryId(firstRepositoryId, secondRepositoryId)
                            .filter(match -> match.getPercentage() >= 0.4)
                            .flatMap(match -> {
                                Mono<FileProject> firstFileMono = fileRepository.findById(match.getFirstFileId());
                                Mono<FileProject> secondFileMono = fileRepository.findById(match.getSecondFileId());

                                return Mono.zip(firstFileMono, secondFileMono)
                                        .onErrorResume(e -> Mono.empty())
                                        .map(tuple -> {
                                            FileProject firstFile = tuple.getT1();
                                            SuspiciousFileResponse response = new SuspiciousFileResponse();
                                            response.setId(firstFile.getId());
                                            response.setName(firstFile.getFilename());
                                            return response;
                                        });
                            })
                            .collectList();
                });
    }


    public Mono<Void> deleteRepository(Long projectId, Long repositoryId) {
        return repositoryProjectRepository.findById(repositoryId)
                .switchIfEmpty(Mono.error(new NotFoundByIdException(RepositoryProject.class, repositoryId)))
                .flatMap(repository ->
                        projectRepository.findById(projectId)
                                .switchIfEmpty(Mono.error(new NotFoundByIdException(Project.class, projectId)))
                                .flatMap(project -> {
                                    if (repository.getProjectId().equals(project.getId())) {
                                        return repositoryProjectRepository.delete(repository);
                                    } else {
                                        return Mono.error(
                                                new NotFoundResourceByIdException(
                                                        Project.class, projectId, RepositoryProject.class, repositoryId));
                                    }
                                })
                );
    }
}


