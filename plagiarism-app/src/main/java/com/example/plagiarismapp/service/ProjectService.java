package com.example.plagiarismapp.service;

import com.example.content.*;
import com.example.plagiarismapp.dto.request.project.ProjectCreateRequest;
import com.example.plagiarismapp.dto.request.project.RepositoryMatchResponse;
import com.example.plagiarismapp.entity.*;
import com.example.plagiarismapp.entity.status.ProjectStatus;
import com.example.plagiarismapp.exception.NotFoundByIdException;
import com.example.plagiarismapp.exception.NotFoundResourceByIdException;
import com.example.plagiarismapp.repository.*;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.example.service.CoreServiceReactive;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {
    private final GitService gitService;
    private final CoreServiceReactive coreServiceReactive;
    private final FileProjectRepository fileRepository;
    private final RepositoryProjectRepository repositoryProjectRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final StatisticRepository statisticRepository;
    private final MatchRepository matchRepository;
    private final TileRepository tileRepository;


    public Mono<Project> downloadProject(ProjectCreateRequest request) {
        return userRepository.findById(request.getUserId())
                .switchIfEmpty(Mono.error(new NotFoundByIdException(User.class, request.getUserId())))
                .flatMap(user -> {
                    List<Mono<RepositoryContent>> contentMonos = request.getRepositoryUrls().stream()
                            .map(url -> gitService.downloadRepository(url, request.getLanguage()))
                            .collect(Collectors.toList());


                    return Flux.concat(contentMonos)
                            .flatMap(this::computeRepository)
                            .collectList()
                            .flatMap(repositories -> {
                                Project project = new Project();
                                project.setName(request.getName());
                                project.setUserId(user.getId());
                                project.setStatus(ProjectStatus.NOT_ANALYZED);
                                return projectRepository.save(project)
                                        .flatMap(savedProject -> {
                                            List<Mono<RepositoryProject>> saveOperations = repositories.stream()
                                                    .peek(repo -> repo.setProjectId(savedProject.getId()))
                                                    .map(repositoryProjectRepository::save)
                                                    .collect(Collectors.toList());


                                            return Flux.merge(saveOperations)
                                                    .then(compareRepositories(savedProject.getId()))
                                                    .then(projectRepository.findById(savedProject.getId()));
                                        });
                            });
                });
    }


    public Mono<Project> getProject(Long userId, Long projectId) {
        return projectRepository.findById(projectId)
                .switchIfEmpty(Mono.error(new NotFoundByIdException(Project.class, projectId)))
                .flatMap(project -> {
                    if (!project.getUserId().equals(userId)) {
                        return Mono.error(
                                new NotFoundResourceByIdException(User.class, userId, Project.class, projectId));
                    }
                    return Mono.just(project);
                });
    }

    public Mono<Statistic> compareRepositories(Long projectId) {
        return projectRepository.findById(projectId)
                .switchIfEmpty(Mono.error(new NotFoundByIdException(Project.class, projectId)))
                .flatMap(project -> {
                    if (project.getStatus().equals(ProjectStatus.ANALYZED)) {
                        return statisticRepository.findByProjectId(projectId);
                    }

                    return repositoryProjectRepository.findByProjectId(projectId)
                            .collectList()
                            .flatMap(repos -> {
                                List<Mono<CompareTwoRepositoryDto>> comparisons = new ArrayList<>();

                                // Для каждой пары репозиториев
                                for (int i = 0; i < repos.size() - 1; i++) {
                                    for (int j = i + 1; j < repos.size(); j++) {
                                        RepositoryProject repo1 = repos.get(i);
                                        RepositoryProject repo2 = repos.get(j);

                                        Mono<List<FileProject>> files1 = fileRepository.findByRepositoryId(repo1.getId())
                                                .collectList();

                                        Mono<List<FileProject>> files2 = fileRepository.findByRepositoryId(repo2.getId())
                                                .collectList();

                                        Mono<CompareTwoRepositoryDto> comparison = Mono.zip(files1, files2)
                                                .flatMap(tuple -> {
                                                    List<FileProject> filesRepo1 = tuple.getT1();
                                                    List<FileProject> filesRepo2 = tuple.getT2();

                                                    RepositoryContentUtil r1 = new RepositoryContentUtil();
                                                    r1.setId(repo1.getId());
                                                    r1.setLanguage(Language.valueOf(repo1.getLanguage()));
                                                    r1.setFiles(filesRepo1.stream()
                                                            .map(f -> new FileContentUtil(f.getId(), f.getFullFilename(), f.getFilename(), f.getContent()))
                                                            .collect(Collectors.toList()));

                                                    RepositoryContentUtil r2 = new RepositoryContentUtil();
                                                    r2.setId(repo2.getId());
                                                    r2.setLanguage(Language.valueOf(repo2.getLanguage()));
                                                    r2.setFiles(filesRepo2.stream()
                                                            .map(f -> new FileContentUtil(f.getId(), f.getFullFilename(), f.getFilename(), f.getContent()))
                                                            .collect(Collectors.toList()));

                                                    return coreServiceReactive.compareRepositoriesReactive(r1, r2);
                                                });

                                        comparisons.add(comparison);
                                    }
                                }

                                return Flux.merge(comparisons).collectList();
                            })
                            .flatMap(allCompare -> Flux.fromIterable(allCompare)
                                    .flatMap(dto -> Flux.fromIterable(dto.getCompareFiles()))
                                    .flatMap(fileDto -> {
                                        Mono<FileProject> file1Mono = fileRepository.findById(fileDto.getIdFirstFile())
                                                .switchIfEmpty(Mono.error(
                                                        new NotFoundByIdException(FileProject.class, fileDto.getIdFirstFile())));

                                        Mono<FileProject> file2Mono = fileRepository.findById(fileDto.getIdSecondFile())
                                                .switchIfEmpty(Mono.error(
                                                        new NotFoundByIdException(FileProject.class, fileDto.getIdSecondFile())));

                                        return Mono.zip(file1Mono, file2Mono)
                                                .flatMap(tuple -> {
                                                    FileProject file1 = tuple.getT1();
                                                    FileProject file2 = tuple.getT2();

                                                    Match match = new Match();
                                                    match.setFirstFileId(file1.getId());
                                                    match.setFirstFileName(file1.getFilename());
                                                    match.setSecondFileId(file2.getId());
                                                    match.setSecondFileName(file2.getFilename());
                                                    match.setPercentage(Math.round(fileDto.getSimilarity() * 100.0) / 100.0);
                                                    match.setFirstRepositoryId(file1.getRepositoryId());
                                                    match.setSecondRepositoryId(file2.getRepositoryId());
                                                    match.setProjectId(project.getId());

                                                    return matchRepository.save(match)
                                                            .flatMap(savedMatch -> {
                                                                List<Mono<Tile>> tileSaves = fileDto.getSimilarityParts().stream()
                                                                        .map(part -> {
                                                                            Tile tile = new Tile();
                                                                            tile.setMatchId(savedMatch.getId());
                                                                            tile.setStartLineInFirstFile((long) part.getStartLineInFirstFile());
                                                                            tile.setStartLineInSecondFile((long) part.getStartLineInSecondFile());
                                                                            tile.setEndLineInFirstFile((long) part.getEndLineInFirstFile());
                                                                            tile.setEndLineInSecondFile((long) part.getEndLineInSecondFile());
                                                                            tile.setTextInFirstFile(part.getSimilarFragmentInFirstFile());
                                                                            tile.setTextAfterContextInFirstFile(part.getContextAfterInFirstFile());
                                                                            tile.setTextBeforeContextInFirstFile(part.getContextBeforeInFirstFile());
                                                                            tile.setTextInSecondFile(part.getSimilarFragmentInSecondFile());
                                                                            tile.setTextAfterContextInSecondFile(part.getContextAfterInSecondFile());
                                                                            tile.setTextBeforeContextInSecondFile(part.getContextBeforeInSecondFile());
                                                                            return tileRepository.save(tile);
                                                                        })
                                                                        .collect(Collectors.toList());

                                                                return Flux.merge(tileSaves).then(Mono.just(savedMatch));
                                                            });
                                                });
                                    })
                                    .then(Mono.defer(() -> repositoryProjectRepository.findByProjectId(projectId).collectList()
                                            .flatMap(repos -> fileRepository.countByProjectId(projectId)
                                                    .flatMap(fileCount -> {
                                                        // Calculate statistics
                                                        List<Double> similarities = allCompare.stream()
                                                                .flatMap(x -> x.getCompareFiles().stream())
                                                                .map(CompareTwoFilesDto::getSimilarity)
                                                                .collect(Collectors.toList());

                                                        DoubleSummaryStatistics stats = similarities.stream()
                                                                .mapToDouble(Double::doubleValue)
                                                                .summaryStatistics();

                                                        long suspiciousFiles = similarities.stream()
                                                                .filter(sim -> sim >= 0.4)
                                                                .count();

                                                        // Create and configure statistic
                                                        Statistic statistic = new Statistic();
                                                        statistic.setNumberOfRepositories((long) repos.size());
                                                        statistic.setNumberOfFiles(fileCount);
                                                        statistic.setAverageSimilarity(Math.round(stats.getAverage() * 100.0) / 100.0);
                                                        statistic.setMaxSimilarity(stats.getMax());
                                                        statistic.setNumberOfSuspiciousFiles(suspiciousFiles);
                                                        statistic.setProjectId(project.getId());

                                                        // Update project
                                                        project.setStatus(ProjectStatus.ANALYZED);

                                                        return projectRepository.save(project)
                                                                .then(statisticRepository.save(statistic))
                                                                .thenReturn(statistic);
                                                    })))));
                });
    }


    public Mono<List<RepositoryProject>> getAllRepositories(Long userId, Long projectId) {
        return projectRepository.findById(projectId)
                .switchIfEmpty(Mono.error(new NotFoundByIdException(Project.class, projectId)))
                .flatMap(project -> {
                    if (!project.getUserId().equals(userId)) {
                        return Mono.error(new NotFoundResourceByIdException(
                                User.class, userId,
                                Project.class, projectId
                        ));
                    }
                    return repositoryProjectRepository.findByProjectId(projectId)
                            .collectList();
                });
    }


    public Mono<Void> deleteProject(Long userId, Long projectId) {
        return projectRepository.findById(projectId)
                .switchIfEmpty(Mono.error(new NotFoundByIdException(Project.class, projectId)))
                .flatMap(project -> {
                    if (!project.getUserId().equals(userId)) {
                        return Mono.error(
                                new NotFoundResourceByIdException(User.class, userId, Project.class, projectId));
                    }
                    return projectRepository.delete(project);
                });
    }


    private Mono<RepositoryProject> computeRepository(RepositoryContent content) {
        RepositoryProject repositoryProject = new RepositoryProject();
        repositoryProject.setUrl(content.getRepositoryUrl());
        repositoryProject.setName(content.getRepositoryName());
        repositoryProject.setLanguage(content.getLanguage().toString());
        repositoryProject.setOwner(content.getOwner());

        List<FileProject> files = content.getFiles().stream()
                .map(fileContent -> {
                    FileProject file = new FileProject();
                    file.setContent(fileContent.getContent());
                    file.setFilename(fileContent.getFilename());
                    file.setFullFilename(fileContent.getFullFilename());
                    return file;
                })
                .toList();

        return repositoryProjectRepository.save(repositoryProject)
                .flatMap(savedRepo -> Flux.fromIterable(files)
                        .doOnNext(file -> file.setRepositoryId(savedRepo.getId()))
                        .flatMap(fileRepository::save)
                        .collectList()
                        .thenReturn(savedRepo));
    }


    public Mono<List<RepositoryMatchResponse>> getAllMatchRepository(Long userId, Long projectId) {
        return projectRepository.findById(projectId)
                .switchIfEmpty(Mono.error(new NotFoundByIdException(Project.class, projectId)))
                .flatMap(project -> {
                    if (!project.getUserId().equals(userId)) {
                        return Mono.error(
                                new NotFoundResourceByIdException(User.class, userId, Project.class, projectId));
                    }

                    return matchRepository.findAllByProjectId(projectId)
                            .collectList()
                            .flatMap(matches -> {
                                Map<String, Match> groupedMatches = matches.stream()
                                        .collect(Collectors.groupingBy(
                                                match -> {
                                                    Long id1 = match.getFirstRepositoryId();
                                                    Long id2 = match.getSecondRepositoryId();
                                                    return id1 < id2 ? id1 + "-" + id2 : id2 + "-" + id1;
                                                },
                                                Collectors.maxBy(Comparator.comparingDouble(Match::getPercentage))
                                        ))
                                        .values()
                                        .stream()
                                        .filter(Optional::isPresent)
                                        .map(Optional::get)
                                        .collect(Collectors.toMap(
                                                match -> match.getFirstRepositoryId() + "-" + match.getSecondRepositoryId(),
                                                match -> match
                                        ));


                                return Flux.fromIterable(groupedMatches.values())
                                        .flatMap(match -> {
                                            RepositoryMatchResponse response = new RepositoryMatchResponse();
                                            response.setFirstRepositoryId(match.getFirstRepositoryId());
                                            response.setSecondRepositoryId(match.getSecondRepositoryId());
                                            response.setPercentage(match.getPercentage());

                                            Mono<String> firstOwner =
                                                    repositoryProjectRepository.findById(match.getFirstRepositoryId())
                                                    .map(RepositoryProject::getOwner)
                                                    .defaultIfEmpty("Unknown");

                                            Mono<String> secondOwner =
                                                    repositoryProjectRepository.findById(match.getSecondRepositoryId())
                                                    .map(RepositoryProject::getOwner)
                                                    .defaultIfEmpty("Unknown");


                                            return Mono.zip(firstOwner, secondOwner)
                                                    .map(tuple -> {
                                                        response.setFirstRepositoryOwner(tuple.getT1());
                                                        response.setSecondRepositoryOwner(tuple.getT2());
                                                        return response;
                                                    });
                                        })
                                        .collectList();
                            });
                });
    }
}
