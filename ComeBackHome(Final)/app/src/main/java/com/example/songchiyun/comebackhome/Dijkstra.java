package com.example.songchiyun.comebackhome;


import android.graphics.Path;
import android.util.Log;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by C on 2016-06-01.
 * 가장 빠른 지하철 노선을 계산해주는 알고리즘 class 입니다.
 *
 */
public class Dijkstra {

    String from; //출발역
    String to; //도착역
    SubwayData fromData; //출발역 정보
    SubwayData toData; //도착역 정보

    int MaxCost = 120; //일단 2시간으로 max를 잡았습니다.

    LinkedList<String> totalSubway; // 도착역에서 출발역까지의 총 지하철명
    LinkedList<SubwayData> totalTransfer = new LinkedList<SubwayData>(); // 총 환승역
    LinkedList<String> Direction = new LinkedList<String>(); // 상행인지 하행인지 알려주는 방향
    LinkedList<Integer> TotalCost = new LinkedList<Integer>(); // 환승역 또는 도착역까지 걸리는 시간

    HashMap<String, LinkedList<SubwayData>> dataset = new HashMap<String, LinkedList<SubwayData>>(); // 모든 지하철 정보
    HashMap<String, HashSet<String>> transfer = new HashMap<String, HashSet<String>>(); // 모든 환승역 이름과 호선

    LinkedList<String> ld = new LinkedList<String>(); //임시 지하철 이름
    LinkedList<Integer> tc = new LinkedList<Integer>(); // 임시 걸리는 시간
    LinkedList<SubwayData> td = new LinkedList<SubwayData>(); //임시 총 환승역
    LinkedList<String> di = new LinkedList<String>();  // 임시 방향
    LinkedList<String> line = new LinkedList<String>(); //목적지까지 가는 호선


    public Dijkstra() {
        from = null;
        to = null;
        dataset = null;
        line = null;
        transfer = null;
    }

    public Dijkstra(String from, String to, HashMap<String, LinkedList<SubwayData>> dataset, LinkedList<String> line, HashMap<String, HashSet<String>> transfer) {
        this.from = from;
        this.to = to;
        this.dataset = dataset;
        this.line = line;
        this.transfer = transfer;
    }

    public RouteData solution() {
        totalSubway = new LinkedList<String>(); //총 지하철 역입니다.
        Iterator<Map.Entry<String, LinkedList<SubwayData>>> it = dataset.entrySet().iterator(); // 출발역과 목적지의 지하철정보를 구합니다.

        while (it.hasNext()) {

            LinkedList<SubwayData> linked = it.next().getValue(); // 호선마다 지하철 정보
            Iterator<SubwayData> sub = linked.iterator(); // 지하철 정보

            while (sub.hasNext()) {
                SubwayData d = sub.next();
                if (d.getName().compareTo(from) == 0) //출발역명이  지하철 정보에 있는 지하철명과 동일하면
                    fromData = d;
                if (d.getName().compareTo(to) == 0) // 목적지명이 지하철 정보에 있는 지하철명과 동일하면
                    toData = d;
                if (fromData != null && toData != null) { //출발지와 목적지 정보가 모두 받아지면 .
                    if (fromData.getLine().compareTo(toData.getLine()) == 0) { //만약 출발지와 목적지 호선이 동일하면 일반적으로 가장 가깝기 때문에 먼저 계산을 합니다.
                        int i = linked.indexOf(fromData);
                        int j = linked.indexOf(toData);

                        if (i < j) { // 상행이면
                            di.addLast("F");
                            td.addLast(fromData);
                            findRouteFs(fromData, 0, ld);
                            td.removeLast();
                            di.removeLast();
                        } else if (i > j) { // 하행이면
                            di.addLast("R");
                            td.addLast(fromData);
                            findRouteRs(fromData, 0, ld);
                            td.removeLast();
                            di.removeLast();
                        }
                        break;
                    }
                }
            }


            if (fromData != null && toData != null) {
                if (transfer.containsKey(fromData.getName())) { //만약 출발역이 환승 가능한 지점이면 출발역의 호선마다 시작합니다. (시작할 때 출발역에서 환승하는 것을 방지하기 위해서)

                    HashSet<String> temp = transfer.get(fromData.getName());
                    Iterator<String> search = temp.iterator();


                    while (search.hasNext()) {
                        String has = search.next();

                        LinkedList<SubwayData> t = dataset.get(has);
                        for (int i = 0; i < t.size(); i++) {
                            SubwayData t1;
                            if (t.get(i).getName().compareTo(fromData.getName()) == 0) { //상행과 하행 모두 실행시킵니다.
                                t1 = t.get(i);
                                di.addLast("F");
                                td.addLast(t1);
                                findRouteF(t1, 0, ld, true);
                                di.removeLast();
                                di.addLast("R");
                                findRouteR(t1, 0, ld, true);
                                td.removeLast();
                                di.removeLast();
                            }
                        }
                    }
                } else { //환승이 아니면
                    di.addLast("F");
                    td.addLast(fromData);
                    findRouteF(fromData, 0, ld, false);
                    di.removeLast();
                    di.addLast("R");
                    findRouteR(fromData, 0, ld, false);
                    td.removeLast();
                    di.removeLast();
                }
                break;
            }
        }

        SubwayData temp1 = totalTransfer.getFirst(); //출발역
        LinkedList<SubwayData> subwayDatas = dataset.get(temp1.getLine());  //만약 출발역 호선이 기존의 출발역 정보와 다르면 출발역 정보를 수정해줍니다.
        for (int i = 0; i < subwayDatas.size(); i++) {
            if (subwayDatas.get(i).getName().compareTo(toData.getName()) == 0) {
                toData = subwayDatas.get(i);
            }
        }
        temp1 = totalTransfer.getLast();
        subwayDatas = dataset.get(temp1.getLine()); //만약 목적지 호선이 기존의 도착역 정보와 다르면 도착역 정보를 수정해줍니다.
        for (int i = 0; i < subwayDatas.size(); i++) {
            if (subwayDatas.get(i).getName().compareTo(fromData.getName()) == 0) {
                fromData = subwayDatas.get(i);
            }
        }

        //RouteData 에다 총 지하철역, 총 환승역, 방향, 출발역 정보, 도착역 정보, 걸리는 시간과, 총 걸리는 시간을 넣어줍니다.
        totalSubway.addLast(from);
        RouteData routeData = new RouteData(totalSubway, totalTransfer, Direction, fromData, toData, MaxCost, TotalCost);

        return routeData;
    }

    public void findRouteFs(SubwayData name, int cost, LinkedList<String> l) { //만약 호선이 같으면 기본적으로 다른역으로 환승하면서 도착역을 찾는 것보다 빠르기 때문에 환승을 하지 않고 바로 갈 수 있게 합니다.

        for (int i = 0; i < td.size() - 1; i++) { // 이미 지나간 역 일 경우 경우
            if (l.get(i).compareTo(name.getName()) == 0)
                return;
        }

        LinkedList<SubwayData> v = dataset.get(name.getLine());


        if (cost > MaxCost) //총 걸린 시간보다 더 걸리면 return 해줍니다.
            return;


        if (name.getName().compareTo(toData.getName()) == 0) {
            l.addLast(name.getName());
            checkDistance(cost, l);
            l.removeLast();
            return;
        }
        if (v.indexOf(name) == v.size() - 1)
            return;
        cost = cost + name.getFrontCost();
        SubwayData front;
        if(name.getName().compareTo("충정로") == 0 && name.getLine().compareTo("2호선") == 0) { //2호선 중에 충정로 다음역으로 가면 시청이 나오도록 설정합니다.
            front =  v.get(0);
        }
        else
            front = v.get(v.indexOf(name) + 1);
        l.addLast(name.getName());


        findRouteFs(front, cost, l);
        l.removeLast();


    }

    public void findRouteRs(SubwayData name, int cost, LinkedList<String> l) {

        for (int i = 0; i < td.size() - 1; i++) { // 이미 지나간 역 일 경우 경우
            if (l.get(i).compareTo(name.getName()) == 0)
                return;
        }
        LinkedList<SubwayData> v = dataset.get(name.getLine());


        if (cost > MaxCost) //총 걸린 시간보다 더 걸리면 return 해줍니다.
            return;

        if (name.getName().compareTo(toData.getName()) == 0) {
            l.add(name.getName());
            Log.i("goal", "" + l);
            checkDistance(cost, l);

            l.removeLast();

            return;
        }

        SubwayData rear;
        if(name.getName().compareTo("시청") == 0 && name.getLine().compareTo("2호선") == 0) { //다음역이 시청 다음역이라면 충정로가 나오도록 설정합니다.
            rear =  v.get(42);
        }
        else if (v.indexOf(name) == 0) {
            return;
        }
        else
            rear = v.get(v.indexOf(name) - 1);




        cost = cost + name.getRearCost();
        l.addLast(name.getName());
        findRouteRs(rear, cost, l);
        l.removeLast();

    }

    public void findRouteF(SubwayData name, int cost, LinkedList<String> l, boolean trans) { //기본적인 지하철 찾아가는 함수입니다.
        for (int i = 0; i < td.size() - 1; i++) { // 이미 지나친 역 일 경우 경우 돌아갑니다
            if (l.get(i).compareTo(name.getName()) == 0)
                return;
        }
        LinkedList<SubwayData> v = dataset.get(name.getLine());

        if (cost > MaxCost)
            return;

        if (name.getName().compareTo(toData.getName()) == 0) {

            l.addLast(name.getName());
            checkDistance(cost, l);
            l.removeLast();
            return;
        }
        l.addLast(name.getName());

        if (!trans) { //그 역에서 이미 환승을 하지 않았으면.
            if (transfer.containsKey(name.getName())) { //만약 현재 위치에 환승이 가능하면 환승해 줍니다.

                HashSet<String> temp = transfer.get(name.getName());
                Iterator<String> search = temp.iterator();

                while (search.hasNext()) {
                    String has = search.next();
                    if (name.getLine().compareTo(has) == 0)
                        continue;
                    LinkedList<SubwayData> t = dataset.get(has);

                    for (int i = 0; i < t.size(); i++) {
                        SubwayData t1;
                        if (t.get(i).getName().compareTo(name.getName()) == 0) {
                            t1 = t.get(i);
                            di.addLast("F");
                            td.addLast(t1);
                            tc.addLast(cost);
                            findRouteF(t1, cost + 3, l, true);
                            di.removeLast();
                            di.addLast("R");
                            findRouteR(t1, cost + 3, l, true);
                            td.removeLast();
                            tc.removeLast();
                            di.removeLast();

                        }
                    }
                }

            }
        }

        if (v.indexOf(name) == v.size() - 1) {
            l.removeLast();
            return;
        }
        cost = cost + name.getFrontCost();
        SubwayData front;
        if(name.getName().compareTo("충정로") == 0 && name.getLine().compareTo("2호선") == 0) {
            front =  v.get(0);
        }
        else
            front = v.get(v.indexOf(name) + 1);
        findRouteF(front, cost, l, false);
        l.removeLast();

    }

    public void findRouteR(SubwayData name, int cost, LinkedList<String> l, boolean trans) {

        for (int i = 0; i < td.size() - 1; i++) { // 이미 지나간 역 일 경우 경우
            if (l.get(i).compareTo(name.getName()) == 0)
                return;
        }
        LinkedList<SubwayData> v = dataset.get(name.getLine());


        if (cost > MaxCost)
            return;

        if (name.getName().compareTo(toData.getName()) == 0) {
            l.addLast(name.getName());

            checkDistance(cost, l);
            l.removeLast();
            return;
        }
        l.addLast(name.getName());

        if (!trans) {
            if (transfer.containsKey(name.getName())) {

                HashSet<String> temp = transfer.get(name.getName());
                Iterator<String> search = temp.iterator();

                {
                    while (search.hasNext()) {
                        String has = search.next();
                        if (name.getLine().compareTo(has) == 0)
                            continue;
                        LinkedList<SubwayData> t = dataset.get(has);
                        for (int i = 0; i < t.size(); i++) {
                            SubwayData t1;
                            if (t.get(i).getName().compareTo(name.getName()) == 0) {
                                t1 = t.get(i);
                                di.addLast("F");
                                td.addLast(t1);
                                tc.addLast(cost);
                                findRouteF(t1, cost + 2, l, true);
                                di.removeLast();
                                di.addLast("R");
                                findRouteR(t1, cost + 2, l, true);
                                tc.removeLast();
                                td.removeLast();
                                di.removeLast();
                            }
                        }
                    }

                }
            }
        }
        SubwayData rear;
        if(name.getName().compareTo("시청") == 0 && name.getLine().compareTo("2호선") == 0) {
            rear =  v.get(42);
        }
        else if (v.indexOf(name) == 0) {
            l.removeLast();
            return;
        }
        else
            rear = v.get(v.indexOf(name) - 1);
        cost = cost + name.getFrontCost();


        findRouteR(rear, cost, l, false);

        l.removeLast();

    }


    public void checkDistance(int cost, LinkedList<String> l) { //목적지에 도착했으면 총 걸린 시간과 총 환승역, 총 지하철역. 환승할 때 또는 출발할 때 상행인지 하행인지 갱신합니다
        if (MaxCost > cost) {
            MaxCost = cost;
            totalSubway = new LinkedList<String>();
            totalTransfer = new LinkedList<SubwayData>();
            Direction = new LinkedList<String>();
            TotalCost = new LinkedList<Integer>();
            for (int i = 0; i < l.size(); i++) {
                totalSubway.addLast(l.get(i));
            }

            for (int i = 0; i < td.size(); i++) {
                totalTransfer.addLast(td.get(i));
            }
            for (int i = 0; i < di.size(); i++) {
                Direction.addLast(di.get(i));
            }

            for (int i = 0; i < tc.size(); i++) {
                TotalCost.addLast(tc.get(i));
            }
            TotalCost.addLast(MaxCost);


        }
    }


}