package org.example.service;

import com.example.content.FileContent;
import com.example.content.RepositoryContent;
import org.antlr.v4.runtime.misc.Pair;
import org.example.token.strategy.TokenCollectorManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CoreService {
    private final TokenCollectorManager tokenCollectorManager;

    public CoreService(TokenCollectorManager tokenCollectorManager) {
        this.tokenCollectorManager = tokenCollectorManager;
    }

    public CompareTwoRepositoryDto compareRepositories (RepositoryContent firstRepository, RepositoryContent secondRepository) {

        CompareTwoRepositoryDto compareResult = new CompareTwoRepositoryDto();
        compareResult.setFirstRepository(firstRepository);
        compareResult.setSecondRepository(secondRepository);
        compareResult.setCompareFiles(new ArrayList<>());

        Set<Pair<FileContent, FileContent>> comparedFiles = new HashSet<>();

        for (FileContent file : firstRepository.getFiles()) {
            for (FileContent file2 : secondRepository.getFiles()) {
                Pair<FileContent, FileContent> pair = new Pair(file, file2);
                comparedFiles.add(pair);
            }
        }

        for (Pair<FileContent, FileContent> pair : comparedFiles) {
            CompareTwoFilesDto result = new CompareTwoFilesDto();
            FileContent firstFile = pair.a;
            FileContent secondFile = pair.b;


            result.setFullFilenameFirst(firstFile.getFullFilename());
            result.setFullFilenameSecond(secondFile.getFullFilename());



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

            result.setSimilarity((double) Math.round(res.getSimilarity()));

            result.setSimilarityParts(res.getTiles().stream().map(t -> {
                SimilarityPart part = new SimilarityPart();
                part.setPatternPostion((long) t.patternPostion);
                part.setLength((long) t.length);
                part.setTextPosition((long) t.textPosition);
                return part;
            }).toList());

            compareResult.getCompareFiles().add(result);
        }



        return compareResult;
    }

}
