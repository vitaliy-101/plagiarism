package com.example.plagiarismapp.service;

import com.example.plagiarismapp.dto.RepositoryContent;
import com.example.plagiarismapp.dto.RepositoryRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
public class RepositoryService {
    private final GitService gitService;

    public Flux<RepositoryContent> processRepositoriesReactive(RepositoryRequest request) {
        return Flux.fromIterable(request.getRepositoryUrls())
                .parallel()
                .runOn(Schedulers.boundedElastic())
                .flatMap(url -> Mono.fromCallable(() -> gitService.downloadRepository(url, request.getFileExtension())))
                .sequential();
    }
}
