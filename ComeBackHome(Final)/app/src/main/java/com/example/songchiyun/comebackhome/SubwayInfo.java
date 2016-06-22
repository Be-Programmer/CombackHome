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
 * 출발역과 환승역 그리고 도착역을 화면으로 보여주는 class 입니다.
 * 여기서 보여주는 것은 지하철역, 호선, 그리고 그 역에 도착하는 시간과 예상시간입니다.
 */
public class SubwayInfo extends Activity {
    String getDest[];
    String normalData[],transData[];
    int length;

    String[] getSubinfo; //지하철 이름
    String[] getTime; //그 역에 도착하는 시간
    String[] getLine; //호선
    int total; // 총 걸리는 시간

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


        //지하철 역, 시간, 호선을 받아옵니다.
        String subInfo = sharedData.getString(INFO, "");
        String time = sharedData.getString(TIME, "");
        String line = sharedData.getString(LINE, "");


        total = sharedData.getInt(COST, 0);

        getSubinfo = subInfo.split(",");
        getTime = time.split(",");
        getLine = line.split(",");

        TextView list[] = new TextView [getSubinfo.length*2 -1];
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

        //지하철호선 그림입니다.
        int[] drawableId = {R.drawable.line_1,R.drawable.line_2,R.drawable.line_3, R.drawable.line_4, R.drawable.line_5,
                R.drawable.line_6, R.drawable.line_7, R.drawable.line_8, R.drawable.line_9,R.drawable.line_bun, R.drawable.line_ever,
                R.drawable.line_in, R.drawable.line_k_m, R.drawable.line_kc,R.drawable.line_kc, R.drawable.line_new};


        //지하철호선 이름입니다.
        String[] lineName = {"1호선","2호선","3호선","4호선","5호선","6호선","7호선","8호선","9호선",
                "분당선","에버라인","인천1호선","중앙선","경의선","경춘선","신분당선"};


        //지금까지 구한 지하철 역과 호선 시간을 표시해줍니다
        for(int i=0;i<getLine.length;i++){

            list[i*2] = new TextView(this);
            list[(i*2)+1] = new TextView(this);

            sub[i] = new ImageView(this);
            if(getLine.length-1 != i)
                sub[i].setLayoutParams(new LinearLayout.LayoutParams(120, 600));
            else
                sub[i].setLayoutParams(new LinearLayout.LayoutParams(120, 600));

            list[i].setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            //알맞은 호선을 그려줍니다.
            for(int j = 0;  j< drawableId.length; j++) {
                if(getLine[i].equalsIgnoreCase(lineName[j])) {
                    sub[i].setBackgroundResource(drawableId[j]);
                    break;
                }
            }



            //역 이름과 시간, 그리고 호선을 보여줍니다.
            leftSide.addView(sub[i]);

            list[i*2].setHeight(300);
            list[i*2].setPadding(10, 000, 0, 0);
            list[i*2].setTextSize(20);
            list[i*2].setText(getSubinfo[i] +"   " + getTime[i]);
            list[(i*2)+1].setHeight(300);
            list[(i*2)+1].setPadding(10, 000, 0, 0);
            list[(i*2)+1].setTextSize(16);
            list[(i*2)+1].setText(getLine[i]);

            righSide.addView(list[i*2]);
            righSide.addView(list[(i*2)+1]);

        }

        //총 걸리는 시간을 구해줍니다.

        String[] startT = getTime[0].split(":");
        String[] finsihT = getTime[getSubinfo.length-1].split(":");

        int minuteT = Integer.parseInt(finsihT[1]) - Integer.parseInt(startT[1]);
        int hourT = Integer.parseInt(finsihT[0]) - Integer.parseInt(startT[0]);

        while(minuteT <0) {
            hourT--;
            minuteT += 60;
        }

        //마지막 지하철 역과 시간, 그리고 총 걸린 시간을 보여줍니다.
        int length = (getSubinfo.length-1) * 2;
        list[length] = new TextView(this);
        list[length].setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        list[length].setHeight(300);
        list[length].setPadding(0, 000, 0, 0);
        list[length].setTextSize(20);

        list[length].setText(getSubinfo[(getSubinfo.length-1)] +"   " + getTime[getSubinfo.length-1]  + "   총 시간 : " +hourT+ "시간 " +minuteT+ "분" );
        righSide.addView(list[length]);

        SubList.addView(leftSide);
        SubList.addView(righSide);


    }
}