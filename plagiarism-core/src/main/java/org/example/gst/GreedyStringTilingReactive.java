package org.example.gst;


import reactor.core.publisher.Mono;

import java.util.*;

public class GreedyStringTilingReactive {

    public ArrayList<MatchVals> tiles = new ArrayList<MatchVals>();
    public ArrayList<Queue<MatchVals>> matchList = new ArrayList<Queue<MatchVals>>();

    public Mono<PlagResult> runReactive(String[] s1, String[] s2, int mML, float threshold) {
        return Mono.fromCallable(() -> {
            if (mML < 1)
                throw new IllegalArgumentException("minimum Matching Length mML must be > 0");
            if (!(0 <= threshold && threshold <= 1))
                throw new IllegalArgumentException("threshold t must be in [0, 1]");
            if (s1.length == 0 || s2.length == 0)
                throw new IllegalArgumentException("Input arrays must not be empty");

            return true;
        }).flatMap(ignored ->
                gst(s1, s2, mML, 20)
        ).map(tilesResult -> {
            this.tiles = tilesResult;

            SimVal simResult = SimilarityCalculator.calcSimilarity(
                    Arrays.asList(s1), Arrays.asList(s2),
                    tilesResult, threshold
            );
            float similarity = Math.min(simResult.similarity, 1.0f);

            PlagResult result = new PlagResult(0, 0);
            result.setIdentifier(
                    createKRHashValue(ArrayToString(s1)),
                    createKRHashValue(ArrayToString(s2))
            );
            result.setTiles(tilesResult);
            result.setSimilarity(similarity);
            result.setSuspectedPlagiarism(simResult.suspPlag);

            tiles = new ArrayList<>();
            matchList = new ArrayList<>();

            return result;
        });
    }

    public Mono<ArrayList<MatchVals>> gst(String[] PList, String[] TList,
                                          int minimalMatchingLength, int initsearchSize) {
        final int minMatchLen = Math.max(minimalMatchingLength, 3);
        final int initialSize = Math.max(initsearchSize, 20);

        class State {
            int s;
            boolean stop;

            State(int s) {
                this.s = s;
                this.stop = false;
            }
        }

        return Mono.just(new State(initialSize))
                .expand(state -> {
                    if (state.stop) return Mono.empty();

                    return Mono.fromSupplier(() -> {
                        int Lmax = scan(state.s, PList, TList);
                        if (Lmax > 2 * state.s) {
                            state.s = Lmax;
                        } else {
                            markStrings(state.s, PList, TList);
                            if (state.s > (2 * minMatchLen)) {
                                state.s = state.s / 2;
                            } else if (state.s > minMatchLen) {
                                state.s = minMatchLen;
                            } else {
                                state.stop = true;
                            }
                        }
                        return state;
                    });
                })
                .then(Mono.fromSupplier(() -> tiles));
    }

    public int scan(int s, String[] P, String[] T) {
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
                dist = (int) distToNextTile(t, T);
            else{
                dist = 0;
                dist = T.length - t;
                noNextTile = true;
            }

            if (dist < s) {
                if (noNextTile)
                    t = T.length;
                else {
                    if(jumpToNextUnmarkedTokenAfterTile(t, T) instanceof Integer)
                        t = (int) jumpToNextUnmarkedTokenAfterTile(t, T);
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
        List<Queue<MatchVals>> matchListCopy = new ArrayList<>(matchList);
        for (Queue<MatchVals> queue : matchListCopy) {
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
        matchList.clear();
    }


    private int createKRHashValue(String substring) {
        int hashValue = 0;
        for (int i = 0; i < substring.length(); i++)
            hashValue = ((hashValue << 1) + (int) substring.charAt(i));
        return hashValue;
    }

    private boolean isUnmarked(String string) {
        return !string.isEmpty() && string.charAt(0) != ' ';
    }

    private boolean isMarked(String string) {
        return (!isUnmarked(string));
    }

    private String markToken(String string) {
        return " " +
                string;
    }


    private boolean isOccluded(MatchVals match, ArrayList<MatchVals> tiles) {
        if(tiles == null || tiles.isEmpty())
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
        var dist = distToNextTile(pos, stringList);
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
        for (String string : array) {
            sb.append(string);
        }
        return sb.toString();
    }
}