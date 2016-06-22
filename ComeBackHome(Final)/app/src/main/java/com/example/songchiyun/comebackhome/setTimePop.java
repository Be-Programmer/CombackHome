package com.example.songchiyun.comebackhome;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;
import android.content.Intent;
/**
 * Created by UK on 2016-06-09.
 */
public class setTimePop extends Activity{
    ImageButton regi;
    TimePicker timePicker;
    Switch onOff;
    boolean setting;
    static final String DATA_ID = "ComeBackHome2";
    static final String TIMESETTING = "TIMESET";
    static final String SETIME = "SETTIME";

    static final String SETMINUTE = "SETMINUTE";

    SharedPreferences sharedData;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        sharedData = getSharedPreferences(DATA_ID, MODE_PRIVATE);
        setting = sharedData.getBoolean(TIMESETTING, false);

        //다이얼로그 형태로 띄운 엑티비티의 타이틀바를 없애준다.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams abc = new WindowManager.LayoutParams();
        setContentView(R.layout.timesetter);

        //시간을 입력받기위한 timePicker를 선언해준다.
        timePicker = (TimePicker)findViewById(R.id.timePicker);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            timePicker.setHour(sharedData.getInt(SETIME, 12));
            timePicker.setMinute(sharedData.getInt(SETMINUTE, 00));
        }
        timePicker.setCurrentHour(sharedData.getInt(SETIME, 12));
        timePicker.setCurrentMinute(sharedData.getInt(SETMINUTE, 00));
        regi = (ImageButton)findViewById(R.id.regist);
        onOff = (Switch)findViewById(R.id.onOff);

        if(setting) {

            onOff.setChecked(true);
            timePicker.setEnabled(true);
        }
        else {

            onOff.setChecked(false);
            timePicker.setEnabled(false);
        }
        Log.d("check","check time set"+setting);
        //timepicker에서 변경될때마다 시간을 받아온다.
        onOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if (isChecked) {
                    setting = true;
                    Log.d("check","check time set1"+setting);
                    timePicker.setEnabled(true);

                } else {
                    setting =  false;
                    Log.d("check","check time set2"+setting);
                    timePicker.setEnabled(false);
                }


            }
        });

    }
    @TargetApi(Build.VERSION_CODES.M)
    //버튼이 눌러질경우 현재의 시간을 번들에 담은뒤
    //엑티비티를 종료해준다.
    public void regist(View view) {
        Intent myFilteredResponse = new Intent("Setting");
        Bundle d = new Bundle();

        Log.d("check", "send data");

        SharedPreferences.Editor editor = sharedData.edit();
        Log.d("check","check time set1"+setting);
        d.putBoolean(TIMESETTING, setting);
        editor.putBoolean(TIMESETTING, setting);
        if(setting) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Toast.makeText(getApplicationContext(), "" + timePicker.getHour(), Toast.LENGTH_SHORT).show();
                d.putInt(SETIME, timePicker.getHour());
                d.putInt(SETMINUTE, timePicker.getMinute());
                editor.putInt(SETIME, timePicker.getHour());
                editor.putInt(SETMINUTE, timePicker.getMinute());
            } else {
                d.putInt(SETIME, timePicker.getCurrentHour());
                d.putInt(SETMINUTE, timePicker.getCurrentMinute());
                editor.putInt(SETIME, timePicker.getCurrentHour());
                editor.putInt(SETMINUTE, timePicker.getCurrentMinute());
                Toast.makeText(getApplicationContext(), "" + timePicker.getCurrentHour() + "  " + timePicker.getCurrentMinute() + "  ", Toast.LENGTH_SHORT).show();
            }
        }
        editor.commit();
        d.putString("data", "timesetting");
        myFilteredResponse.putExtras(d);
        sendBroadcast(myFilteredResponse);
        finish();
    }
}
