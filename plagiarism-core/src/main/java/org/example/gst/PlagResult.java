package org.example.gst;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlagResult {
    public ArrayList<MatchVals> tiles;
    public float similarity;
    public int id1;
    public int id2;
    public int id1StringLength;
    public int id2StringLength;
    public String algName;
    public String normName;
    public boolean suspectedPlagiarism;

    public PlagResult(int id1, int id2){
        this.tiles = new ArrayList<>();
        this.similarity = (float) 0.0;
        this.id1 = id1;
        this.id2 = id2;
        this.id1StringLength = Integer.toString(id1).length();
        this.id2StringLength = Integer.toString(id2).length();
        this.algName = "";
        this.normName = "";
        this.suspectedPlagiarism = false;
    }

    public void setTiles(ArrayList<MatchVals> tiles){
        if(tiles.getClass() != ArrayList.class)
            throw new RuntimeException("NoValidArgumentError: tiles must be of type list");
        else
            this.tiles = tiles;
    }

    public void setSimilarity(float similarity){
        if (! (0 <= similarity) && (similarity <= 1))
            throw new RuntimeException("OutOfRangeError: Similarity value should be out of range 0 to 1.0");
        else
            this.similarity = similarity;
    }

    public void setIdentifier(int i, int j){
        this.id1 = i;
        this.id2 = j;
    }
}