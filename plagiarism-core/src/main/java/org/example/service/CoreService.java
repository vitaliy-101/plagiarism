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


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
            result.setSimilarity((double) Math.round(res.getSimilarity()));
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

    public CompareTwoRepositoryDto compareRepositories (RepositoryContentUtil firstRepository, RepositoryContentUtil secondRepository) {

        CompareTwoRepositoryDto compareResult = new CompareTwoRepositoryDto();
        compareResult.setIdFirstRepository(firstRepository.getId());
        compareResult.setIdSecondRepository(secondRepository.getId());
        compareResult.setCompareFiles(new ArrayList<>());

        List<Pair<FileContentUtil, FileContentUtil>> comparedFiles = new ArrayList<>();

        for (FileContentUtil file : firstRepository.getFiles()) {
            for (FileContentUtil file2 : secondRepository.getFiles()) {
                Pair<FileContentUtil, FileContentUtil> pair = new Pair(file, file2);
                comparedFiles.add(pair);
            }
        }

        for (Pair<FileContentUtil, FileContentUtil> pair : comparedFiles) {
            CompareTwoFilesDto result = new CompareTwoFilesDto();
            FileContentUtil firstFile = pair.a;
            FileContentUtil secondFile = pair.b;


            result.setIdFirstFile(firstFile.getId());

            result.setIdSecondFile(secondFile.getId());





            List<TokenInfo> Submission1Tokens = tokenCollectorManager.collectTokensFromFile(firstRepository.getLanguage(), firstFile.getContent());
            List<TokenInfo> Submission2Tokens = tokenCollectorManager.collectTokensFromFile(secondRepository.getLanguage(), secondFile.getContent());


            StringBuilder submissionBuild1 = new StringBuilder();
            for (TokenInfo token : Submission1Tokens) {
                submissionBuild1.append((char) token.type);
            }
            String submission1 = submissionBuild1.toString();

            StringBuilder submissionBuild2 = new StringBuilder();
            for (TokenInfo token : Submission2Tokens) {
                submissionBuild2.append((char) token.type);
            }
            String submission2 = submissionBuild2.toString();

            if (submission1.length() > submission2.length()) {
                String temp = submission1;
                submission1 = submission2;
                submission2 = temp;

            }

            PlagResult res = GreedyStringTiling.run(submission1, submission2, 1, 0.8f);

            result.setSimilarity((double) res.getSimilarity());

            String text1 = submission1;
            String text2 = submission2;
            result.setSimilarityParts(res.getTiles().stream().map(t -> {

                SimilarityPart part = new SimilarityPart();
                part.setPositionInFirstFile((long) t.patternPostion);
                part.setLength((long) t.length);
                part.setPositionInSecondFile((long) t.textPosition);
                part.setTextInFirstFile(text1.substring(t.patternPostion, t.patternPostion + t.length));
                part.setTextInSecondFile(text2.substring(t.textPosition, t.textPosition + t.length));
                return part;
            }).toList());

            compareResult.getCompareFiles().add(result);
        }



        return compareResult;
    }

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