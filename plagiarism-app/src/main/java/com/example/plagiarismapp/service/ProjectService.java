package com.example.plagiarismapp.service;

import com.example.content.*;
import com.example.plagiarismapp.dto.request.project.ProjectCreateRequest;
import com.example.plagiarismapp.dto.request.project.RepositoryMatchResponse;
import com.example.plagiarismapp.entity.*;
import com.example.plagiarismapp.entity.status.ProjectStatus;
import com.example.plagiarismapp.exception.NotFoundByIdException;
import com.example.plagiarismapp.exception.NotFoundResourceByIdException;
import com.example.plagiarismapp.exception.ProcessGitEcxeption;
import com.example.plagiarismapp.repository.*;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.example.service.CoreService;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectService {
    private final GitService gitService;
    private final CoreService coreService;
    private final FileProjectRepository fileRepository;
    private final RepositoryProjectRepository repositoryProjectRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final StatisticRepository statisticRepository;
    private final MatchRepository matchRepository;
    private final TileRepository tileRepository;



    public Project downloadProject(ProjectCreateRequest request) {
        var user = userRepository.findById(request.getUserId()).orElseThrow(
                () -> new NotFoundByIdException(User.class, request.getUserId()));

        List<RepositoryContent> contents = new ArrayList<>();
        for (String url : request.getRepositoryUrls()) {
            try {
                contents.add(gitService.downloadRepository(url, request.getLanguage()));
            }
            catch (Exception e) {
                throw new ProcessGitEcxeption(e.getMessage());
            }
        }

        Project project = new Project();

        List<RepositoryProject> repositories = new ArrayList<>();

        for (RepositoryContent content : contents) {
            repositories.add(computeRepository(content));
        }

        repositories.forEach(x -> x.setProject(project));

        project.setRepositories(repositories);
        project.setName(request.getName());
        project.setUser(user);
        project.setStatus(ProjectStatus.NOT_ANALYZED);

        projectRepository.save(project);

        repositoryProjectRepository.saveAll(repositories);

        return project;
    }

    public Project getProject(Long userId, Long projectId) {
        var project = projectRepository.findById(projectId).orElseThrow(
                () -> new NotFoundByIdException(Project.class, projectId));

        if (!project.getUser().getId().equals(userId)) {
            throw new NotFoundResourceByIdException(User.class, userId, Project.class, projectId);
        }

        return project;
    }


    public Statistic compareRepositories(Long projectId) {

        var project = projectRepository.findById(projectId).orElseThrow(
                () -> new NotFoundByIdException(Project.class, projectId));

        if (project.getStatus().equals(ProjectStatus.ANALYZED)) {
            return project.getStatistic();
        }

        List<RepositoryProject> repositoryProjects = project.getRepositories();

        List<CompareTwoRepositoryDto> allCompare = new ArrayList<>();
        for (int i = 0; i < repositoryProjects.size() - 1; i++) {
            for (int j = i + 1; j < repositoryProjects.size(); j++) {
                List<FileContentUtil> files = repositoryProjects.get(i).getFiles().stream()
                        .map(x -> {
                            FileContentUtil fileContent = new FileContentUtil();
                            fileContent.setId(x.getId());
                            fileContent.setContent(x.getContent());
                            return fileContent;
                        })
                        .collect(Collectors.toList());

                List<FileContentUtil> files2 = repositoryProjects.get(j).getFiles().stream()
                        .map(x -> {
                            FileContentUtil fileContent = new FileContentUtil();
                            fileContent.setId(x.getId());
                            fileContent.setContent(x.getContent());
                            return fileContent;
                        })
                        .collect(Collectors.toList());

                RepositoryContentUtil repositoryContent1 = new RepositoryContentUtil();
                repositoryContent1.setId(repositoryProjects.get(i).getId());
                repositoryContent1.setFiles(files);
                repositoryContent1.setLanguage(repositoryProjects.get(i).getLanguage());
                RepositoryContentUtil repositoryContent2 = new RepositoryContentUtil();
                repositoryContent2.setId(repositoryProjects.get(j).getId());
                repositoryContent2.setFiles(files2);
                repositoryContent2.setLanguage(repositoryProjects.get(j).getLanguage());
                Mono<CompareTwoRepositoryDto> compareResult =
                        coreService.compareRepositoriesReactive(repositoryContent1, repositoryContent2);


                allCompare.add(compareResult.block());
            }

        }

        allCompare.forEach(x -> x.getCompareFiles().forEach(y -> {
            Match match = new Match();
            System.out.println(y.getIdFirstFile() + " " + y.getIdSecondFile() + " " + y.getSimilarity());
            var fileFirst = fileRepository.findById(y.getIdFirstFile()).orElseThrow(
                    () -> new NotFoundByIdException(FileProject.class, y.getIdFirstFile()));
            var fileSecond = fileRepository.findById(y.getIdSecondFile()).orElseThrow(
                    () -> new NotFoundByIdException(FileProject.class, y.getIdSecondFile()));

            match.setFirstFile(fileFirst);
            match.setSecondFile(fileSecond);
            match.setPercentage(y.getSimilarity());

            match.setFirstRepository(fileFirst.getRepository());
            match.setSecondRepository(fileSecond.getRepository());
            match.setProject(project);

            List<Tile> tiles = new ArrayList<>();
            y.getSimilarityParts().forEach(z -> {
                Tile tile = new Tile();
                tile.setMatch(match);
                tile.setStartLineInFirstFile((long) z.getStartLineInFirstFile());
                tile.setStartLineInSecondFile((long) z.getStartLineInSecondFile());
                tile.setEndLineInFirstFile((long) z.getEndLineInFirstFile());
                tile.setEndLineInSecondFile((long)z.getEndLineInSecondFile());
                tile.setTextInFirstFile(z.getSimilarFragmentInFirstFile());
                tile.setTextInSecondFile(z.getSimilarFragmentInSecondFile());
                tiles.add(tile);
            });

            match.setTiles(tiles);
            matchRepository.save(match);

            tileRepository.saveAll(tiles);

        }));

        Statistic statistic = new Statistic();
        statistic.setNumberOfRepositories((long)project.getRepositories().size());
        statistic.setNumberOfFiles(project.getRepositories().stream().mapToLong(x -> x.getFiles().size()).sum());

        statistic.setAverageSimilarity(
                allCompare.stream().flatMap(x -> x.getCompareFiles().stream())
                                .mapToDouble(CompareTwoFilesDto::getSimilarity)
                                        .average().orElse(0.0));

        statistic.setMaxSimilarity(
                allCompare.stream()
                                .flatMap(x -> x.getCompareFiles().stream())
                                        .map(CompareTwoFilesDto::getSimilarity)
                                                .max(Double::compare).orElse(0.0));

        statistic.setNumberOfSuspiciousFiles(
                allCompare.stream().mapToLong(
                        x ->
                                x.getCompareFiles()
                                        .stream()
                                        .filter(y -> y.getSimilarity() >= 0.8)
                                        .count())
                        .sum()
        );

        statistic.setProject(project);

        project.setStatus(ProjectStatus.ANALYZED);

        projectRepository.save(project);

        statisticRepository.save(statistic);

        return statistic;

    }


    public List<RepositoryProject> getAllRepositories(Long userId, Long projectId) {
        var project = projectRepository.findById(projectId).orElseThrow(
                () -> new NotFoundByIdException(Project.class, projectId));
        if (!project.getUser().getId().equals(userId)) {
            throw new NotFoundResourceByIdException(User.class, userId, Project.class, projectId);
        }

        return project.getRepositories();
    }


    public void deleteProject(Long userId,Long projectId) {
        var project = checkProjectForExist(projectId);
        if (!project.getUser().getId().equals(userId)) {
            throw new NotFoundResourceByIdException(User.class, userId, Project.class, projectId);
        }

        projectRepository.delete(project);
    }

    private RepositoryProject computeRepository(RepositoryContent content) {
        RepositoryProject repositoryProject = new RepositoryProject();
        repositoryProject.setUrl(content.getRepositoryUrl());
        repositoryProject.setName(content.getRepositoryName());
        repositoryProject.setLanguage(content.getLanguage());
        repositoryProject.setOwner(content.getOwner());
        repositoryProject.setFiles(computeFile(content.getFiles()));

        repositoryProject.getFiles().forEach(x -> x.setRepository(repositoryProject));

        repositoryProjectRepository.save(repositoryProject);

        fileRepository.saveAll(repositoryProject.getFiles());

        return repositoryProject;
    }

    private List<FileProject> computeFile(List<FileContent> fileContents) {
        return fileContents.stream().map(x -> {
            FileProject fileProject = new FileProject();
            fileProject.setFullFilename(x.getFullFilename());
            fileProject.setFilename(x.getFilename());
            fileProject.setContent(x.getContent());
            return fileProject;
        }).collect(toList());

    }

    private Project checkProjectForExist(Long projectId) {
        return projectRepository.findById(projectId).orElseThrow(
                () -> new NotFoundByIdException(Project.class, projectId));
    }

    public List<RepositoryMatchResponse> getAllMatchRepository(Long userId, Long projectId) {
        var project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundByIdException(Project.class, projectId));

        if (!project.getUser().getId().equals(userId)) {
            throw new NotFoundResourceByIdException(User.class, userId, Project.class, projectId);
        }

        return matchRepository.findAllByProjectId(projectId)
                .stream()
                .collect(Collectors.groupingBy(
                        match -> {
                            Long id1 = match.getFirstRepository().getId();
                            Long id2 = match.getSecondRepository().getId();
                            return id1 < id2 ? id1 + "-" + id2 : id2 + "-" + id1;
                        },
                        Collectors.maxBy(Comparator.comparingDouble(Match::getPercentage))
                ))
                .values()
                .stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(match -> {
                    RepositoryMatchResponse response = new RepositoryMatchResponse();
                    response.setFirstRepositoryId(match.getFirstRepository().getId());
                    response.setFirstRepositoryOwner(match.getFirstRepository().getOwner());
                    response.setSecondRepositoryId(match.getSecondRepository().getId());
                    response.setSecondRepositoryOwner(match.getSecondRepository().getOwner());
                    response.setPercentage(match.getPercentage());
                    return response;
                })
                .collect(Collectors.toList());
    }
}
