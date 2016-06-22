package com.example.songchiyun.comebackhome;

import java.util.LinkedList;

/**
 * Created by C on 2016-06-06.
 * 출발역과 도착역의 지하철의 정보, 그리고 목적지까지 가기 위한 모든 지하철 이름과, 환승역 그리고 통금시간 정보입니다.
 *
 */

public class RouteData {
    //통금시간입니다
    int curfewH;
    int curfewM;


    int fromH;
    int fromM;
    int toH;
    int toM;

    // 총 시간입니다.
    LinkedList<Integer> TH;
    LinkedList<Integer> TM;



    LinkedList<String> totalSubway; //출발역에서 목적지역까지 총 지하철 명입니다.
    LinkedList<SubwayData> totalTransfer; //총 환승역입니다.
    LinkedList<String> Direction; //상행 하행을 표시해주는 방향입니다.
    LinkedList<Integer> LineCost; //환승 또는 목적지역까지 걸리는 시간입니다.
    int Cost;

    SubwayData from; //출발역입니다
    SubwayData to; //도착역입니다.

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
