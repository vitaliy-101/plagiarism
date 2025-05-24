package org.example.gst;

import java.util.ArrayList;
import java.util.HashMap;


public class GSTHashTable {

    HashMap<Long, ArrayList<Integer>> dict;

    public GSTHashTable(){
        dict = new HashMap<Long,ArrayList<Integer>>();
    }


    public void add(long h, int obj){
        ArrayList<Integer> newlist;
        if(dict.containsKey(h)){
            newlist = dict.get(h);
            newlist.add(obj);
            dict.put(h, newlist);
        }
        else{
            newlist = new ArrayList<Integer>();
            newlist.add(obj);
            dict.put(h, newlist);
        }
    }

    public ArrayList<Integer> get(long key){
        if(dict.containsKey(key))
            return dict.get(key);
        else
            return null;
    }

    public void clear(){
        dict = new HashMap<Long,ArrayList<Integer>>();
    }

}