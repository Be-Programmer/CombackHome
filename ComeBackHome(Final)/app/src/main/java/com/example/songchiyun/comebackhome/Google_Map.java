package com.example.songchiyun.comebackhome;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.Timer;

public class Google_Map extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private boolean isSetting;   //check user set destination or not
    Receiver receiver;  //broadcast receiver
    IntentFilter mainFilter;
    Location subwayCoor;
    static final String DATA_ID = "ComeBackHome2";
    static final String Near_SubLat = "nearSubLat";
    static final String Near_SubLng = "nearSubLng";
    static final String DESLAT = "desLat";
    static final String DESLNG = "desLng";
    static final String ONLINE = "online";
    static final String MODE = "Mode";
    Location myLocation;
    Location desLocation;
    boolean first;
    boolean nearFromDes; //check user is near from destination
    boolean mode;  //it is used for communicate backgroundservice to notify the mode of application
    SharedPreferences Data;
    BackgroundService mService;
    Messenger sendMsgService;  //to use messenger to communicate with service
    boolean mBound = false;
    boolean flag;
    boolean isOnline;
    Marker user, des;
    Polyline line;
    boolean checkError;
    String errorMsg;
    ////////////////////////////////////////////////////
    Timer_for_map timerTask;  //to use to show time limit
    Timer timer;
    TextView timerText;
    static long count = 0;
    String timer_time;// 타이머시간을 위한변수
    ///////////////////////////////////////////////////
    ImageButton time_button, music_button, subway_button, home_button;

    TranslateAnimation home_open, time_open, music_open, subway_open;
    String subwayRoute;
    Switch onOff;
    //animation buttom
    ////////////////////////////////////


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {

        super.onResume();
        flag = true;
        Log.d("check", "map on");

        //if google map state is resume then register broadcast receiver.
        mainFilter = new IntentFilter("getting");
        receiver = new Receiver();
        registerReceiver(receiver, mainFilter);
        Log.d("check","bound"+mBound);
        getSharedPreferences();
    }

    // service connection to connect with service
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {

            Log.d("service check", "check Service connet");
            sendMsgService = new Messenger(service);
            mBound = true;
            if (mode) {
                Log.d("check", "request mode change");
                mapOn();  //mapon means to notify to service map acitivity on and request message
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.d("service check", "check Service disconnect");
            mBound = false;
            mService = null;
            sendMsgService = null;
        }
    };

    public void mapOn() {   //send message to service that map activity is on
        if (!mBound) {
            Log.d("check","not connet");
            startService(new Intent(getBaseContext(), BackgroundService.class));
            return;
        }
        // Create and send a message to the service, using a supported 'what' value
        Message msg = Message.obtain(null, BackgroundService.ONMAP, 0, 0);
        try {
            sendMsgService.send(msg);

        } catch (RemoteException e) {

            e.printStackTrace();
        }
    }

    public void mapOff() { //send message to service that map activity is off
        if (!mBound) return;
        // Create and send a message to the service, using a supported 'what' value
        Message msg = Message.obtain(null, BackgroundService.BACKGROUND, 0, 0);
        try {

            sendMsgService.send(msg);

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void turnOff() { //send message to service that user turn off application(don't want to get service
        if (!mBound) return;
        offTimerTask();
        // Create and send a message to the service, using a supported 'what' value
        Message msg = Message.obtain(null, BackgroundService.OFF, 0, 0);
        try {

            sendMsgService.send(msg);

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void saveMode() {  //save mode in sharedpreferences
        SharedPreferences.Editor editor = Data.edit();
        editor.putBoolean(MODE, mode);
        editor.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        Bundle data = new Bundle();
        data.putInt("Interval", 5000);

        Intent intent = new Intent(getBaseContext(), BackgroundService.class);
        if (!isSetting) {   //if user didn't set destination then, service is not acting. So, need to start service to get data
            Log.d("start", "bind error");
            startService(intent);
        }
        intent.putExtras(data);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE); //bind to service
        setContentView(R.layout.activity_google__map);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        Log.d("check", "receiver :" + receiver);


        //text view for Timer
        timerText = (TextView) findViewById(R.id.timer);

        //set the Buttons
        home_button = (ImageButton) findViewById(R.id.home_button);
        time_button = (ImageButton) findViewById(R.id.time_button);
        music_button = (ImageButton) findViewById(R.id.music_button);
        subway_button = (ImageButton) findViewById(R.id.subway_button);


        ////////////////////////////////////////////////////////////////////////////////////////
        onOff = (Switch) findViewById(R.id.switch1);
        if (!mode)  //check mode, and set switch button according to mode
            onOff.setChecked(false);
        Log.d("check", "check mode : " + String.valueOf(mode));
        onOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if (isChecked) {
                    mode = true;
                    saveMode();  //if user click switch to turn on, then service is started
                    mapOn();

                } else {
                    mode = false; //if user click switch to turn off, then service sleeps.(doen't work)
                    saveMode();
                    turnOff();
                }


            }
        });
    }

    private void timerSetting(String time){  //timer setting
        Log.d("check timer",time);
        if(!isSetting) {   //if user did'nt set destination, then show nothing
            timer_time = "00:00:00"; //초기값 표시
            timerText.setText(timer_time);
        }
        else if(time.equals("near")){//if user is near from destination, then show nothing
            offTimerTask();
        }
        else {
            try {
                Log.i("time checker",time);
                timerText.setVisibility(View.VISIBLE);
                if(timer == null){
                    timer = new Timer();
                }
                if(timerTask != null) {
                    timerTask.cancel();
                    timerTask = null;
                }  //set timertask
                timerTask = new Timer_for_map(Google_Map.this, time);//예시 3분  "19,40,00"  //여긱 시간을 지하철 막차 시간으로 setting

                timer.schedule(timerTask, 0, 1000);
            }catch(IllegalStateException e){

            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (isSetting) {
            Log.d("check", "map off");
            mapOff();  //if map acitivity is stop also destination is set then notify change mode to mapoff(background mode) from mapon
        } else {
            //if map acitivity is stop also destination isn't set then notify change mode to turnoff(sleep mode) from mapon

            Log.d("check", "off");
            turnOff();
        }

    }

    @Override
    protected void onDestroy() {
        offTimerTask();
        Log.d("check", "check destroy");
        super.onDestroy();
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }


    }

    private void init() {  //initialize all of variables.
        subwayCoor = new Location("NearSubway");
        myLocation = new Location("current");
        desLocation = new Location("Destination");
        getSharedPreferences();
        flag = true;
        first = true;
        line = null;
        des = null;
        timer = null;
        user = null;
        subwayRoute = "";
        checkError = false;
        errorMsg = "";
        timerTask = null;
    }

    public void getSharedPreferences() {   //get data from sharedPreferences. you should know that mode is used to setting

        Data = getSharedPreferences(DATA_ID, MODE_PRIVATE);

     /*   if(isSetting == false)
            doingNotSetting.cancel(true);*/
        isSetting = Data.getBoolean(login.SETTING, false);
        Log.d("check", String.valueOf(isSetting));
        subwayCoor.setLatitude(Data.getFloat(Near_SubLat, -1));
        subwayCoor.setLongitude(Data.getFloat(Near_SubLng, -1));
        desLocation.setLatitude(Data.getFloat(DESLAT, -1));
        desLocation.setLongitude(Data.getFloat(DESLNG, -1));
        mode = Data.getBoolean(MODE, true);

        Log.d("check", "check mode" + mode);
        if (mode) {
            Log.d("check", "request mode change");
            mapOn();
        }
        Log.d("check", "subway : " + String.valueOf(subwayCoor));

    }

    @Override  //initailize map setting
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(false);
        double myLat, myLng;


        if (myLocation == null) {


        } else {
            LatLng latlng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 17));
            user = mMap.addMarker(new MarkerOptions()
                    .position(latlng)
                    .title("MyLocation")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.person)));

            if (isSetting)  //if destination is setting then show destination postion to show android launcher
                des = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(desLocation.getLatitude(), desLocation.getLongitude()))
                        .title("destination")
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher)));
       }
    }

    private void updateMap() {   //if this class get broadcast message which inform user's location, then update map

        if (!checkError) {
            user.remove();
            Log.d("check", "check focus");
            LatLng MyLatlng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MyLatlng, 17));
            if (first) {  //if first, move camera on user's location
                Log.d("check", "first");
                first = false;
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MyLatlng, 17));
            }

            if (isSetting) {
                if (nearFromDes) {  //if destination is set but near from destination, then erase line
                    if (line != null)
                        line.remove();
                    if (user != null)
                        user.remove();
                    Log.d("check", "my location : " + MyLatlng);
                    user = mMap.addMarker(new MarkerOptions()
                            .position(MyLatlng)
                            .title("Mylocation")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.person)));
                    if (des != null)
                        des.remove();
                    des = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(desLocation.getLatitude(), desLocation.getLongitude()))
                            .title("destination")
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher)));

                    Log.d("check", "not far from des");

                } else {//if destination is set and far from destination, then draw line to subwaystation from user
                    if (user != null)
                        user.remove();
                    user = mMap.addMarker(new MarkerOptions()
                            .position(MyLatlng)
                            .title("Mylocation")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.person)));
                    if (des != null)
                        des.remove();
                    des = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(subwayCoor.getLatitude(), subwayCoor.getLongitude()))
                            .title("destination"));

                    if (line != null)
                        line.remove();
                    Log.d("current", "line check");

                    line = mMap.addPolyline(new PolylineOptions()
                            .add(MyLatlng, new LatLng(subwayCoor.getLatitude(), subwayCoor.getLongitude()))
                            .width(5)
                            .color(Color.RED));

                    des = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(desLocation.getLatitude(), desLocation.getLongitude()))
                            .title("destination")
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher)));
                }


            } else {
                //not setting destination, then onl
                user.remove();
                Log.d("check", "not setting");
                user = mMap.addMarker(new MarkerOptions()
                        .position(MyLatlng)
                        .title("Mylocation")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.person)));
            }
        } else {
            checkError = false;
            Log.d("check", "check error");
            user.remove();
            if (line != null)
                line.remove();
            Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
        }

    }
    //broadcast receiver to communicate service
    public class Receiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("check", "receivce broadcast");
            Bundle data = intent.getExtras();
            if (flag) {
                flag = false;
                if (data.getString("data").equals("setting")) {  //message saying that setting
                    getSharedPreferences();
                    Log.d("check", "setting");
                    flag = true;
                } else if (data.getString("data").equals("subway")) {//message saying that receive subway coordinate
                    Log.d("check", "receivce subway");
                    subwayCoor.setLatitude(data.getDouble("sub-lat"));
                    subwayCoor.setLongitude(data.getDouble("sub-lng"));
                    flag = true;
                } else if (data.getString("data").equals("myLocation")) { //message saying that get current location
                    myLocation.setLatitude(data.getDouble("current-lat"));
                    myLocation.setLongitude(data.getDouble("current-lng"));
                    nearFromDes = data.getBoolean("nearFromDes");
                    isOnline = data.getBoolean(ONLINE);
                    Log.d("check","check near"+nearFromDes);
                    updateMap();

                    flag = true;
                } else if (data.getString("data").equals("Route")) { ////message saying that get subway route
                    subwayRoute = data.getString("route");
                    if (subwayRoute.equals(""))
                        Toast.makeText(getApplicationContext(), "wait to compute", Toast.LENGTH_SHORT).show();

                    else {
                        Log.d("check", "check show receive route");
                        flag = false;

                    }
                    Log.d("check", "get route :" + String.valueOf(subwayRoute));
                } else if (data.getString("data").equals("lastTrain")) {  ////message saying that get departure time
                    Log.d("check", "call timer setting");
                    timerSetting(data.getString("timerTime"));
                    flag = true;

                } else if (data.getString("data").equals("Error")) {  ////message saying that error
                    checkError = true;
                    errorMsg = data.getString("error");
                    Log.d("1", "receive error");
                    updateMap();
                    flag = true;
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);

    }

    ////////////////////////////////////////////////////////////////////////////////////////
    //set the Handler for external thread
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 2) {
                if (count == 1750) {//1750 밑으로떨어지면 글씨색변경
                    timerText.setTextColor(Color.argb(206, 255, 51, 51));
                }
                timerText.setText(timer_time);
            } else if (msg.what == 3) {//타이머 종료시 시간을0으로바꾸고 글씨색 초기화
                timerTask.cancel();
                timerText.setTextColor(Color.argb(212, 67, 67, 67));
                timerText.setText("00:00:00");
            }
        }
    };
    private void offTimerTask(){  //turn off the timer task
        if(timerTask != null) {
            timerTask.cancel();
            timerTask = null;
            timer_time = "00:00:00"; //초기값 표시
            timer.cancel();
            timer = null;
            timerText.setVisibility(View.INVISIBLE);
        }
    }
    //타이머 결과값을 받아오기위한 함수
    public void setResult(String temp) {
        timer_time = temp;
    }

    //each listener button

    //팝업 엑티비티띄우는 클릭리스너
    public void SetTime(View view) {
        if (isSetting) {
            startActivity(new Intent(this, setTimePop.class));
        } else {
            Toast.makeText(getApplicationContext(), "set destination first", Toast.LENGTH_SHORT).show();
        }
    }

    public void musicList(View view) {
        startActivity(new Intent(this, musicList.class));
    }

    public void setHome(View view) {
        startActivity(new Intent(this, login.class));
    }


  //show subway route when user set destination
    public void showSubMain(View v) {
        if (!nearFromDes) {
            if (isSetting) {
                Intent intent = new Intent(this, SubwayInfo.class);
                startActivity(intent);
            } else
                Toast.makeText(getApplicationContext(), "set destination first", Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(getApplicationContext(), "NEAR FROM DESTINATION", Toast.LENGTH_SHORT).show();

    }



    ///////////////////////////////////////////////////////////////////////////////////////////

}
