package com.example.songchiyun.comebackhome;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

/**
 * Created by UK on 2016-05-28.
 */
public class alarm {
    String time="";
    alarm(BackgroundService con ,String now){

        //기존에 알람설정이 되있다면 제거
        AlarmManager alarmManager = (AlarmManager)con.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(con, AlarmPage.class);
        PendingIntent pIntent = PendingIntent.getActivity(con, 0, intent, 0);
        alarmManager.cancel(pIntent);

        //받아온시간을 분리
        this.time=now;
        String SplitTime[];
        SplitTime = time.split(",");


        //시간을 밀리세컨드로 변환
        Calendar setTimer = Calendar.getInstance();
        setTimer.set(Calendar.HOUR_OF_DAY,Integer.valueOf(SplitTime[0]) );
        setTimer.set(Calendar.MINUTE, Integer.valueOf(SplitTime[1]) );
        setTimer.set(Calendar.SECOND, Integer.valueOf(SplitTime[2]) );


        //만약 설정하려는 시간이 현제시간보다 과거라면 알람설정 킵
        Calendar timeChecker = Calendar.getInstance();
        if(setTimer.getTimeInMillis() > timeChecker.getTimeInMillis() ){
            alarmManager.set(AlarmManager.RTC_WAKEUP,setTimer.getTimeInMillis(), pIntent);
        }



    }


}
