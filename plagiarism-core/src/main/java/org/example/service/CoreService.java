package org.example.service;

import com.example.content.*;
import org.antlr.v4.runtime.misc.Pair;
import org.example.gst.GreedyStringTiling;
import org.example.gst.PlagResult;
import org.example.token.TokenInfo;
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

}
