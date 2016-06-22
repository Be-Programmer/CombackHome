package com.example.songchiyun.comebackhome;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * Created by C on 2016-06-05.
 * 모든 지하철의 정보를 저장하는 class 입니다.
 */
public class Data {

    HashMap<String, LinkedList<SubwayData>> dataset; // 모든 지하철 정보
    HashMap<String, HashSet<String>> transfer; // 모든 환승 정보
    LinkedList<String> line; // 모든 지하철 호선 이름

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
