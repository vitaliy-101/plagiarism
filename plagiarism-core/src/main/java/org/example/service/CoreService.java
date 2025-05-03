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

import java.time.Duration;
import java.util.ArrayList;
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

        Flux<FileContent> firstFiles = Flux.fromIterable(firstRepository.getFiles()).cache();
        Flux<FileContent> secondFiles = Flux.fromIterable(secondRepository.getFiles()).cache();

        return firstFiles
                .flatMap(file1 -> secondFiles
                                .map(file2 -> Tuples.of(file1, file2)),
                        Runtime.getRuntime().availableProcessors())
                .parallel()
                .runOn(Schedulers.boundedElastic())
                .flatMap(tuple -> Mono.fromCallable(() ->
                                compareTwoFiles(
                                        firstRepository.getLanguage(),
                                        secondRepository.getLanguage(),
                                        tuple.getT1(),
                                        tuple.getT2()))
                        .subscribeOn(Schedulers.boundedElastic())
                        .timeout(Duration.ofSeconds(10000)))
                .sequential()
                .collectList()
                .map(compareFiles -> {
                    compareResult.setCompareFiles(compareFiles);
                    return compareResult;
                })
                .timeout(Duration.ofSeconds(250));
    }

    private CompareTwoFilesDto compareTwoFiles(Language language1, Language language2, FileContent file1, FileContent file2) {
        var result = new CompareTwoFilesDto();
        result.setFullFilenameFirst(file1.getFullFilename());
        result.setFullFilenameSecond(file2.getFullFilename());
        System.out.println("START COMPARE: " + file1.getFilename() + "; " + file2.getFilename());

        // Создаем защитную копию списка токенов
        List<TokenInfo> tokens1 = new ArrayList<>(tokenCollectorManager.collectTokensFromFile(language1, file1.getContent()));
        List<TokenInfo> tokens2 = new ArrayList<>(tokenCollectorManager.collectTokensFromFile(language2, file2.getContent()));

        String submission1 = tokensToString(tokens1);
        String submission2 = tokensToString(tokens2);

        System.out.println("BEFORE GREEDY: " + file1.getFilename() + "; " + file2.getFilename());
        try {
            PlagResult res = GreedyStringTiling.run(submission1, submission2, 9, 0.8f);
            result.setSimilarity((double) Math.round(res.getSimilarity()));
            result.setSimilarityParts(res.getTiles().stream().map(t -> {
                SimilarityPart part = new SimilarityPart();
                part.setPositionInFirstFile((long) t.patternPostion);
                part.setLength((long) t.length);
                part.setPositionInSecondFile((long) t.textPosition);
                return part;
            }).toList());
        } catch (Exception e) {
            System.out.println("EXCEPTION!!!");
            e.printStackTrace(); // Добавим вывод стека для отладки
        }
        System.out.println("SUCCESS COMPARE 2 FILES");
        System.out.println("END NAMES: " + file1.getFilename() + "; " + file2.getFilename());
        return result;
    }

    private String tokensToString(List<TokenInfo> tokens) {
        StringBuilder sb = new StringBuilder();
        // Создаем защитную копию списка для итерации
        List<TokenInfo> tokensCopy = new ArrayList<>(tokens);
        for (TokenInfo token : tokensCopy) {
            if (token != null) {
                if (token.type != -1) {
                    sb.append(token.normalizedText);
                    sb.append(' ');
                } else {
                    sb.append(' ');
                }
            } else {
                sb.append(' ');
            }
        }
        return sb.toString();
    }
}