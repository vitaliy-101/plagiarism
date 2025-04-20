package org.example;



import org.example.gst.GreedyStringTiling;
import org.example.gst.MatchVals;
import org.example.gst.PlagResult;
import org.example.token.TokenCollector;
import org.example.token.TokenInfo;

import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        String path1 = "data/s041396743.java";
        String path2 = "data/p041396743.java";
        List<TokenInfo> Submission1Tokens = TokenCollector.collectTokensFromFile(path1);
        List<TokenInfo> Submission2Tokens = TokenCollector.collectTokensFromFile(path2);

        StringBuilder submissionBuild1 = new StringBuilder();
        for (TokenInfo token : Submission1Tokens) {
            submissionBuild1.append((char) token.type);
//            System.out.println(token);
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

            temp = path1;
            path1 = path2;
            path2 = temp;

            List<TokenInfo> temp1 = Submission1Tokens;
            Submission1Tokens = Submission2Tokens;
            Submission2Tokens = temp1;
        }
        // submission1 - pattern, submission2 - text

        PlagResult res = GreedyStringTiling.run(submission1, submission2, 1, 0.8f);
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
//        for (MatchVals val : res.tiles) {
//            System.out.printf("%d)Pattern lines: line = '%s'\t Text lines: line = '%s'\n", cnt,
//                    submission1.substring(val.patternPostion, val.patternPostion + val.length),
//                    submission2.substring(val.textPosition, val.textPosition + val.length));
//            cnt++;
//        }

        //System.out.println(submission1);
        //System.out.println(submission2);
    }
}