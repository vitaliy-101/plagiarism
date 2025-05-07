package com.example.plagiarismapp.service;


import com.example.content.CompareResultDto;
import com.example.content.RepositoryContent;
import com.example.plagiarismapp.dto.RepositoryRequest;

import com.example.plagiarismapp.dto.response.file.SuspiciousFileResponse;
import com.example.plagiarismapp.entity.Project;
import com.example.plagiarismapp.entity.RepositoryProject;
import com.example.plagiarismapp.exception.NotFoundByIdException;
import com.example.plagiarismapp.exception.NotFoundResourceByIdException;
import com.example.plagiarismapp.repository.ProjectRepository;
import com.example.plagiarismapp.repository.RepositoryProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.service.CoreService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuples;

import java.time.Duration;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class RepositoryService {
    private static final int COMPARISON_TIMEOUT_SECONDS = 3000;
    private static final int DOWNLOAD_TIMEOUT_SECONDS = 1500;

    private final GitService gitService;
    private final CoreService coreService;

    private final ProjectRepository projectRepository;
    private final RepositoryProjectRepository repositoryProjectRepository;

    public Mono<CompareResultDto> comparingRepositoriesReactive(Flux<RepositoryContent> repositories) {
        return repositories
                .collectList()
                .flatMapMany(repoList -> Flux.range(0, repoList.size())
                        .flatMap(i -> Flux.range(i + 1, repoList.size() - (i + 1))
                                        .map(j -> Tuples.of(repoList.get(i), repoList.get(j))),
                                Runtime.getRuntime().availableProcessors()))
                .parallel()
                .runOn(Schedulers.boundedElastic())
                .flatMap(pair -> coreService.compareRepositoriesReactive(pair.getT1(), pair.getT2())
                        .onErrorResume(e -> {
                            log.warn("Comparison failed for {} and {}",
                                    pair.getT1().getRepositoryName(),
                                    pair.getT2().getRepositoryName(), e);
                            return Mono.empty();
                        }))
                .sequential()
                .collectList()
                .timeout(Duration.ofSeconds(COMPARISON_TIMEOUT_SECONDS))
                .map(results -> {
                    CompareResultDto compareResult = new CompareResultDto();
                    compareResult.setResults(results);
                    return compareResult;
                });
    }

    public Flux<RepositoryContent> processRepositoriesReactive(RepositoryRequest request) {
        return Flux.fromIterable(request.getRepositoryUrls())
                .parallel()
                .runOn(Schedulers.boundedElastic())
                .flatMap(url -> Mono.fromCallable(() -> gitService.downloadRepository(url, request.getLanguage()))
                        .subscribeOn(Schedulers.boundedElastic())
                        .timeout(Duration.ofSeconds(DOWNLOAD_TIMEOUT_SECONDS)))
                .sequential()
                .cache()
                .timeout(Duration.ofSeconds(COMPARISON_TIMEOUT_SECONDS))
                .doOnComplete(() -> log.info("SUCCESS DOWNLOAD REPO"));

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
