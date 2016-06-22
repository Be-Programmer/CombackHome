package com.example.songchiyun.comebackhome;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by C on 2016-06-01.
 * 그 역의 지하철 정보 class 입니다.
 */
public class SubwayData {

    String name; // 지하철
    String line; // 지하철 호선
    int rearCost; // 상행 걸린시간
    int frontCost; // 하행 걸린시간

    HashMap<Integer, LinkedList<Integer>> fromTime; // 상행 시간표
    HashMap<Integer, LinkedList<Integer>> toTime; // 하행 시간표


    public void add(String name, String line, int rearCost, int frontCost, HashMap<Integer, LinkedList<Integer>> fromTime, HashMap<Integer, LinkedList<Integer>> toTime) {
        this.name = name;
        this.line = line;
        this.rearCost = rearCost;
        this.frontCost = frontCost;
        this.fromTime =fromTime;
        this.toTime = toTime;
    }


    public String getName() {
        return  name;
    }
    public String getLine() {
        return  line;
    }
    public int getFrontCost() {
        return frontCost;
    }
    public int getRearCost() {
        return rearCost;
    }


    public void addName(String name) {
        this.name = name;
    }
    public void addLine(String line) {
        this.line = line;
    }
    public void addFront(int front) {
        this.frontCost = front;
    }
    public void addRear(int rear) {
        this.rearCost = rear;
    }

}
