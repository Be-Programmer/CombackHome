package com.example.songchiyun.comebackhome;


import android.os.Handler;
import android.util.Log;

import java.util.Calendar;
import java.util.TimerTask;

public class Timer_for_map extends TimerTask{
    String result="";
    Handler msg;
    Google_Map sendMsg;

    public Timer_for_map(Google_Map sendToMain, String setTime){
        Log.d("check","result : start timer");
        msg=sendToMain.handler;
        sendMsg=sendToMain;
        long currentTime;

        Calendar timeChecker = Calendar.getInstance();
        String nowTime = timeChecker.getTime().toString();
        String[] tempTime1,tempTime2;

        //현재 시간을 밀리세컨드화 시켜준다.
        tempTime1=nowTime.split(" ");
        tempTime2=tempTime1[3].split(":");
        currentTime=(Integer.valueOf(tempTime2[2]) * 10);
        currentTime+=(Integer.valueOf(tempTime2[1]) * 600 );
        currentTime+=(Integer.valueOf(tempTime2[0]) * 36000);


        //받아온 도착 시간을 밀리세큰드화 시켜준다.
        String[] timesp = setTime.split("[,]");
        sendToMain.count=(Integer.valueOf(timesp[2]) * 10);
        sendToMain.count+=(Integer.valueOf(timesp[1]) * 600 );
        sendToMain.count+=(Integer.valueOf(timesp[0]) * 36000);

        sendToMain.count=sendToMain.count-currentTime;
    }
    @Override
    public void run() {

        //1초마다 한번씩 발동하면서 count시간을 1초씩 빼서 계산해준다.
        msg.post(new Runnable() {
            public void run() {
                Google_Map.count-=10;
                if( Google_Map.count <= 0){
                    //타이머가 종료됬을시 헨들러에게 메세지를 보내준다.
                    msg.sendEmptyMessage(3);

                }
                //밀리세컨을 시간, 분, 초로 변환시켜준다.
                long nh =  sendMsg.count*100 / 1000 / 60 /60;
                long mm =  sendMsg.count*100 / 1000 / 60 %60;
                long ss =  sendMsg.count*100 / 1000 % 60;

                //변환된시간을 스트링화 시킨뒤 헨들러를
                //통하여 메세지를 보내준다.
                result=String.format("%1$02d:%2$02d:%3$02d", nh,mm, ss);
                Log.d("check","result :"+result);
                sendMsg.setResult(result);
                msg.sendEmptyMessage(2);
            }
        });
    }

}