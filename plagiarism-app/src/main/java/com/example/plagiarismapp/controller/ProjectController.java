package com.example.plagiarismapp.controller;

import com.example.plagiarismapp.dto.request.project.ProjectCreateRequest;

import com.example.plagiarismapp.dto.request.project.RepositoryMatchResponse;

import com.example.plagiarismapp.dto.response.project.SmallProjectResponse;
import com.example.plagiarismapp.dto.response.repository.SmallRepositoryResponse;
import com.example.plagiarismapp.dto.response.statistic.StatisticRepositoryResponse;
import com.example.plagiarismapp.mapper.MatchMapper;
import com.example.plagiarismapp.mapper.ProjectMapper;

import com.example.plagiarismapp.mapper.RepositoryProjectMapper;
import com.example.plagiarismapp.mapper.StatisticMapper;
import com.example.plagiarismapp.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;


@RestController
@RequestMapping("api/v1/project")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;
    private final ProjectMapper projectMapper;
    private final StatisticMapper statisticMapper;
    private final MatchMapper matchMapper;
    private final RepositoryProjectMapper repositoryMapper;


    @PostMapping("/create")
    public Mono<SmallProjectResponse> download(@RequestBody ProjectCreateRequest request) {
        return projectMapper.smallProjectResponseFromMono(projectService.downloadProject(request));
    }

    @PostMapping("compare/{projectId}")
    public Mono<StatisticRepositoryResponse> compare(@PathVariable("projectId") Long projectId) {
        return statisticMapper.statisticRepositoryResponseFromMono(projectService.compareRepositories(projectId));
    }

    @GetMapping("/{userId}/{projectId}")
    public Mono<SmallProjectResponse> getProjectStatus(@PathVariable("userId") Long userId, @PathVariable("projectId") Long projectId) {
        return projectMapper.smallProjectResponseFromMono(projectService.getProject(userId, projectId));
    }


    @GetMapping("repository/all/{userId}/{projectId}")
    public Mono<List<SmallRepositoryResponse>> getAllRepositories(@PathVariable("userId") Long userId, @PathVariable("projectId") Long projectId) {
        return repositoryMapper.listSmallRepositoryResponseFromListMono(projectService.getAllRepositories(userId, projectId));
    }

    @GetMapping("all/match/repository/{userId}/{projectId}")
    public Mono<List<RepositoryMatchResponse>> getAllMatchRepository(@PathVariable("userId") Long userId, @PathVariable("projectId") Long projectId) {
        return projectService.getAllMatchRepository(userId, projectId);
    }

    @DeleteMapping("/delete/{userId}/{projectId}")
    public Mono<Void> deleteProject(@PathVariable("userId") Long userId, @PathVariable("projectId")Long projectId) {
        return projectService.deleteProject(userId, projectId);
    }

}
