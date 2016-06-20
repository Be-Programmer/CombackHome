package com.example.songchiyun.comebackhome;

import android.app.Activity;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import java.io.IOException;

/**
 * Created by UK on 2016-05-15.
 */
public class AlarmPage extends Activity implements View.OnClickListener {

    Button button;
    MediaPlayer music;

    //쉐어드프리퍼런스부분
    final String ShKey = "musicSelect";
    SharedPreferences musicSelect;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm);

        /*
        ImageView img = (ImageView)findViewById(R.id.ImageView_Juggle);
        */

        //슬립화면 꺠워주는부분
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);


        button = (Button)findViewById(R.id.stop);
        button.setOnClickListener(this);
        musicSelect = getSharedPreferences(ShKey,0);

        if(!musicSelect.getBoolean("first",false)) {
            music=MediaPlayer.create(getApplicationContext(), R.raw.come);
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





        music.setLooping(true);//노래 무한루프
        music.start();
        ////////////////////////////

    }
    public void onClick(View view){
        music.pause();
        finish();
    }
}