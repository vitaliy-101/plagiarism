package com.example.plagiarismapp.controller;


import com.example.content.CompareResultDto;
import com.example.content.RepositoryContent;
import com.example.plagiarismapp.dto.RepositoryRequest;
import com.example.plagiarismapp.service.RepositoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("api/v1/repository")
@RequiredArgsConstructor
public class RepositoryController {
    private final RepositoryService service;
    @PostMapping
    public Flux<RepositoryContent> compareV2(@RequestBody RepositoryRequest request) {
        return service.processRepositoriesReactive(request);
    }

    @PostMapping("/compare")
    public Mono<CompareResultDto> compareV3(@RequestBody RepositoryRequest request) {
        return service.comparingRepositoriesReactive(service.processRepositoriesReactive(request));
    }
}
