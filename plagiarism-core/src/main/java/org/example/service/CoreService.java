package org.example.service;

import com.example.content.*;
import org.example.gst.GreedyStringTiling;
import org.example.gst.PlagResult;
import org.example.token.TokenInfo;
import org.example.token.strategy.TokenCollectorManager;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuples;

import java.util.List;


public class CoreService {
    private final TokenCollectorManager tokenCollectorManager;

    public CoreService(TokenCollectorManager tokenCollectorManager) {
        this.tokenCollectorManager = tokenCollectorManager;
    }


    public Mono<CompareTwoRepositoryDto> compareRepositoriesReactive(RepositoryContent firstRepository, RepositoryContent secondRepository) {
        CompareTwoRepositoryDto compareResult = new CompareTwoRepositoryDto();
        compareResult.setFirstRepository(firstRepository);
        compareResult.setSecondRepository(secondRepository);

        return Flux.fromIterable(firstRepository.getFiles())
                .flatMap(file1 ->
                        Flux.fromIterable(secondRepository.getFiles())
                                .map(file2 -> Tuples.of(file1, file2))
                )
                .flatMap(tuple -> Mono.fromCallable(() -> compareTwoFiles(firstRepository, secondRepository, tuple.getT1(), tuple.getT2()))
                        .subscribeOn(Schedulers.boundedElastic())
                )
                .collectList()
                .map(compareFiles -> {
                    compareResult.setCompareFiles(compareFiles);
                    return compareResult;
                });
    }



    private CompareTwoFilesDto compareTwoFiles(RepositoryContent repo1, RepositoryContent repo2, FileContent file1, FileContent file2) {
        CompareTwoFilesDto result = new CompareTwoFilesDto();
        result.setFullFilenameFirst(file1.getFullFilename());
        result.setFullFilenameSecond(file2.getFullFilename());

        List<TokenInfo> tokens1 = tokenCollectorManager.collectTokensFromFile(repo1.getLanguage(), file1.getContent());
        List<TokenInfo> tokens2 = tokenCollectorManager.collectTokensFromFile(repo2.getLanguage(), file2.getContent());


        String submission1 = tokensToString(tokens1);
        String submission2 = tokensToString(tokens2);


        if (submission1.length() > submission2.length()) {
            String temp = submission1;
            submission1 = submission2;
            submission2 = temp;
        }

        PlagResult res = GreedyStringTiling.run(submission1, submission2, 1, 0.8f);
        result.setSimilarity((double) Math.round(res.getSimilarity()));
        result.setSimilarityParts(res.getTiles().stream().map(t -> {
            SimilarityPart part = new SimilarityPart();
            part.setPositionInFirstFile((long) t.patternPostion);
            part.setLength((long) t.length);
            part.setPositionInSecondFile((long) t.textPosition);
            return part;
        }).toList());

        return result;
    }

    private String tokensToString(List<TokenInfo> tokens) {
        StringBuilder sb = new StringBuilder();
        for (TokenInfo token : tokens) {
            sb.append((char) token.type);
        }
        return sb.toString();
    }
}

