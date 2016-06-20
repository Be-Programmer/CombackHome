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
    private boolean isSetting;
    Receiver receiver;
    IntentFilter mainFilter;
    Location subwayCoor;
    static final String DATA_ID = "ComeBackHome2";
    static final String Near_SubLat = "nearSubLat";
    static final String Near_SubLng = "nearSubLng";
    static final String DESLAT= "desLat";
    static final String DESLNG= "desLng";
    static final String ONLINE = "online";
    static final String MODE = "Mode";
    Location myLocation;
    Location desLocation;
    boolean first;
    boolean nearFromDes;
    boolean mode;
    SharedPreferences Data;
    BackgroundService mService;
    Messenger sendMsgService;
    boolean mBound = false;
    boolean flag;
    boolean isOnline;
    Marker user, des;
    Polyline line;
    boolean checkError;
    String errorMsg;
    ////////////////////////////////////////////////////
    Timer_for_map timerTask;
    Timer timer;
    TextView timerText;
    static long count = 0;
    String  timer_time;// 타이머시간을 위한변수
    ///////////////////////////////////////////////////
    ImageButton gear_button,time_button,music_button,subway_button,home_button;
    ImageButton time_wait,music_wait,subway_wait,home_wait;
    TranslateAnimation home_open,time_open,music_open,subway_open;
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
        Log.d("check","map on");
        getSharedPreferences();
        Bundle data = new Bundle();
        data.putInt("Interval", 5000);

        Intent intent = new Intent(getBaseContext(), BackgroundService.class);
        intent.putExtras(data);
        if (!isSetting) {
            Log.d("start", "bind error");
            startService(intent);
        }
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        receiver = new Receiver();
        registerReceiver(receiver, mainFilter);
    }


    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {

            Log.d("service check","check Service connet");


            sendMsgService = new Messenger(service);
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.d("service check","check Service disconnect");
            mBound = false;
            mService = null;
            sendMsgService = null;
        }
    };

    public void mapOn() {
        if (!mBound) {
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

    public void mapOff() {
        if (!mBound) return;
        // Create and send a message to the service, using a supported 'what' value
        Message msg = Message.obtain(null, BackgroundService.BACKGROUND, 0, 0);
        try {

            sendMsgService.send(msg);

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void turnOff() {
        if (!mBound) return;

        // Create and send a message to the service, using a supported 'what' value
        Message msg = Message.obtain(null, BackgroundService.OFF, 0, 0);
        try {

            sendMsgService.send(msg);

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    private void saveMode(){
        SharedPreferences.Editor editor = Data.edit();
        editor.putBoolean(MODE, mode);
        editor.commit();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();

        Log.d("check","check mode accept "+String.valueOf(mode));
        setContentView(R.layout.activity_google__map);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mainFilter = new IntentFilter("getting");
        receiver = new Receiver();
        registerReceiver(receiver, mainFilter);
        Log.d("check","receiver :"+receiver);

        //////////////////////////////////////////////////////////////////////
        //택스트랑 설정버튼
        timerText = (TextView)findViewById(R.id.timer);

        //////////////////////////////////////
        home_button=(ImageButton)findViewById(R.id.home_button);
        time_button=(ImageButton)findViewById(R.id.time_button);
        music_button=(ImageButton)findViewById(R.id.music_button);
        subway_button=(ImageButton)findViewById(R.id.subway_button);


        ////////////////////////////////////////////////////////////////////////////////////////
        onOff = (Switch)findViewById(R.id.switch1);
        if(!mode)
            onOff.setChecked(false);
        Log.d("check","check mode : "+String.valueOf(mode));
        onOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if (isChecked) {
                    mode = true;
                    saveMode();
                    mapOn();

                } else {
                    mode = false;
                    saveMode();
                    turnOff();
                }


            }
        });
    }
    private void timerSetting(String time){
        Log.d("check timer",time);
        if(!isSetting) {
            timer_time = "00:00:00"; //초기값 표시
            timerText.setText(timer_time);
        }
        else if(time.equals("near")){
            timerTask.cancel();
            timerTask=null;
            timerText.setVisibility(View.INVISIBLE);

        }
        else {
            try {
                Log.i("time checker",time);
                if(timerTask != null){
                    timerTask.cancel();
                    count=0;
                }

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
            mapOff();
        }
        else {
            Log.d("check", "off");
            turnOff();
        }

    }
    @Override
    protected  void onDestroy(){
        Log.d("check","check destroy");
        super.onDestroy();
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }

            unregisterReceiver(receiver);

    }
    public void exit(View v) {

        if (!mBound) return;
        // Create and send a message to the service, using a supported 'what' value
        Message msg = Message.obtain(null, BackgroundService.ONMAP, 0, 0);
        try {
            sendMsgService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void init() {
        subwayCoor = new Location("NearSubway");
        myLocation = new Location("current");
        desLocation = new Location("Destination");
        getSharedPreferences();
        flag = true;
        first = true;
        line = null;
        des = null;
        timer = new Timer();
        user = null;
        subwayRoute ="";
        checkError = false;
        errorMsg = "";
    }

    public void getSharedPreferences() {

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

        Log.d("check","check mode"+mode);
            if(mode)
                mapOn();
            Log.d("check", "subway : " + String.valueOf(subwayCoor));

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(false);
        double myLat, myLng;


        if (myLocation == null) {


        } else {
        /*    myLat = myLocation.getLatitude();
            myLng = myLocation.getLongitude();
*/
            LatLng latlng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 17));
            user = mMap.addMarker(new MarkerOptions()
                    .position(latlng)
                    .title("MyLocation")
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher)));

            if(isSetting)
                des = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(desLocation.getLatitude(), desLocation.getLongitude()))
                        .title("destination")
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher)));

        /*    des = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(subwayCoor.getLatitude(), subwayCoor.getLongitude()))
                    .title("destination"));
            mMap.addPolyline(new PolylineOptions()
                    .add(new LatLng(myLat, myLng), new LatLng(subwayCoor.getLatitude(), subwayCoor.getLongitude()))
                    .width(5)
                    .color(Color.RED));*/
        }
    }

    private void updateMap() {

        if(!checkError) {
            user.remove();
            Log.d("check","check focus");
            LatLng MyLatlng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MyLatlng, 17));
            if (first) {
                Log.d("check", "first");
                first = false;
                Toast.makeText(getApplicationContext(), String.valueOf(myLocation), Toast.LENGTH_SHORT).show();
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MyLatlng, 17));
            }

            if (isSetting) {
                if (nearFromDes) {
                    if (line != null)
                        line.remove();
                    if (user != null)
                        user.remove();
                    Log.d("check","my location : "+MyLatlng );
                    user = mMap.addMarker(new MarkerOptions()
                            .position(MyLatlng)
                            .title("Mylocation")
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher)));
                    if (des != null)
                        des.remove();
                    des = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(desLocation.getLatitude(), desLocation.getLongitude()))
                            .title("destination")
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher)));

                    Log.d("check", "not far from des");

                } else {
                    if (user != null)
                        user.remove();
                    user = mMap.addMarker(new MarkerOptions()
                            .position(MyLatlng)
                            .title("Mylocation")
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher)));
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
//            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), 17));

                user.remove();
                Log.d("check", "not setting");
                user = mMap.addMarker(new MarkerOptions()
                        .position(MyLatlng)
                        .title("Mylocation")
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher)));
            }
        }
        else {
            checkError = false;
            Log.d("check","check error");
            user.remove();
            if(line != null)
                line.remove();
            Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
        }

    }

    public class Receiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("check", "receivce broadcast");
            Bundle data = intent.getExtras();
            if (flag) {
                flag = false;
                if (data.getString("data").equals("setting")) {
                    getSharedPreferences();
                    Log.d("check", "setting");
                    flag = true;
                } else if (data.getString("data").equals("subway")) {
                    Log.d("check", "receivce subway");
                    subwayCoor.setLatitude(data.getDouble("sub-lat"));
                    subwayCoor.setLongitude(data.getDouble("sub-lng"));
                    flag = true;
                } else if (data.getString("data").equals("myLocation")) {
                    myLocation.setLatitude(data.getDouble("current-lat"));
                    myLocation.setLongitude(data.getDouble("current-lng"));
                    nearFromDes = data.getBoolean("nearFromDes");
                    isOnline = data.getBoolean(ONLINE);
                    Log.d("check", String.valueOf(myLocation));
                    updateMap();
                    flag = true;
                } else if (data.getString("data").equals("Route")) {
                    subwayRoute = data.getString("route");
                    if (subwayRoute.equals(""))
                        Toast.makeText(getApplicationContext(), "wait to compute", Toast.LENGTH_SHORT).show();

                    else {
                        Log.d("check", "check show receive route");
                        flag = false;

                    }
                    Log.d("check", "get route :" + String.valueOf(subwayRoute));
                } else if (data.getString("data").equals("lastTrain")) {

                    timerSetting(data.getString("timerTime"));
                    flag = true;

                }else if (data.getString("data").equals("Error")) {
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


    }

    ////////////////////////////////////////////////////////////////////////////////////////
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            if(msg.what == 2) {
                if(count == 1750)  {//1750 밑으로떨어지면 글씨색변경
                    timerText.setTextColor(Color.argb(206,255,51,51));
                }
                timerText.setText(timer_time);
            }
            else if(msg.what == 3){//타이머 종료시 시간을0으로바꾸고 글씨색 초기화
                timerTask.cancel();
                timerText.setTextColor(Color.argb(212,67,67,67));
                timerText.setText("00:00:00");
            }
        }
    };
    //타이머 결과값을 받아오기위한 함수
    public void setResult(String temp){
        timer_time=temp;
    }

    //봉인된 에니메이션 버튼
    public void menuOpen(View view){
        home_open=new TranslateAnimation(0 ,200 ,0 , 0);
        time_open=new TranslateAnimation(0 ,400 ,0 , 0);
        music_open=new TranslateAnimation(0 ,600 ,0 , 0);
        subway_open=new TranslateAnimation(0 ,800 ,0 , 0);

        home_open.setDuration(800);
        time_open.setDuration(800);
        music_open.setDuration(800);
        subway_open.setDuration(800);

        home_wait.startAnimation(home_open);
        time_wait.startAnimation(time_open);
        music_wait.startAnimation(music_open);
        subway_wait.startAnimation(subway_open);

    }

    //팝업 엑티비티띄우는 클릭리스너

    public void SetTime(View view){
        if(isSetting) {
            startActivity(new Intent(this, setTimePop.class));
        }else{
            Toast.makeText(getApplicationContext(),"set destination first",Toast.LENGTH_SHORT).show();
            }
    }
    public void musicList(View view){
        startActivity(new Intent(this,musicList.class));
    }
    public void setHome(View view){
        startActivity(new Intent(this,login.class));
    }

    public void showSubwayPath(View v){
        if (!mBound) {
            Log.d("check", "don't catch");
            return;
        }
        // Create and send a message to the service, using a supported 'what' value
        Message msg = Message.obtain(null, BackgroundService.REQUEST_ROUT, 0, 0);
        try {
            sendMsgService.send(msg);

        } catch (RemoteException e) {

            e.printStackTrace();
        }
    }
    public void showSubMain(View v){
        if(isSetting) {
            Intent intent = new Intent(this, SubwayInfo.class);
            startActivity(intent);
        }
        else
            Toast.makeText(getApplicationContext(),"set destination first", Toast.LENGTH_SHORT).show();
    }


    ///////////////////////////////////////////////////////////////////////////////////////////

}

