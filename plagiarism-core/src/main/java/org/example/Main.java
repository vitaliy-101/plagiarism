package org.example;



import com.example.content.FileContent;
import org.antlr.v4.runtime.CharStreams;
import org.example.gst.GreedyStringTiling;
import org.example.gst.MatchVals;
import org.example.gst.PlagResult;
import org.example.token.*;
import org.example.token.strategy.TokenCollectorManager;
import com.example.content.Language;

import java.nio.file.Files;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        // Example for Java
//        String path1 = "plagiarism-core/src/main/data/java/p041396743.java";
//        String path2 = "plagiarism-core/src/main/data/java/s041396743.java";
//        List<TokenInfo> Submission1Tokens = JavaTokenCollector.collectTokensFromFile(path1);
//        List<TokenInfo> Submission2Tokens = JavaTokenCollector.collectTokensFromFile(path2);

        // Example for Python
//        String path1 = "plagiarism-core/src/main/data/python/s001090749.py";
//        String path2 = "plagiarism-core/src/main/data/python/s000173384.py";
//        TokenCollectorManager tokenCollectorManager = new TokenCollectorManager();
//        List<TokenInfo> Submission1Tokens = tokenCollectorManager.collectTokensFromFile(Language.PY, path1);
//        List<TokenInfo> Submission2Tokens = tokenCollectorManager.collectTokensFromFile(Language.PY, path2);

        // Example for Cpp
//        String path1 = "plagiarism-core/src/main/data/cpp/s003241101.cpp";
//        String path2 = "plagiarism-core/src/main/data/cpp/s001541052.cpp";
        String path1 = "plagiarism-core/src/main/data/cpp/s004292872.cpp";
        String path2 = "plagiarism-core/src/main/data/cpp/s000165165.cpp";
        TokenCollectorManager tokenCollectorManager = new TokenCollectorManager();
        List<TokenInfo> Submission1Tokens = tokenCollectorManager.collectTokensFromFile(Language.CPP, CharStreams.fromFileName(path1).toString());
        List<TokenInfo> Submission2Tokens = tokenCollectorManager.collectTokensFromFile(Language.CPP, CharStreams.fromFileName(path2).toString());

        // Example for Go
//        String path1 = "plagiarism-core/src/main/data/go/s021334834.go";
//        String path2 = "plagiarism-core/src/main/data/go/s480436221.go";
//        List<TokenInfo> Submission1Tokens = GoTokenCollector.collectTokensFromFile(path1);
//        List<TokenInfo> Submission2Tokens = GoTokenCollector.collectTokensFromFile(path2);

        StringBuilder submissionBuild1 = new StringBuilder();
        for (TokenInfo token : Submission1Tokens) {
            submissionBuild1.append(token.normalizedText);
            submissionBuild1.append(" ");
//            System.out.println(token.normalizedText);
        }
        String submission1 = submissionBuild1.toString();

        StringBuilder submissionBuild2 = new StringBuilder();
        for (TokenInfo token : Submission2Tokens) {
            submissionBuild2.append(token.normalizedText);
            submissionBuild2.append(" ");
        }
        String submission2 = submissionBuild2.toString();

//        if (Submission1Tokens.size() > Submission2Tokens.size()) {
//            String temp = submission1;
//            submission1 = submission2;
//            submission2 = temp;
//
//            temp = path1;
//            path1 = path2;
//            path2 = temp;
//
//            List<TokenInfo> temp1 = Submission1Tokens;
//            Submission1Tokens = Submission2Tokens;
//            Submission2Tokens = temp1;
//        }
        // submission1 - pattern, submission2 - text

        PlagResult res = GreedyStringTiling.run(submission1, submission2, 9, 0.8f);
        System.out.println(res);

        System.out.printf("Pattern - %s;\t Text - %s\n", path1, path2);
        System.out.printf("Number of suspected parts: %d\n", res.tiles.size());
        int cnt = 1;
        for (MatchVals val : res.tiles) {
            System.out.printf("%d)Pattern lines: start = %d, end = %d\t Text lines: start = %d, end = %d\n", cnt,
                    Submission1Tokens.get(val.patternPostion).line, Submission1Tokens.get(val.patternPostion + val.length - 1).line,
                    Submission2Tokens.get(val.textPosition).line, Submission2Tokens.get(val.textPosition + val.length - 1).line);
            cnt++;
        }
//        System.out.println("Testing");
//        PlagResult res_test = GreedyStringTiling.run("c a a b a a d", "b a a d c a a a a b a a", 2, 0.8f);
//        System.out.println(res_test);

        //System.out.println(submission1);
        //System.out.println(submission2);
    }
}