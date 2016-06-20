package com.example.songchiyun.comebackhome;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by LG on 2016-05-28.
 */
public class DBAdapter{

    SQLiteDatabase mDb;

    //데이터베이스 주소
    final static String SD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
    final static String DB_PATH = "/Android/data/com.ComeBackHome.subway/databases/";
    static String DATABASE_NAME = SD_PATH + DB_PATH + "subway.db";

    //데이터베이스 쿼리
    //StationName, StationLine, StationURL, preStation, nextStation, StationTime, UpTime, DownTime

    final static String TABLE_NAME = "Station"; // 테이블 이름

    final static String STA_NAME = "StationName"; //이름
    final static String STA_LINE = "StationLine"; //라인
    final static String STA_URL = "StationURL"; //
    final static String PRE_STA = "preStation";
    final static String NXT_STA = "nextStation";
    final static String STA_TIME = "StationTime";
    final static String STA_UP = "UpTime";
    final static String STA_DOWN = "DownTime";
    final static String STA_UP_SAT = "UpTimeSat";
    final static String STA_DOWN_SAT = "DownTimeSat";
    final static String STA_UP_SUN = "UpTimeSat";
    final static String STA_DOWN_SUN = "DownTimeSat";
    HashMap <String, LinkedList<SubwayData>> dataset = new HashMap<String, LinkedList<SubwayData>>();
    HashMap<String, HashSet<String>> transfer = new HashMap<String, HashSet<String>>();
    LinkedList<String> line = new LinkedList<String>();

    public DBAdapter() {
    }

    public void open() {
        mDb = SQLiteDatabase.openOrCreateDatabase(DATABASE_NAME, null);
    }

    public void close() {
        mDb.close();
    }

    public Cursor getAllContacts(){
        return mDb.query(TABLE_NAME,new String[] {STA_NAME,STA_LINE,STA_URL,PRE_STA,NXT_STA,STA_TIME,STA_UP,STA_DOWN,STA_UP_SAT,STA_DOWN_SAT,STA_UP_SUN,STA_DOWN_SUN
        }, null, null, null, null, null);
    }


    // 순서 상관없이 그냥 받음. 수정할 필요 있음.
    public Data Operation(Cursor c) {

        int i = 0;

        if (c.moveToFirst())
            do {

                SubwayData d = new SubwayData();
                int rearCost = 3; // 앞 역 시간
                int frontCost = 3   ; // 다음역 가는 시간.

                if(c.getString(3) == null)
                    rearCost = 0;
                if(c.getString(4) == null)
                    frontCost = 0;
                //Log.i("시간" + c.getString(0) ,c.getString(6));



                Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_WEEK);
                if(day == 7)
                    d.add(c.getString(0),c.getString(1),rearCost,frontCost,Timetable(c.getString(8)),Timetable(c.getString(9)));
                else if(day == 1)
                    d.add(c.getString(0),c.getString(1),rearCost,frontCost,Timetable(c.getString(10)),Timetable(c.getString(11)));
                else
                    d.add(c.getString(0),c.getString(1),rearCost,frontCost,Timetable(c.getString(6)),Timetable(c.getString(7)));
                if(dataset.containsKey(c.getString(1))) {
                    dataset.get(c.getString(1)).add(d);
                } else {
                    LinkedList<SubwayData> set= new LinkedList<SubwayData>();
                    line.add(c.getString(1));
                    set.add(d);
                    dataset.put(c.getString(1), set);
                }
                i++;
            } while (c.moveToNext());

        Iterator<Map.Entry<String, LinkedList<SubwayData>>> it1 = dataset.entrySet().iterator();



        //환승 지점 만들기
        HashMap<String, LinkedList<String>> temp = new HashMap<String, LinkedList<String>>();
        while(it1.hasNext()) {
            LinkedList<SubwayData> a1 = it1.next().getValue();
            Iterator<SubwayData> b1 = a1.iterator();

            while (b1.hasNext()) {
                SubwayData t = b1.next();
                if (temp.containsKey(t.getLine())) {
                    temp.get(t.getLine()).add(t.getName());
                } else {
                    LinkedList<String> set = new LinkedList<String>();
                    set.add(t.getLine());
                    set.add(t.getName());
                    temp.put(t.getLine(), set);
                }
            }
        }

        //
        it1 = dataset.entrySet().iterator();
        while(it1.hasNext()) {
            LinkedList<SubwayData> a1 = it1.next().getValue();
            Iterator<SubwayData> b1 = a1.iterator();
            while (b1.hasNext()) {
                Iterator<Map.Entry<String, LinkedList<String>>> it2 = temp.entrySet().iterator();
                SubwayData data = b1.next();
                while (it2.hasNext()) {

                    LinkedList<String> a2 = it2.next().getValue();
                    String value = a2.get(0);

                    if (value.compareTo(data.getLine())==0) {
                        continue;
                    }

                    for (int k = 1; k < a2.size(); k++) {
                        if (a2.get(k).compareTo(data.getName()) == 0) {
                            if(a2.get(k).compareTo("신촌")==0)
                                continue;
                            if (transfer.containsKey(data.getName())) {
                                transfer.get(data.getName()).add(value);
                            } else {
                                HashSet<String> hashSet = new HashSet<String>();
                                hashSet.add(value);
                                transfer.put(data.getName(), hashSet);
                            }

                        }

                    }
                }
            }
        }
        Data data = new Data(dataset,transfer,line);
        return data;
    }

    /*역의 시간표를 만들어 줌 */

    public HashMap<Integer, LinkedList<Integer>> Timetable(String time) {


        int i = 5;
        int j = 4;
        String temp[] = time.split(" ");
        String compare = null;
        HashMap<Integer, LinkedList<Integer>> timetable = new  HashMap<Integer, LinkedList<Integer>>();

        for(String var : temp) {
            if(i ==  25)
                break;
            if(var.compareTo("-") == 0 || var.compareTo("") == 0) {
                i++;
                timetable.put(i,null);
                i++;
                compare = null;
                continue;
            }
            if(compare!=null) {
                if (Integer.parseInt(compare) > Integer.parseInt(var)) {
                    if(i == 24)
                        break;
                    i++;
                    LinkedList<Integer> timelink = new LinkedList<Integer>();
                    timelink.add(Integer.parseInt(var));
                    timetable.put(i, timelink);
                    compare = var;
                }
                else {
                    timetable.get(i).add(Integer.parseInt(var));
                    compare = var;
                }
            }
            else {
                LinkedList<Integer> timelink = new LinkedList<Integer>();
                timelink.add(Integer.parseInt(var));
                timetable.put(i, timelink);

                compare = var;
            }
        }

        //  Log.i("두유", ""+timetable);
        return timetable;
    }

}