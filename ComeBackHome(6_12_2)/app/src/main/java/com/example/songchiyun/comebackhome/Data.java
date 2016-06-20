package com.example.songchiyun.comebackhome;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * Created by LG on 2016-06-05.
 */
public class Data {

    HashMap<String, LinkedList<SubwayData>> dataset;
    HashMap<String, HashSet<String>> transfer;
    LinkedList<String> line;

    public Data() {
        this.dataset = null;
        this.transfer = null;
        this.line = null;
    }

    public Data(HashMap<String, LinkedList<SubwayData>> dataset,HashMap<String, HashSet<String>> transfer,LinkedList<String> line ) {
        this.dataset = dataset;
        this.transfer= transfer;
        this.line = line;
    }
    public  HashMap<String, LinkedList<SubwayData>> getDataset() {
         return dataset;
    }
    public HashMap<String, HashSet<String>> getTransfer() {
        return transfer;
    }
    public LinkedList<String> getLine() {
        return  line;
    }
}
