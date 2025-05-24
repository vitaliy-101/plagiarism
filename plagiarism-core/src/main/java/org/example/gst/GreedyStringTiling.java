package org.example.gst;

import java.util.*;

public class GreedyStringTiling {

    public ArrayList<MatchVals> tiles = new ArrayList<MatchVals>();
    public ArrayList<Queue<MatchVals>> matchList = new ArrayList<Queue<MatchVals>>();

    public PlagResult run(String[] s1, String[] s2, int mML, float threshold) {
        if (mML < 1)
            System.err
                    .println("OutOfRangeError: minimum Matching Length mML needs to be greater than 0");
        if (!((0 <= threshold) && (threshold <= 1)))
            System.err
                    .println("OutOfRangeError: treshold t needs to be 0<=t<=1");
        if (s1.length == 0 || s2.length == 0)
            System.err
                    .println("NoValidArgumentError: input must be of type string not None");

        // Compute Tiles
        tiles = RKR_GST(s1, s2, mML, 20);

        // Compute Similarity
        SimVal simResult = SimilarityCalculator.calcSimilarity(
                Arrays.asList(s1), Arrays.asList(s2),
                tiles, threshold);
        float similarity = simResult.similarity;
        if (similarity > 1)
            similarity = 1;

        // Create Plagiarism result and set attributes
        PlagResult result = new PlagResult(0, 0);
        result.setIdentifier(createKRHashValue(ArrayToString(s1)), createKRHashValue(ArrayToString(s2)));
        result.setTiles(tiles);
        result.setSimilarity(similarity);
        result.setSuspectedPlagiarism(simResult.suspPlag);

        tiles = new ArrayList<MatchVals>();
        matchList = new ArrayList<Queue<MatchVals>>();

        return result;
    }

    public ArrayList<MatchVals> RKR_GST(String[] PList, String[] TList,
                                               int minimalMatchingLength, int initsearchSize) {
        if (minimalMatchingLength < 1)
            minimalMatchingLength = 3;

        if (initsearchSize < 5)
            initsearchSize = 20;

        int s = 0;

        s = initsearchSize;
        boolean stop = false;

        while (!stop) {
            // Lmax is size of largest maximal-matches from this scan
            int Lmax = scanpattern(s, PList, TList);
            // if very long string no tiles marked. Iterate with larger s
            if (Lmax > 2 * s)
                s = Lmax;
            else {
                markStrings(s, PList, TList);
                if (s > (2 * minimalMatchingLength))
                    s = s/2;
                else if (s > minimalMatchingLength)
                    s = minimalMatchingLength;
                else
                    stop = true;
            }
        }
        return tiles;
    }

    public int scanpattern(int s, String[] P, String[] T) {

        int longestMaxMatch = 0;
        Queue<MatchVals> queue = new LinkedList<MatchVals>();
        GSTHashTable hashtable = new GSTHashTable();

        int t = 0;
        boolean noNextTile = false;
        int h;
        while (t < T.length) {
            if (isMarked(T[t])) {
                t = t+1;
                continue;
            }

            int dist;
            if(distToNextTile(t, T) instanceof Integer)
                dist = (int)distToNextTile(t, T);
            else{
                dist = 0;
                dist = T.length - t;
                noNextTile = true;
            }
            //int dist = distToNextTile(t, T);
            // No next tile found

            if (dist < s) {
                if (noNextTile)
                    t = T.length;
                else {
                    if(jumpToNextUnmarkedTokenAfterTile(t, T) instanceof Integer)
                        t = (int)jumpToNextUnmarkedTokenAfterTile(t, T);
                    else
                        t = T.length;
                }
            } else {
                StringBuilder sb = new StringBuilder();

                for (int i = t; i <= t + s-1; i++)
                    sb.append(T[i]);
                String substring = sb.toString();
                h = createKRHashValue(substring);
                hashtable.add(h, t);
                t = t+1;
            }
        }

        noNextTile = false;
        int p = 0;
        while (p < P.length) {
            if (isMarked(P[p])) {
                p = p + 1;
                continue;
            }

            int dist;

            if(distToNextTile(p, P) instanceof Integer){
                dist = (int)distToNextTile(p, P);
            }
            else{
                dist = 0;
                dist = P.length - p;
                noNextTile = true;
            }

            if (dist < s) {
                if (noNextTile)
                    p = P.length;
                else {

                    if(jumpToNextUnmarkedTokenAfterTile(p, P) instanceof Integer)
                        p = (int)jumpToNextUnmarkedTokenAfterTile(p, P);
                    else{
                        p = 0;
                        p = P.length;
                    }
                }
            } else {
                StringBuilder sb = new StringBuilder();
                for (int i = p; i <= p + s-1; i++) {
                    sb.append(P[i]);
                }
                String substring = sb.toString();
                h = createKRHashValue(substring);
                ArrayList<Integer> values = hashtable.get(h);
                if (values != null) {
                    for (Integer val : values) {
                        StringBuilder newsb = new StringBuilder();
                        for (int i = val; i <= val + s-1; i++) {
                            newsb.append(T[i]);
                        }
                        if (newsb.toString().equals(substring)) {
                            t = val;
                            int k = s;

                            while (p + k < P.length && t + k < T.length
                                    && P[p + k].equals(T[t + k])
                                    && isUnmarked(P[p + k])
                                    && isUnmarked(T[t + k]))
                                k = k + 1;

                            if (k > 2 * s)
                                return k;
                            else {
                                if (longestMaxMatch < s)
                                    longestMaxMatch = s;
                                MatchVals mv = new MatchVals(p, t, k);
                                queue.add(mv);
                            }
                        }
                    }
                }
                p += 1;
            }

        }
        if (!queue.isEmpty()){
            matchList.add(queue);
        }
        return longestMaxMatch;
    }

    private void markStrings(int s, String[] P, String[] T) {
        // Создаем копию matchList, чтобы избежать ConcurrentModificationException
        List<Queue<MatchVals>> matchListCopy = new ArrayList<>(matchList);
        for (Queue<MatchVals> queue : matchListCopy) {
            // Создаем копию очереди, чтобы безопасно модифицировать
            Queue<MatchVals> queueCopy = new LinkedList<>(queue);
            while (!queueCopy.isEmpty()) {
                MatchVals match = queueCopy.poll();
                if (!isOccluded(match, tiles)) {
                    for (int j = 0; j < match.length; j++) {
                        P[match.patternPostion + j] = markToken(P[match.patternPostion + j]);
                        T[match.textPosition + j] = markToken(T[match.textPosition + j]);
                    }
                    tiles.add(match);
                }
            }
        }
        matchList.clear(); // очищаем оригинальный список после обработки
    }


    private int createKRHashValue(String substring) {
        int hashValue = 0;
        for (int i = 0; i < substring.length(); i++)
            hashValue = ((hashValue << 1) + (int) substring.charAt(i));
        return hashValue;
    }

    private boolean isUnmarked(String string) {
        if (string.length() > 0 && string.charAt(0) != ' ')
            return true;
        else
            return false;
    }

    private boolean isMarked(String string) {
        return (!isUnmarked(string));
    }

    private String markToken(String string) {
        StringBuilder sb = new StringBuilder();
        sb.append(" ");
        sb.append(string);
        return sb.toString();
    }


    private boolean isOccluded(MatchVals match, ArrayList<MatchVals> tiles) {
        if(tiles.equals(null) || tiles == null || tiles.size() == 0)
            return false;
        for (MatchVals matches : tiles) {
            if ((matches.patternPostion + matches.length == match.patternPostion
                    + match.length)
                    && (matches.textPosition + matches.length == match.textPosition
                    + match.length))
                return true;
        }
        return false;
    }

    private Object distToNextTile(int pos, String[] stringList) {
        if (pos == stringList.length)
            return null;
        int dist = 0;
        while (pos+dist+1<stringList.length && isUnmarked(stringList[pos+dist+1]))
            dist += 1;
        if (pos+dist+1 == stringList.length)
            return null;
        return dist+1;
    }

    private Object jumpToNextUnmarkedTokenAfterTile(int pos, String[] stringList) {
        Object dist = distToNextTile(pos, stringList);
        if(dist instanceof Integer)
            pos = pos+ (int)dist;
        else
            return null;
        while (pos+1<stringList.length && (isMarked(stringList[pos+1])))
            pos = pos+1;
        if (pos+1> stringList.length-1)
            return null;
        return pos+1;
    }

    private String ArrayToString(String[] array) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            sb.append(array[i]);
        }
        return sb.toString();
    }
}