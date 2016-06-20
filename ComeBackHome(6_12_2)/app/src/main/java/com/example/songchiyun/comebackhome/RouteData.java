package com.example.songchiyun.comebackhome;

import java.util.LinkedList;

/**
 * Created by LG on 2016-06-06.
 */
public class RouteData {
    int curfewH;
    int curfewM;

    int fromH;
    int fromM;
    int toH;
    int toM;

    LinkedList<Integer> TH;
    LinkedList<Integer> TM;


    LinkedList<String> totalSubway;
    LinkedList<SubwayData> totalTransfer;
    LinkedList<String> Direction;
    LinkedList<Integer> LineCost;
    int Cost;

    SubwayData from;
    SubwayData to;

    public RouteData(LinkedList<String> totalSubway, LinkedList<SubwayData> totalTransfer, LinkedList<String> Direction, SubwayData from, SubwayData to, int Cost, LinkedList<Integer> LineCost) {
        this.totalSubway =totalSubway;
        this.totalTransfer = totalTransfer;
        this.from = from;
        this.to = to;
        this.Direction = Direction;
        this.Cost = Cost;
        this.LineCost = LineCost;

    }
    public void Curfew(int hour, int minute) {
        curfewH = hour;
        curfewM = minute;
    }
    public void setTime(int fromH, int fromM, int toH, int toM) {
        this.fromH = fromH;
        this.fromM = fromM;
        this.toH = toH;
        this.toM = toM;
    }
    public void setTime(LinkedList<Integer> TH, LinkedList<Integer> TM) {
        this.TH = TH;
        this.TM = TM;
    }

}
