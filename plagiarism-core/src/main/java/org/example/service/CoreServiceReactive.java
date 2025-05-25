package org.example.service;

import com.example.content.*;
import org.example.gst.GreedyStringTilingReactive;
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


public class CoreServiceReactive {
    private final TokenCollectorManager tokenCollectorManager;

    public CoreServiceReactive(TokenCollectorManager tokenCollectorManager) {
        this.tokenCollectorManager = tokenCollectorManager;
    }

    public Mono<CompareTwoRepositoryDto> compareRepositoriesReactive(RepositoryContentUtil firstRepository, RepositoryContentUtil secondRepository) {
        CompareTwoRepositoryDto compareResult = new CompareTwoRepositoryDto();
        compareResult.setIdFirstRepository(firstRepository.getId());
        compareResult.setIdSecondRepository(secondRepository.getId());

        Flux<FileContentUtil> firstFiles = Flux.fromIterable(firstRepository.getFiles()).cache();
        Flux<FileContentUtil> secondFiles = Flux.fromIterable(secondRepository.getFiles()).cache();

        return firstFiles
                .flatMap(file1 -> secondFiles.map(file2 -> Tuples.of(file1, file2)),
                        Runtime.getRuntime().availableProcessors())
                .parallel()
                .runOn(Schedulers.boundedElastic())
                .flatMap(tuple -> compareTwoFilesReactive(
                        firstRepository.getLanguage(),
                        secondRepository.getLanguage(),
                        tuple.getT1(),
                        tuple.getT2()))
                .sequential()
                .collectList()
                .map(compareFiles -> {
                    compareResult.setCompareFiles(compareFiles);
                    return compareResult;
                })
                .timeout(Duration.ofSeconds(250));
    }

    private Mono<CompareTwoFilesDto> compareTwoFilesReactive(Language language1, Language language2, FileContentUtil file1, FileContentUtil file2) {
        return Mono.fromCallable(() -> {
            CompareTwoFilesDto result = new CompareTwoFilesDto();
            result.setIdFirstFile(file1.getId());
            result.setIdSecondFile(file2.getId());

            NormalizationManager normalizationManager = new NormalizationManager();
            List<TokenInfo> tokens1 = new ArrayList<>(normalizationManager.normalizeTokens(
                    language1,
                    tokenCollectorManager.collectTokensFromFile(language1, file1.getContent())
            ));
            List<TokenInfo> tokens2 = new ArrayList<>(normalizationManager.normalizeTokens(
                    language2,
                    tokenCollectorManager.collectTokensFromFile(language2, file2.getContent())
            ));

            return Tuples.of(result, tokens1, tokens2);
        }).flatMap(tuple -> {
            CompareTwoFilesDto result = tuple.getT1();
            List<TokenInfo> tokens1 = tuple.getT2();
            List<TokenInfo> tokens2 = tuple.getT3();
            String[] submission1 = getTokenNames(tokens1);
            String[] submission2 = getTokenNames(tokens2);

            GreedyStringTilingReactive gst = new GreedyStringTilingReactive();

            return gst.runReactive(submission1, submission2, 9, 0.4f)
                    .map(res -> {
                        result.setSimilarity((double) Math.round(res.getSimilarity()));
                        result.setSimilarityParts(res.getTiles().stream().map(t -> {
                            SimilarityPart part = new SimilarityPart();

                            part.setStartLineInFirstFile(tokens1.get(t.patternPostion).getLine());
                            part.setStartColumnInFirstFile(tokens1.get(t.patternPostion).getColumn());
                            part.setEndLineInFirstFile(tokens1.get(t.patternPostion + t.length - 1).getLine());
                            part.setEndColumnInFirstFile(tokens1.get(t.patternPostion + t.length - 1).getColumn()
                                    + tokens1.get(t.patternPostion + t.length - 1).getLength() - 1);

                            part.setStartLineInSecondFile(tokens2.get(t.textPosition).getLine());
                            part.setStartColumnInSecondFile(tokens2.get(t.textPosition).getColumn());
                            part.setEndLineInSecondFile(tokens2.get(t.textPosition + t.length - 1).getLine());
                            part.setEndColumnInSecondFile(tokens2.get(t.textPosition + t.length - 1).getColumn()
                                    + tokens2.get(t.textPosition + t.length - 1).getLength() - 1);

                            part.setContextLength(2);
                            part.setSimilarFragmentInFirstFile(getSimilarFragmentString(file1, part.getStartLineInFirstFile(),
                                    part.getEndLineInFirstFile(), part.getContextLength()));
                            part.setSimilarFragmentInSecondFile(getSimilarFragmentString(file2, part.getStartLineInSecondFile(),
                                    part.getEndLineInSecondFile(), part.getContextLength()));
                            return part;
                        }).toList());

                        return result;
                    });
        });
    }

    private String[] getTokenNames(List<TokenInfo> tokens) {
        String[] tokenNames = new String[tokens.size()];
        for (int i = 0; i < tokens.size(); i++) {
            tokenNames[i] = (tokens.get(i) != null && tokens.get(i).getType() != -1)
                    ? tokens.get(i).getNormalizedText()
                    : "?";
        }
        return tokenNames;
    }

    private String getSimilarFragmentString(FileContentUtil file, int startLine,
                                            int endLine, int contextLength) {
        StringBuilder result = new StringBuilder();
        String[] fileContent = file.getContent().split("\n");
        startLine = Math.max(startLine - contextLength, 0);
        endLine = Math.min(endLine + contextLength, fileContent.length - 1);

        for (int line = startLine; line <= endLine; line++) {
            result.append(fileContent[line]);
            result.append("\n");
        }

        return result.toString();
    }
}
