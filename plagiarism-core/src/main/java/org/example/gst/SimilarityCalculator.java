package org.example.gst;

import java.util.ArrayList;
import java.util.List;


public class SimilarityCalculator {

    public static SimVal calcSimilarity(List<String> s1List, List<String> s2List, ArrayList<MatchVals> tiles, float threshold ){
        float similarity = sim(s1List, s2List, tiles);
        boolean suspPlag = similarity >= threshold;

        return (new SimVal(similarity, suspPlag));
    }

    private static float sim(List<String> s1List,
                             List<String> s2List, ArrayList<MatchVals> tiles) {

        return ((float)(2*coverage(tiles))/(float)(s1List.size()+s2List.size()));
    }

    private static int coverage(ArrayList<MatchVals> tiles) {
        int accu = 0;
        for (MatchVals tile : tiles){
            accu += tile.length;
        }
        return accu;
    }

}