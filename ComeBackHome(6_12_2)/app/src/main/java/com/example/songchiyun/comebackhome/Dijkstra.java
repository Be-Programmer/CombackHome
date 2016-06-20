package com.example.songchiyun.comebackhome;


import android.util.Log;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by LG on 2016-06-01.
 */
public class Dijkstra {

    String from;
    String to;
    SubwayData fromData;
    SubwayData toData;

    int MaxCost = 120;

    LinkedList<String> totalSubway;
    LinkedList<SubwayData> totalTransfer = new LinkedList<SubwayData>();
    LinkedList<String> Direction = new LinkedList<String>();
    LinkedList<Integer> TotalCost = new LinkedList<Integer>();

    HashMap<String, LinkedList<SubwayData>> dataset = new HashMap<String, LinkedList<SubwayData>>();
    HashMap<String, HashSet<String>> transfer = new HashMap<String, HashSet<String>>();

    LinkedList<String> ld = new LinkedList<String>();

    LinkedList<Integer> tc = new LinkedList<Integer>();
    LinkedList<SubwayData> td = new LinkedList<SubwayData>();
    LinkedList<String> di = new LinkedList<String>();
    LinkedList<String> line = new LinkedList<String>();


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
        totalSubway = new LinkedList<String>();

        Iterator<Map.Entry<String, LinkedList<SubwayData>>> it = dataset.entrySet().iterator();

        while (it.hasNext()) {

            LinkedList<SubwayData> linked = it.next().getValue();
            Iterator<SubwayData> sub = linked.iterator();

            while (sub.hasNext()) {
                SubwayData d = sub.next();
                if (d.getName().compareTo(from) == 0)
                    fromData = d;
                if (d.getName().compareTo(to) == 0)
                    toData = d;
                if (fromData != null && toData != null) {
                    if (fromData.getLine().compareTo(toData.getLine()) == 0) {
                        int i = linked.indexOf(fromData);
                        int j = linked.indexOf(toData);

                        if (i < j) {
                            di.addLast("F");
                            td.addLast(fromData);
                            findRouteFs(fromData, 0, ld);
                            td.removeLast();
                            di.removeLast();
                        } else if (i > j) {
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
                if (transfer.containsKey(fromData.getName())) {

                    HashSet<String> temp = transfer.get(fromData.getName());
                    Iterator<String> search = temp.iterator();


                    while (search.hasNext()) {
                        String has = search.next();

                        LinkedList<SubwayData> t = dataset.get(has);
                        for (int i = 0; i < t.size(); i++) {
                            SubwayData t1;
                            if (t.get(i).getName().compareTo(fromData.getName()) == 0) {
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
                } else {
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

        SubwayData temp1 = totalTransfer.getFirst();
        LinkedList<SubwayData> subwayDatas = dataset.get(temp1.getLine());
        for (int i = 0; i < subwayDatas.size(); i++) {
            if (subwayDatas.get(i).getName().compareTo(toData.getName()) == 0) {
                toData = subwayDatas.get(i);
            }
        }
        temp1 = totalTransfer.getLast();
        subwayDatas = dataset.get(temp1.getLine());
        for (int i = 0; i < subwayDatas.size(); i++) {
            if (subwayDatas.get(i).getName().compareTo(fromData.getName()) == 0) {
                fromData = subwayDatas.get(i);
            }
        }

        totalSubway.addLast(from);
        RouteData routeData = new RouteData(totalSubway, totalTransfer, Direction, fromData, toData, MaxCost, TotalCost);

        return routeData;
    }

    public void findRouteFs(SubwayData name, int cost, LinkedList<String> l) {

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
        if (v.indexOf(name) == v.size() - 1)
            return;
        cost = cost + name.getFrontCost();
        SubwayData front;
        if(name.getName().compareTo("충정로") == 0 && name.getLine().compareTo("2호선") == 0) {
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


        if (cost > MaxCost)
            return;

        if (name.getName().compareTo(toData.getName()) == 0) {
            l.add(name.getName());
            Log.i("goal", "" + l);
            checkDistance(cost, l);

            l.removeLast();

            return;
        }

        SubwayData rear;
        if(name.getName().compareTo("시청") == 0 && name.getLine().compareTo("2호선") == 0) {
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

    public void findRouteF(SubwayData name, int cost, LinkedList<String> l, boolean trans) {
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


    public void checkDistance(int cost, LinkedList<String> l) {
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