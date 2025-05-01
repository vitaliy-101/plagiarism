package com.example.plagiarismapp.service;


import com.example.content.CompareResultDto;
import com.example.content.RepositoryContent;
import com.example.plagiarismapp.dto.RepositoryRequest;
import lombok.RequiredArgsConstructor;
import org.example.service.CoreService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;

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

    public Mono<CompareResultDto> comparingRepositoriesReactive(Flux<RepositoryContent> repositories) {
        return repositories
                .index()
                .cache()
                .collectList()
                .flatMap(list ->
                        Flux.fromIterable(list)
                                .flatMapSequential(tuple1 -> {
                                    long i = tuple1.getT1();
                                    RepositoryContent repo1 = tuple1.getT2();

                                    return Flux.fromIterable(list)
                                            .filter(tuple2 -> tuple2.getT1() > i)
                                            .flatMap(tuple2 -> {
                                                RepositoryContent repo2 = tuple2.getT2();
                                                return coreService.compareRepositoriesReactive(repo1, repo2);
                                            });
                                })
                                .collectList()
                                .map(results -> {
                                    CompareResultDto compareResult = new CompareResultDto();
                                    compareResult.setResults(new ArrayList<>());
                                    compareResult.getResults().addAll(results);
                                    return compareResult;
                                })
                );
    }

}

