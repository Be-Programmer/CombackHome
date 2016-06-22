package com.example.songchiyun.comebackhome;

/**
 * Created by LG on 2016-06-06.
 */


import android.util.Log;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

/**
 * Created by C on 2016-06-06.
 * curfew시간을 기준으로 각 환승역마다 도착해야 할 시간, 그리고 가장 가까운 지하철에서 출발해야 할 시간을 구해주는 class입니다.
 */ public class CalculateCurfew {
    RouteData routeData;

    public CalculateCurfew(RouteData routeData) {
        this.routeData = routeData;
    }

    LinkedList<Integer> H = new LinkedList<Integer>();
    LinkedList<Integer> M = new LinkedList<Integer>();

    /*


    고려사항 환승 역의 마지막 시간 격차가 커져서 기다리는 시간이 길어지는 경우 발생

     */
    public RouteData calculateTime() { //도착역을 기준으로 거슬러 올라가서 환승역 시간표를 구하고 출발 시간표를 구합니다.
        SubwayData temp;

        int curfewH;
        int curfewM;

        int min;
        int hour;

        LinkedList<Integer> minute;

        for (int i = routeData.totalTransfer.size() - 1; i >= 0; i--) {
            int cost;
            if (i >= 1)
                cost = routeData.LineCost.get(i) - routeData.LineCost.get(i - 1) - 3; // 30 60 25
            else
                cost = routeData.LineCost.get(i);

            SubwayData subwayData = routeData.totalTransfer.get(i); //신분당선 정자
            String direction = routeData.Direction.get(i); // F

            HashMap<Integer, LinkedList<Integer>> findLastTime = new HashMap<Integer, LinkedList<Integer>>();

            if (direction.compareTo("F") == 0)
                findLastTime = subwayData.toTime; //앞 시간표
            else
                findLastTime = subwayData.fromTime; //

            int lastHour = 0;
            Set<Integer> timeset = findLastTime.keySet();
            for(int value : timeset) {
                if(lastHour < value)
                    lastHour = value;
            }

            if (i == routeData.totalTransfer.size() - 1) {
                if (routeData.curfewH == -1 || routeData.curfewM == -1) {
                    curfewH = lastHour;
                    curfewM = 60;
                }else {
                    curfewM = routeData.curfewM - cost; //커트라인 에서 시간 빼면
                    curfewH = routeData.curfewH; }
            } else {
                curfewH = H.getFirst();
                curfewM = M.getFirst() - cost;
            }
            while (curfewM < 0) {
                curfewM += 60;
                curfewH -= 1;
            }

            hour = lastHour;
            min = -1;

            for (int j = curfewH; j > 5; j--) {
                minute = findLastTime.get(j);

                if (minute == null) { // linkedlist 24시 이상부터는 없는것도 있기에..
                    curfewM = 60;
                    continue;
                }
                for (int k = 0; k < minute.size(); k++) {
                    if (min < minute.get(k) && minute.get(k) < curfewM)
                        min = minute.get(k);
                }
                if (min == -1) {
                    min += 60;
                    continue;
                }
                hour = j;
                break;
            }
            H.addFirst(hour);
            M.addFirst(min);
        }

        considerTime();
        routeData.setTime(H, M);

        return routeData;
    }

    public void considerTime() { // 이번엔 출발역에서 환승역 도착역 의 시간을 구합니다.
        // 만약 기존의 환승 지하철과 도착 지하철 역 시간이 그 이전이 이전에 탈 수 있다면 그 시간으로 변경해줍니다.
        SubwayData temp;

        int firstH;
        int firstM;

        int min;
        int hour;

        LinkedList<Integer> minute;


            /*
            구해진 출발 시간에서 마지막 역 까지의 시간을 재 설정함. by C
            첫 역은 필요 없음.
             */
        for (int i = 1; i <  routeData.totalTransfer.size(); i++) {

            boolean check = false;
            int cost;
            if(i == 1)
                cost = routeData.LineCost.get(0);
            else
                cost = routeData.LineCost.get(i) - routeData.LineCost.get(i - 1) + 3;

            firstH = H.get(i-1);
            firstM = M.get(i-1)+cost;

            while(firstM >= 60) {
                firstM -=  60;
                firstH += 1;
            }

            SubwayData subwayData = routeData.totalTransfer.get(i);
            String direction = routeData.Direction.get(i);

            HashMap<Integer, LinkedList<Integer>> findFirstTime = new HashMap<Integer, LinkedList<Integer>>();

            if (direction.compareTo("F") == 0)
                findFirstTime = subwayData.toTime; //앞 시간표
            else
                findFirstTime = subwayData.fromTime; //

            hour = firstH;
            min = firstM; //이게 말이 디나

            for (int j = firstH; j <= H.get(i); j++) {
                minute = findFirstTime.get(j);

                if (minute == null) { // linkedlist 24시 이상부터는 없는것도 있기에..
                    break;
                }
                for (int k = 0; k <minute.size(); k++) {
                    if (min <= minute.get(k) && min >= firstM) { // 이미 오름차순으로 되어 있기 때문에 한 시간만 찾으면 더 이상 찾을 필요가 없습니다.
                        min = minute.get(k);
                        check = true;
                        break;
                    }
                }
                if(!check) {
                    firstM -= 60;
                    continue;
                }
                hour = j;
                break;
            }

            if(H.get(i) != hour || M.get(i) != min) //새로운 지하철 시간이 있으면 갱신해줍니다
            {
                H.remove(i);
                H.add(i, hour);
                M.remove(i);
                M.add(i, min);
            }
        }
    }
}