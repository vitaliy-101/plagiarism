package com.example.plagiarismapp.service;

import com.example.content.RepositoryContent;
import com.example.plagiarismapp.dto.RepositoryRequest;
import lombok.RequiredArgsConstructor;
import org.example.service.CoreService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.List;

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

    public CompareResultDto comparingRepositories(RepositoryRequest request) {
        List<RepositoryContent> contents = new ArrayList<>();
        CompareResultDto compareResult = new CompareResultDto();
        compareResult.setResults(new ArrayList<>());


        for (String url : request.getRepositoryUrls()) {
            try {
                contents.add(gitService.downloadRepository(url, request.getLanguage()));
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }


        for (int i = 0; i < contents.size(); i++) {
            for (int j = i + 1; j < contents.size(); j++) {
                compareResult.getResults().add(coreService.compareRepositories(contents.get(i), contents.get(j)));
            }
        }

        return compareResult;
    }
}
