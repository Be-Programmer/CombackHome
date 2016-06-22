package com.example.songchiyun.comebackhome;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;

import java.io.IOException;

/**
 * Created by UK on 2016-05-15.
 */
public class AlarmPage extends Activity implements View.OnClickListener {

    MediaPlayer music;

    //쉐어드 프리퍼런스를 통하 현재 원하는
    //알람음악으로 쓰는 노래를 파악해준다.
    final String ShKey = "musicSelect";
    AnimationDrawable mframeAnimation = new AnimationDrawable();
    SharedPreferences musicSelect;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm);
        Log.d("alram call","call");
        ImageButton img = (ImageButton) findViewById(R.id.stop);


        //이미지 버튼의 에니메이션을 넣기위한 부분/
        BitmapDrawable frame1 =(BitmapDrawable)getResources().getDrawable(R.drawable.home_1);
        BitmapDrawable frame2 =(BitmapDrawable)getResources().getDrawable(R.drawable.home_2);

        //슬립화면 꺠워주는부분
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);


        img.setOnClickListener(this);
        musicSelect = getSharedPreferences(ShKey,0);

        //만약 사용자가 알람음악을 설정해주지 않았다면 기본음악을 실행해주고 아닐경우 쉐어드 프리퍼런스를
        //통하여 설정해둔 음악의 경로를 통하여 실행시켜준다.
        if(!musicSelect.getBoolean("first",false)) {
            music=MediaPlayer.create(getApplicationContext(), R.raw.alarm_bell);
        }
        else{
            try {
                music = new MediaPlayer();
                music.setDataSource( (musicSelect.getString("music","error")).toString());
                music.prepare();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        music.setLooping(true);
        music.start();
        ////////////////////////////

        //에니메이션을 시작시켜준다.
        int reasonableDuration = 250;
        mframeAnimation.setOneShot(false); // loop continuously
        mframeAnimation.addFrame(frame1, reasonableDuration);
        mframeAnimation.addFrame(frame2, reasonableDuration);


        img.setBackgroundDrawable(mframeAnimation);
        mframeAnimation.setVisible(true, true);
        mframeAnimation.start();

    }
    //이미지 버튼을 클릭시 엑티비티와 음악 에니메이션 모두 종료시켜준다.
    public void onClick(View view){
        music.pause();
        mframeAnimation.stop();
        mframeAnimation.setVisible(false, false);
        finish();
    }
}