package org.example.service;

import com.example.content.*;
import org.example.gst.GreedyStringTiling;
import org.example.gst.PlagResult;
import org.example.normalization.strategy.NormalizationManager;
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

    public Mono<CompareTwoRepositoryDto> compareRepositoriesReactive(RepositoryContentUtil firstRepository, RepositoryContentUtil secondRepository) {
        CompareTwoRepositoryDto compareResult = new CompareTwoRepositoryDto();
        compareResult.setIdFirstRepository(firstRepository.getId());
        compareResult.setIdSecondRepository(secondRepository.getId());

        Flux<FileContentUtil> firstFiles = Flux.fromIterable(firstRepository.getFiles()).cache();
        Flux<FileContentUtil> secondFiles = Flux.fromIterable(secondRepository.getFiles()).cache();

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

    private CompareTwoFilesDto compareTwoFiles(Language language1, Language language2, FileContentUtil file1, FileContentUtil file2) {
        var result = new CompareTwoFilesDto();
        result.setIdFirstFile(file1.getId());
        result.setIdSecondFile(file2.getId());

        System.out.println("START COMPARE: " + file1.getFilename() + "; " + file2.getFilename());

        NormalizationManager normalizationManager = new NormalizationManager();
        List<TokenInfo> tokens1 = new ArrayList<>(normalizationManager.normalizeTokens(language1,
                tokenCollectorManager.collectTokensFromFile(language1, file1.getContent())));
        List<TokenInfo> tokens2 = new ArrayList<>(normalizationManager.normalizeTokens(language2,
                tokenCollectorManager.collectTokensFromFile(language2, file2.getContent())));

        String[] submission1 = getTokenNames(tokens1);
        String[] submission2 = getTokenNames(tokens2);

        System.out.println("BEFORE GREEDY: " + file1.getFilename() + "; " + file2.getFilename());
        try {
            GreedyStringTiling greedyStringTiling = new GreedyStringTiling();
            PlagResult res = greedyStringTiling.run(submission1, submission2, 9, 0.4f);
            result.setSimilarity((double) Math.round(res.getSimilarity() * 100.0) / 100.0);
            result.setSimilarityParts(res.getTiles().stream().map(t -> {
                SimilarityPart part = new SimilarityPart();

                part.setStartLineInFirstFile((long) tokens1.get(t.patternPostion).line);
                part.setStartColumnInFirstFile((long) tokens1.get(t.patternPostion).column);
                part.setEndLineInFirstFile((long) tokens1.get(t.patternPostion + t.length - 1).line);
                part.setEndColumnInFirstFile((long) tokens1.get(t.patternPostion + t.length - 1).column);

                part.setStartLineInSecondFile((long) tokens2.get(t.textPosition).line);
                part.setStartColumnInSecondFile((long) tokens2.get(t.textPosition).column);
                part.setEndLineInSecondFile((long) tokens2.get(t.textPosition + t.length - 1).line);
                part.setEndColumnInSecondFile((long) tokens2.get(t.textPosition + t.length - 1).column);
                part.setSimilarFragmentInFirstFile(getSimilarFragmentString(tokens1, t.patternPostion,
                        t.patternPostion + t.length - 1));
                part.setSimilarFragmentInSecondFile(getSimilarFragmentString(tokens2, t.textPosition,
                        t.textPosition + t.length - 1));
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


    private String[] getTokenNames(List<TokenInfo> tokens) {
        String[] tokenNames = new String[tokens.size()];
        for (int i = 0; i < tokens.size(); i++) {
            if (tokens.get(i) != null && tokens.get(i).type != -1)
                tokenNames[i] = tokens.get(i).normalizedText;
            else
                tokenNames[i] = "?";
        }
        return tokenNames;
    }

    private String getSimilarFragmentString(List<TokenInfo> tokens, int startIndex, int endIndex) {
        StringBuilder result = new StringBuilder();
        for (int i = startIndex; i <= endIndex; i++) {
            result.append(tokens.get(i).text);
        }

        return result.toString();
    }
}