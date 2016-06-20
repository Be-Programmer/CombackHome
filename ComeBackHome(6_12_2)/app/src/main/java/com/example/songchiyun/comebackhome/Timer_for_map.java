package com.example.songchiyun.comebackhome;


import android.os.Handler;
import android.util.Log;

import java.util.Calendar;
import java.util.TimerTask;

/**
 * Created by UK on 2016-05-30.
 */
public class Timer_for_map extends TimerTask{
    String result="";
    Handler msg;
    Google_Map sendMsg;

    public Timer_for_map(Google_Map sendToMain, String setTime){
        Log.i("timer cheack","ddddd"+setTime);
        msg=sendToMain.handler;
        sendMsg=sendToMain;
        long currentTime;

        Calendar timeChecker = Calendar.getInstance();
        String nowTime = timeChecker.getTime().toString();
        String[] tempTime1,tempTime2;

        //현재 시간을 밀리세컨드화
        tempTime1=nowTime.split(" ");
        tempTime2=tempTime1[3].split(":");
        currentTime=(Integer.valueOf(tempTime2[2]) * 10);
        currentTime+=(Integer.valueOf(tempTime2[1]) * 600 );
        currentTime+=(Integer.valueOf(tempTime2[0]) * 36000);

        Log.i("timer cheack","ddddd"+sendToMain.count);
        Log.i("timer cheack","ddddd"+nowTime);


        //받아온 도착 시간을 밀리세큰드화
        String[] timesp = setTime.split("[,]");
        sendToMain.count=(Integer.valueOf(timesp[2]) * 10);
        sendToMain.count+=(Integer.valueOf(timesp[1]) * 600 );
        sendToMain.count+=(Integer.valueOf(timesp[0]) * 36000);

        sendToMain.count=sendToMain.count-currentTime;

        Log.i("timer cheack","ddddd"+sendToMain.count);
    }
    @Override
    public void run() {

        // 시간 계산해주는 부분
        msg.post(new Runnable() {
            public void run() {
                Google_Map.count-=10;
                if( Google_Map.count <= 0){
                    //타이머가 종료됬을시의 상황을 넣으면 된다.
                    Log.i("timer cheack","ddddd?"+sendMsg.count);
                    msg.sendEmptyMessage(3);

                }
                Log.i("timer cheack","ddddd?"+sendMsg.count);
                long nh =  sendMsg.count*100 / 1000 / 60 /60;
                long mm =  sendMsg.count*100 / 1000 / 60 %60;
                long ss =  sendMsg.count*100 / 1000 % 60;



                result=String.format("%1$02d:%2$02d:%3$02d", nh,mm, ss);
                sendMsg.setResult(result);
                msg.sendEmptyMessage(2);
            }
        });
    }

}
