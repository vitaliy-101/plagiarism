package org.example.gst;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;

@Getter
@Setter
@AllArgsConstructor
public class GSTHashTable {

    HashMap<Long, ArrayList<Integer>> dict;

    public GSTHashTable(){
        dict = new HashMap<>();
    }

    public void add(long h, int obj){
        ArrayList<Integer> newlist;
        if(dict.containsKey(h)){
            newlist = dict.get(h);
            newlist.add(obj);
            dict.put(h, newlist);
        }
        else{
            newlist = new ArrayList<>();
            newlist.add(obj);
            dict.put(h, newlist);
        }
    }

    public ArrayList<Integer> get(long key){
        return dict.getOrDefault(key, null);
    }

}