package com.example.songchiyun.comebackhome;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

/**
 * Created by UK on 2016-05-28.
 */public class alarm {
    String time="";
    alarm(BackgroundService con ,String now){


        AlarmManager alarmManager = (AlarmManager)con.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(con, AlarmPage.class);
        PendingIntent pIntent = PendingIntent.getActivity(con, 0, intent, 0);
        alarmManager.cancel(pIntent);

        if(!now.equals("cancel")) {
            //기존에 알람설정이 되있다면 제거해주고 새로 설정해준다.

            //받아온시간을 분리
            this.time = now;
            String SplitTime[];
            SplitTime = time.split(",");


            //받아온 시간을 시간을 밀리세컨드로 변환해준다.
            Calendar setTimer = Calendar.getInstance();
            setTimer.set(Calendar.HOUR_OF_DAY, Integer.valueOf(SplitTime[0]));
            setTimer.set(Calendar.MINUTE, Integer.valueOf(SplitTime[1]));
            setTimer.set(Calendar.SECOND, Integer.valueOf(SplitTime[2]));


            //만약 설정하려는 시간이 현제시간보다 과거라면 알람설정 을 하지않고 넘어가준다.
            Calendar timeChecker = Calendar.getInstance();
            if (setTimer.getTimeInMillis() > timeChecker.getTimeInMillis()) {
                //받아온시간을 알람에 등록해준다.
                alarmManager.set(AlarmManager.RTC_WAKEUP, setTimer.getTimeInMillis(), pIntent);
            }
        }
        else Log.d("check alarm","alarm off");


    }


}
