package com.example.plagiarismapp.service;

import com.example.content.RepositoryContent;
import com.example.plagiarismapp.dto.RepositoryRequest;
import lombok.RequiredArgsConstructor;
import org.example.service.CoreService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
public class RepositoryService {
    private final GitService gitService;
    private final CoreService coreService;

    public Flux<RepositoryContent> processRepositoriesReactive(RepositoryRequest request) {
        return Flux.fromIterable(request.getRepositoryUrls())
                .parallel()
                .runOn(Schedulers.boundedElastic())
                .flatMap(url -> Mono.fromCallable(() -> gitService.downloadRepository(url, request.getLanguage())))
                .sequential();
    }
}
