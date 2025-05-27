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

                            int startLineInFirstFile = tokens1.get(t.patternPostion).getLine();
                            int startColumnInFirstFile = tokens1.get(t.patternPostion).getColumn();
                            int endLineInFirstFile = tokens1.get(t.patternPostion + t.length - 1).getLine();
                            int endColumnInFirstFile = tokens1.get(t.patternPostion + t.length - 1).getColumn() +
                                    tokens1.get(t.patternPostion + t.length - 1).getLength() - 1;

                            int startLineInSecondFile = tokens2.get(t.textPosition).getLine();
                            int startColumnInSecondFile = tokens2.get(t.textPosition).getColumn();
                            int endLineInSecondFile = tokens2.get(t.textPosition + t.length - 1).getLine();
                            int endColumnInSecondFile = tokens2.get(t.textPosition + t.length - 1).getColumn() +
                                    tokens2.get(t.textPosition + t.length - 1).getLength() - 1;

                            int contextLength = 2;

                            String[] resultFirstFile = getSimilarFragmentString(file1, startLineInFirstFile, startColumnInFirstFile,
                                    endLineInFirstFile, endColumnInFirstFile, contextLength);
                            String[] resultSecondFile = getSimilarFragmentString(file2, startLineInSecondFile, startColumnInSecondFile,
                                    endLineInSecondFile, endColumnInSecondFile, contextLength);
                            part.setSimilarFragmentInFirstFile(resultFirstFile[0]);
                            part.setContextBeforeInFirstFile(resultFirstFile[1]);
                            part.setContextAfterInFirstFile(resultFirstFile[2]);
                            part.setSimilarFragmentInSecondFile(resultSecondFile[0]);
                            part.setContextBeforeInSecondFile(resultSecondFile[1]);
                            part.setContextAfterInSecondFile(resultSecondFile[2]);

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

    private String[] getSimilarFragmentString(FileContentUtil file, int startLine, int startColumn,
                                              int endLine, int endColumn, int contextLength) {
        String[] fileContent = file.getContent().split("\n");

        StringBuilder contextBefore = new StringBuilder();
        StringBuilder contextAfter = new StringBuilder();
        int startContextLine = Math.max(startLine - contextLength, 0);
        int endContextLine = Math.min(endLine + contextLength, fileContent.length - 1);

        for (int i = startContextLine; i <= startLine; i++)
        {
            if (i == startLine)
            {
                contextBefore.append(fileContent[i], 0, startColumn);
            }
            else
            {
                contextBefore.append(fileContent[i]);
                contextBefore.append("\n");
            }
        }

        for (int i = endLine; i <= endContextLine; i++)
        {
            if (i == endLine)
            {
                contextAfter.append(fileContent[i], Math.min(endColumn + 1, fileContent[i].length()),
                        fileContent[i].length());
                if (fileContent[i].length() > endColumn + 1)
                    contextAfter.append("\n");
            }
            else
            {
                contextAfter.append(fileContent[i]);
                contextAfter.append("\n");
            }
        }

        StringBuilder plagiarism = new StringBuilder();

        for (int line = 0; line < fileContent.length; line++) {
            if (line >= startLine && line <= endLine) {
                if (startLine == endLine){
                    plagiarism.append(fileContent[line], startColumn, Math.min(endColumn + 1, fileContent[line].length()));
                    if (fileContent[line].length() <= endColumn + 1)
                        plagiarism.append("\n");
                }
                else if (line == startLine) {
                    plagiarism.append(fileContent[line], startColumn, fileContent[line].length());
                    plagiarism.append("\n");
                }
                else if (line == endLine) {
                    plagiarism.append(fileContent[line], 0, Math.min(endColumn + 1, fileContent[line].length()));
                    if (fileContent[line].length() <= endColumn + 1)
                        plagiarism.append("\n");
                }
                else {
                    plagiarism.append(fileContent[line]);
                    plagiarism.append("\n");
                }
            }
        }
        return new String[] {plagiarism.toString(), contextBefore.toString(), contextAfter.toString()};
    }
}
