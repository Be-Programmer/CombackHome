package com.example.songchiyun.comebackhome;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by UK on 2016-06-06.
 */
public class SubwayInfo extends Activity {
    String getDest[];
    String normalData[],transData[];
    int length;

    String[] getSubinfo;
    String[] getTime;
    String[] getLine;
    int total;

    SharedPreferences sharedData;
    static final String DATA_ID = "ComeBackHome2";

    static final String INFO = "Info";
    static final String LINE = "line";
    static final String TIME = "time";
    static final String COST = "cost";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subway_main);

        sharedData = getSharedPreferences(DATA_ID, MODE_PRIVATE);

        String subInfo = sharedData.getString(INFO, "");
        String time = sharedData.getString(TIME, "");
        String line = sharedData.getString(LINE, "");


        total = sharedData.getInt(COST, 0);

        getSubinfo = subInfo.split(",");
        getTime = time.split(",");
        getLine = line.split(",");

        //getDest=subInfo.split("!");
        //  normalData=getDest[0].split(",");
        //  transData=getDest[1].split(",");

        //length=normalData.length;
        TextView list[] = new TextView [getSubinfo.length];
        ImageView sub[] = new ImageView[getSubinfo.length];


        LinearLayout leftSide = new LinearLayout(this);
        leftSide.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        leftSide.setOrientation(LinearLayout.VERTICAL);
        LinearLayout righSide = new LinearLayout(this);
        righSide.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        righSide.setOrientation(LinearLayout.VERTICAL);
        //righSide.setLayoutParams(new ViewGroup.MarginLayoutParams(1,1));





        LinearLayout SubList;
        SubList = (LinearLayout)findViewById(R.id.abc);

        int[] drawbleId = {R.drawable.line_1,R.drawable.line_2,R.drawable.line_3, R.drawable.line_4, R.drawable.line_5,
                R.drawable.line_6, R.drawable.line_7, R.drawable.line_8, R.drawable.line_9,R.drawable.line_bun, R.drawable.line_ever,
                R.drawable.line_in, R.drawable.line_k_m, R.drawable.line_kc, R.drawable.line_new};


        for(int i=0;i<getLine.length;i++){


            list[i] = new TextView(this);
            sub[i] = new ImageView(this);
            if(getLine.length-1 != i)
                sub[i].setLayoutParams(new LinearLayout.LayoutParams(120, 600));
            else
                sub[i].setLayoutParams(new LinearLayout.LayoutParams(120, 600));

            list[i].setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));




            if(getLine[i].equalsIgnoreCase("1호선"))
                sub[i].setBackgroundResource(R.drawable.line_1);
            else if(getLine[i].equalsIgnoreCase("2호선"))
                sub[i].setBackgroundResource(R.drawable.line_2);
            else if(getLine[i].equalsIgnoreCase("3호선"))
                sub[i].setBackgroundResource(R.drawable.line_3);
            else if(getLine[i].equalsIgnoreCase("4호선"))
                sub[i].setBackgroundResource(R.drawable.line_4);
            else if(getLine[i].equalsIgnoreCase("5호선"))
                sub[i].setBackgroundResource(R.drawable.line_5);
            else if(getLine[i].equalsIgnoreCase("6호선"))
                sub[i].setBackgroundResource(R.drawable.line_6);
            else if(getLine[i].equalsIgnoreCase("7호선"))
                sub[i].setBackgroundResource(R.drawable.line_7);
            else if(getLine[i].equalsIgnoreCase("8호선"))
                sub[i].setBackgroundResource(R.drawable.line_8);
            else if(getLine[i].equalsIgnoreCase("9호선"))
                sub[i].setBackgroundResource(R.drawable.line_9);


            else if(getLine[i].equalsIgnoreCase("분당선"))
                sub[i].setBackgroundResource(R.drawable.line_bun);
            else if(getLine[i].equalsIgnoreCase("에버라인"))
                sub[i].setBackgroundResource(R.drawable.line_ever);
            else if(getLine[i].equalsIgnoreCase("인천1호선"))
                sub[i].setBackgroundResource(R.drawable.line_in);
            else if(getLine[i].equalsIgnoreCase("중앙선"))
                sub[i].setBackgroundResource(R.drawable.line_k_m);
            else if(getLine[i].equalsIgnoreCase("경춘선"))
                sub[i].setBackgroundResource(R.drawable.line_kc);
            else if(getLine[i].equalsIgnoreCase("신분당선"))
                sub[i].setBackgroundResource(R.drawable.line_new);


            leftSide.addView(sub[i]);

            list[i].setHeight(600);
            list[i].setPadding(10, 000, 0, 0);
            list[i].setTextSize(20);
            list[i].setText(getSubinfo[i] +"   " + getTime[i]);

            righSide.addView(list[i]);

        }
        list[(getSubinfo.length-1)] = new TextView(this);
        list[(getSubinfo.length-1)].setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        list[(getSubinfo.length-1)].setHeight(600);
        list[(getSubinfo.length-1)].setPadding(0, 000, 0, 0);
        list[(getSubinfo.length-1)].setTextSize(20);

        list[getSubinfo.length-1].setText(getSubinfo[(getSubinfo.length-1)] +"   " + getTime[getSubinfo.length-1]  + "   총 시간 : " +total + "분");
        righSide.addView(list[getSubinfo.length-1]);

        SubList.addView(leftSide);
        SubList.addView(righSide);
    }
}