package com.example.songchiyun.comebackhome;

import android.Manifest;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by songchiyun on 16. 5. 28..
 */
public class BackgroundService extends Service implements LocationListener {
    Location tempLoc;
    int count = 0;
    //  map이 켜져있을때 -> timer cancel 후 intervel 재설정/ 안켜져있을때 구별해서 서비스 돌려줘야함

    final static String SD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
    final static String DB_PATH = "/Android/data/com.ComeBackHome.subway/databases/";
    LocationManager locationManager, GPSlocManger;
    Dijkstra dijkstra;
    Data data;
    boolean reset;

    boolean timeSetting;

    DBAdapter db;

    boolean first = true;
    RouteData routeData;
    //mode value
    static final int BACKGROUND = 1;
    static final int ONMAP = 2;
    static final int OFF = 3;
    static final int REQUEST_ROUT = 4;  //each mode of number.
    boolean isSetting;
    boolean mode; // on / off

    boolean flag;
    NotificationManager manager;
    boolean firstStart;
    int UPDATE_INTERVAL;
    private Timer timer;
    private boolean nearFromDes;
    Location myLocation;
    Location Criteria;
    Location subway;
    Location destination;
    Location desSubway;
    float desToSub;
    String deSubName;
    String nearSubName;

    private int DISTANCE_CHANGE_FOR_UPDATES = 5;
    private int MIN_TIME_BW_UPDATES = 5 * 1000;


    static final String DATA_ID = "ComeBackHome2";
    static final String ADDRESS_ID = "Destination";
    static final String DESLAT = "desLat";
    static final String DESLNG = "desLng";
    static final String DESSubLat = "desSubLat";
    static final String DESSubLng = "desSubLng";
    static final String DESSubNAME = "desSubName";
    static final String Near_SubLat = "nearSubLat";
    static final String Near_SubLng = "nearSubLng";
    static final String SETTING = "Setting";
    static final String ONLINE = "online";
    static final String TIMESETTING = "TIMESET";
    static final String SETIME = "SETTIME";
    static final String SETMINUTE = "SETMINUTE";
    static final String INFO = "Info";
    static final String LINE = "line";
    static final String TIME = "time";
    static final String COST = "cost";
    static final String MODE = "MODE";
    final int BACKGROUNDINTERVAL = 300000;  //if background mode, then interval is 5minutes
    final int ONGROUNDINTERVAL = 3000; // if onmap mode, interval = 3

    SharedPreferences sharedData;
    int remainTime;
    Time nowTime;
    int startH;
    int startM;
    int combackHour;
    int combackMinute;
    alarm setAlarm;
    Receiver receiver;
    boolean needWork;
    //목적지 설정
    String subwayRoute;
    Bundle subwayData;
    boolean test = false;

    @Override
    public void onCreate() {
        Log.d("check", "Service Start");

        super.onCreate();
        if (Build.VERSION.SDK_INT >= 23) {
            doCopy();
        } else
            doCopy();


        DBopen();

        //server implenments , and setting receiver
        Toast.makeText(getApplicationContext(), "START SERVICE", Toast.LENGTH_SHORT).show();
        IntentFilter mainFilter = new IntentFilter("Setting");
        receiver = new Receiver();
        registerReceiver(receiver, mainFilter);
        init();
    }

    private void DBopen() {

        db = new DBAdapter();
        db.open();
        Cursor c = db.getAllContacts();
        data = db.Operation(c);
        db.close();
        test = true;

    }

    private void init() {

        timeSetting = false;
        subwayData = new Bundle();
        //  data = new Data();
        routeData = null;
        reset = false;
        nearSubName = "";
        desSubway = new Location("desSubway");
        destination = new Location("destination");

        myLocation = new Location("myLocation");
        myLocation = getLocation(); //get user's location
        //   Log.d("get",myLocation.toString());
        getSharedPreferences();
        Log.d("check des", String.valueOf(destination));
        Criteria = null;
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        flag = true;
        nowTime = new Time();
        needWork = false;
        timer = null;
        UPDATE_INTERVAL = BACKGROUNDINTERVAL; // 300000
        firstStart = true;
        subwayRoute = "";
    }

    public void doCopy() {
        File outDir = null;
        File outfile = null;

        // 외장메모리가 사용가능 상태인지 확인
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            outDir = new File(SD_PATH + DB_PATH);

            outDir.mkdirs(); // 디렉토리 생성

            outfile = new File(outDir, "subway.db");

            InputStream is = null;
            OutputStream os = null;
            int size;

            try {
                // AssetsManager를 이용하여 subway.db파일 읽기
                Log.i("안녕", "안녕");
                is = getAssets().open("subway.db");
                size = is.available();

                outfile.createNewFile(); // 파일 생성
                os = new FileOutputStream(outfile);

                byte[] buffer = new byte[size];

                is.read(buffer);
                os.write(buffer);

                is.close();
                os.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                    os.close();
                    Log.i("안녕", "안녕");
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();

                }
            }
        }
    }

    public void getSharedPreferences() { //setting each variables, if destination is set, or notw=
        float lat, lng;
        float subLat, subLng;

        sharedData = getSharedPreferences(DATA_ID, MODE_PRIVATE);
        isSetting = sharedData.getBoolean(SETTING, false);
        if (isSetting) {
            lat = sharedData.getFloat(DESLAT, -1);
            lng = sharedData.getFloat(DESLNG, -1);

            subLat = sharedData.getFloat(DESSubLat, -1);
            subLng = sharedData.getFloat(DESSubLng, -1);

            deSubName = sharedData.getString(DESSubNAME, "서울역");

            destination.setLatitude(lat);
            destination.setLongitude(lng);

            desSubway.setLatitude(subLat);
            desSubway.setLongitude(subLng);

            desToSub = destination.distanceTo(desSubway);
            int subwayH = (int) (desToSub / 0.3 + 300) / (60 * 60);
            int subwayM = (int) ((desToSub / 0.3 + 300) - (subwayH * 60 * 60)) / 60;

            Log.d("1", desSubway.toString() + "\n" + destination
                    .toString());
            timeSetting = sharedData.getBoolean(TIMESETTING, false);
            if (timeSetting) {
                combackHour = sharedData.getInt(SETIME, -1);
                combackMinute = sharedData.getInt(SETMINUTE, -1);
                if (combackMinute >= subwayM)
                    combackMinute -= subwayM;
                else {
                    combackMinute = 60 - subwayM + startM;
                    combackHour--;
                }
                combackHour -= subwayH;
            }

            if (!nearSubName.equals("")) {  //it means user reset the destination
                Log.d("check reset", "check reset");
                reset = true;
            }
        }

    }

    private void OnBackground() {
        if (mode) {
            if (timer != null)
                timer.cancel();
            timer = null;
            UPDATE_INTERVAL = BACKGROUNDINTERVAL; // 5분마다 돌아간다 300000
            Log.d("check", "mode change1");
            operating();
        }
    }

    private void onMap() {  //onmap mode, timer interval is set 3seconds
        Log.d("check", "mode change2 start");
        if (isSetting) {
            Log.d("check", "mode change2 start");
            sendTime();
        }
        mode = true;
        if (timer != null)
            timer.cancel();
        timer = null;
        UPDATE_INTERVAL = ONGROUNDINTERVAL; // 3초마다
        Log.d("check", "mode change2");
        operating();
    }

    private void alramOff() {
        setAlarm = new alarm(this, "cancel");
    }

    private void turnOff() {
        mode = false;
        alramOff();
        if (timer != null)
            timer.cancel();
        timer = null;
        Log.d("check", "off");
    }

    class IncomingHandler extends Handler {   //it is part of handler to communicate with google map
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case BACKGROUND:  //background service mode
                    OnBackground();
                    break;
                case ONMAP:  //onmap mode
                    Log.d("check reset", "mode check2");
                    onMap();
                    break;
                case OFF:  //turn off mode
                    turnOff();
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    final Messenger mMessenger = new Messenger(new IncomingHandler());

    @Override
    public IBinder onBind(Intent intent) {
        Toast.makeText(getApplicationContext(), "binding", Toast.LENGTH_SHORT).show();
        return mMessenger.getBinder();
    }


    public void displayNotificationMessage(String msg) {   //show to user some state of service

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(getApplicationContext());

        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(),
                android.R.drawable.ic_input_add))
                .setSmallIcon(android.R.drawable.ic_input_delete)
                .setTicker(msg)
                .setContentText(msg);
        manager.notify(1, builder.build());
    }

    private void sendTime() {   //if alram time is set, then send that time to map to show timer tastw
        if (!nearFromDes) {
            Bundle d = new Bundle();
            d.putString("data", "lastTrain");
            d.putString("timerTime", startH + "," + startM + "," + "00");  //19,40,00
            Intent myFilteredResponse = new Intent("getting");
            myFilteredResponse.putExtras(d);
            sendBroadcast(myFilteredResponse);
        } else {  //if alaram is cancel, then to send the message to google map
            Bundle data = new Bundle();
            Intent myFiltered = new Intent("getting");
            Log.d("error", "not fall from destination");
            data.putString("data", "lastTrain");
            data.putString("timerTime", "near");  //19,40,00
            myFiltered.putExtras(data);
            sendBroadcast(myFiltered);
        }
    }

    public void setAlarmTime(float distance) {   //set alaram time

        //사람 평균 걷는 속도 0.5 m/second
        //출발시간 + subway.distance
        // Of(current) / 0.5 초 + 5분  - (현재시간)
        //start to computing

        Log.d("check distancd time", String.valueOf(distance));
        Log.d("check alarm1 time", startH + ":" + startM);

        nowTime.setToNow();
        //consider the distance from user to subway station
        int subwayH = (int) (distance / 0.3 + 300) / (60 * 60);
        int subwayM = (int) ((distance / 0.3 + 300) - (subwayH * 60 * 60)) / 60;
        int subwayS = (int) ((distance / 0.3 + 300) - (subwayH * 60 * 60) - subwayM * 60);
        Log.d("check alarm2 time", subwayH + ":" + subwayM + ":" + subwayS);
        //   if(startH*60 + startM - (subwayH*60 + subwayM)< nowTime)

        if (startM >= subwayM)
            startM -= subwayM;
        else {
            startM = 60 - subwayM + startM;
            startH--;
        }
        startH -= subwayH;

        Log.d("check alarm2 time", startH + ":" + startM + ":" + subwayS);
        setAlarm = new alarm(this, startH + "," + startM + "," + subwayS);
        //!!!!!!!!!!!
        sendTime();
        //if alarm time is setting then inform to google map

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("check", "start onStratCommand");
        if (firstStart) {
            firstStart = false;
            try {
                UPDATE_INTERVAL = intent.getExtras().getInt("Interval");
            } catch (Exception e) {
                UPDATE_INTERVAL = BACKGROUNDINTERVAL;
            }
        }

        Log.d("check", "INTERVAL :" + String.valueOf(UPDATE_INTERVAL));

        operating();

        return START_STICKY;
    }

    private void operating() {  //it is main method to act in service, time interval is defined to mode
        tempLoc = new Location("temp");
        displayNotificationMessage("service start");
        if (timer == null)
            timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {

            public void run() {
                Log.d("check", "operating");
                boolean checkOnline = true;
                if (myLocation != null)
                    tempLoc = myLocation;
                myLocation = getLocation();  //get current location
                Log.d("check", "location :" + myLocation);

                if (reset) {  //if destination is reset then recompute subway route and alarm time
                    Criteria = myLocation;
                    reset = false;
                    Log.d("zz?", "sss");
                    if (distanceFromDes(myLocation)) { //check user is near from destination
                        nearFromDes = false;

                        if (Build.VERSION.SDK_INT >= 11)
                            new GetSub().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        else
                            new GetSub().execute();
                    } else {  //if near then turn off alarm and to notify to google map
                        alramOff();
                        nearFromDes = true;
                        sendTime();

                    }

                }
                if (myLocation == null) {
                    Bundle d = new Bundle();
                    Log.d("check", "send data");
                    d.putString("data", "Error");
                    d.putString("error", "turn on GPS");
                    castToMapError(d);
                } else {
                    //check user don't move then do nothing
                    if (myLocation != null && tempLoc.getLatitude() == myLocation.getLatitude() &&
                            tempLoc.getLongitude() == myLocation.getLongitude()) {
                        Log.d("check", "not change");

                    } else {
                        Log.d("check", "myLocation : " + myLocation);

                        Log.d("check", "network good");
                        if (isSetting) {   //if detination set
                            if (first) { //first time to operate then get subway inform and route
                                Log.d("check", "first");
                                Criteria = myLocation;
                                new GetSub().execute();
                                first = false;
                            }
                            Log.d("check", "isSetting true");
                            if (distanceFromDes(myLocation)) {  //if user is near from destination
                                Log.d("des", destination.getLatitude() + "/" + destination.getLongitude());
                                nearFromDes = false;
                                Log.d("check des", String.valueOf(nearFromDes));
                                //     Log.d("1", "MyLocation check: " + String.valueOf(myLocation.getLatitude()) + "/" + myLocation.getLongitude());
                                //     Log.d("2", "Criteria check: " + String.valueOf(Criteria.getLatitude()) + "/" + Criteria.getLongitude());


                                if (distanceFromCriteria(myLocation)) {  //500미터 움직였는지 안움직였는지 판단
                                    Log.d("error", "error_check : move over 500m");
                                    count++;

                                    if (subway.distanceTo(myLocation) > subway.distanceTo(Criteria)) {
                                        new GetSub().execute();  //user move over 500m and more move from subway
                                        checkOnline = true;


                                    } else {   //지하철로 가까이 움직이는 중
                                        Log.d("check", "move to subway");
                                        setAlarmTime(subway.distanceTo(myLocation));  //concern subway rout and distance between current and subway station

                                    }
                                    Criteria = myLocation;

                                }
                            } else {   //user is near from destination then send the message to google map
                                if (!nearFromDes)
                                    alramOff();
                                nearFromDes = true;
                                Log.d("check des", String.valueOf(nearFromDes));
                                Bundle data = new Bundle();
                                Intent myFiltered = new Intent("getting");
                                Log.d("error", "not fall from destination");
                                data.putString("data", "lastTrain");
                                data.putString("timerTime", "near");  //19,40,00
                                myFiltered.putExtras(data);
                                sendBroadcast(myFiltered);
                            }
                        } else {
                            Log.d("check", "isSetting false");
                            Log.d("check", String.valueOf(myLocation));
                        }


                    }   //to notify user's location continually
                    Bundle d = new Bundle();
                    Log.d("check", "send data");
                    d.putBoolean(ONLINE, isOnline());
                    d.putBoolean("nearFromDes", nearFromDes);
                    d.putString("data", "myLocation");
                    d.putDouble("current-lat", myLocation.getLatitude());
                    d.putDouble("current-lng", myLocation.getLongitude());

                    Intent myFilteredResponse = new Intent("getting");
                    myFilteredResponse.putExtras(d);
                    sendBroadcast(myFilteredResponse);

                }
            }
        }, 0, UPDATE_INTERVAL);
    }

    private boolean distanceFromCriteria(Location current) {  //compare distance from criteria to current location

        return current.distanceTo(Criteria) > 200;
    }

    private boolean distanceFromDes(Location current) {  //compare distance from destination to current
        if (destination == null) {
            Log.d("check", "destination null");
            return false;
        }
        if (myLocation == null) {
            Log.d("check", "mylocation null");
            return false;
        }

        return destination.distanceTo(myLocation) > 1000;  //2km 좀 안좋아
    }

    public Location getLocation() {  //getLocation to use google map service
        Location location = null;
        boolean isNetworkEnabled;
        Context mContext = this;

        try {
            locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            Log.d("check", "network enable : " + isNetworkEnabled);

            if (isNetworkEnabled) {
                Log.d("check", "network enable");
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //request permission
                }
                locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        DISTANCE_CHANGE_FOR_UPDATES, this);
                if (locationManager != null) {
                    Log.d("check error", String.valueOf(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)));

                    return locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
//        Log.d("check","location check: "+location.getLatitude()+"/"+location.getLongitude());
        return location;
    }

    public class Receiver extends BroadcastReceiver {  //broadcast receiver to get some message saying setting

        @Override
        public void onReceive(Context context, Intent intent) {

            Bundle d = intent.getExtras();
            if (d.getString("data").equals("setting")) {
                getSharedPreferences();
                Log.d("check", "setting");
            }
            if (d.getString("data").equals("timesetting")) {
                timeSetting = intent.getExtras().getBoolean(TIMESETTING);
                Log.d("check", "check timesetting" + timeSetting);
                if (timeSetting) {
                    combackHour = intent.getExtras().getInt(SETIME);
                    combackMinute = intent.getExtras().getInt(SETMINUTE);
                    //시간세팅
                }
                reset = true;
                Log.d("check", "check timesetting" + combackHour + "/" + combackMinute);
            }

        }
    }

    private class GetSub extends AsyncTask<Void, Void, Void> {  //get nearest subway information to use asyncTask
        Location sub;
        String subwayName;
        GetSubLocation nearSub;

        protected void onPreExecute() {
            nearSub = new GetSubLocation(myLocation.getLatitude(), myLocation.getLongitude());
            sub = new Location("subway");
            subwayName = "";
        }

        @Override
        protected Void doInBackground(Void... params) {
            boolean stop = false;
            while (!stop) {
                Log.d("sub", "sub1 ");

                nearSub.getSubInfo();
                Log.d("sub", "sub2 :");

                stop = true;
            }


            return null;
        }

        protected void onPostExecute(final Void sub) {
            //get nearest subway station,  set variable and refresh sharedpreferences
            subway = nearSub.getSubCoor();
            subwayName = nearSub.getSubName();
            Log.d("sub", "sub :" + subway.toString());

            if (subwayName.equals(deSubName)) {
                //near home although 2km over
                nearFromDes = true;  // combinate with 통금시간
                alramOff();
                sendTime();
                Log.d("sub", "first if");

            } else {
                nearFromDes = false;
                Log.d("near Subname ", nearSubName + "/" + subwayName);
                if (!nearSubName.equals(deSubName)) {  //if subway is same nearest subway from destination to current then do
                    //nothing, else compute shortest path
                    SharedPreferences.Editor editor = sharedData.edit();
                    editor.putFloat(Near_SubLat, (float) subway.getLatitude());
                    editor.putFloat(Near_SubLng, (float) subway.getLongitude());
                    editor.commit();
                    nearSubName = subwayName;
                    Log.d("1", "check: start 다이제스트라");
                    if (Build.VERSION.SDK_INT >= 11)
                        new findingRoute().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    else
                        new findingRoute().execute();

                    //set alarm
                    castToMap();
                }

            }
        }

    }

    private void castToMapError(Bundle d) {
        Intent myFilteredResponse = new Intent("getting");
        Log.d("check", "cast error");
        myFilteredResponse.putExtras(d);
        sendBroadcast(myFilteredResponse);
    }

    private void castToMap() {   //send message that new subway coordinate
        Bundle d = new Bundle();
        Log.d("check", "send data");
        d.putString("data", "subway");
        d.putDouble("sub-lat", subway.getLatitude());
        d.putDouble("sub-lng", subway.getLongitude());

        Intent myFilteredResponse = new Intent("getting");
        myFilteredResponse.putExtras(d);
        sendBroadcast(myFilteredResponse);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("check", "check destroy");
        flag = false;
        timer.cancel();


        unregisterReceiver(receiver);

    }

    public boolean isOnline() {  //check to be connect Network
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onProviderDisabled(String provider) { // needed by Interface. Not used
    }

    @Override
    public void onProviderEnabled(String provider) { // needed by Interface. Not used
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) { // needed by Interface. Not used
    }

    //find shortest subway path and get departure time
    private class findingRoute extends AsyncTask<Void, Integer, Void> {
        String from = "";
        String to = "";

        @Override
        protected void onPreExecute() {
            Log.d("check test", String.valueOf(timeSetting));
            from = nearSubName;//nearSubName;
            to = deSubName;
            subwayRoute = "working";
            Log.d("check des", to + "/" + from);
            dijkstra = new Dijkstra(from, to, data.getDataset(), data.getLine(), data.getTransfer());
            Log.d("check des", "di");
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.d("check", "find route" + timeSetting);
            routeData = dijkstra.solution();

            Log.d("check", "find route" + timeSetting);
            if (timeSetting)
                routeData.Curfew(combackHour, combackMinute);   //통금시간  (시간 / 분)
            else
                routeData.Curfew(-1, -1);   //통금시간
            CalculateCurfew calculateData = new CalculateCurfew(routeData);


            routeData = calculateData.calculateTime();
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }

        @Override
        protected void onPostExecute(Void result) {

            Iterator<String> show = routeData.totalSubway.iterator();
            String txt = "";

            txt = from;
            String temp = "";
            while (show.hasNext()) {
                String temp1 = show.next();
                //if (temp.compareTo(temp1) == 0)
                txt = txt + "\n" + temp;
                temp = temp1;
            }
            txt = "";

            String line = "";
            String time = "";
            for (int i = 0; i < routeData.totalTransfer.size(); i++) {
                SubwayData subwayData = routeData.totalTransfer.get(i);

                int findsize = routeData.totalTransfer.size() - 1;
                if (i != findsize) {
                    txt = txt + subwayData.getName() + ",";
                    line = line + subwayData.getLine() + ",";
                    if (routeData.TM.get(i) < 10)
                        time = time + routeData.TH.get(i) + ":0" + routeData.TM.get(i) + ",";
                    else
                        time = time + routeData.TH.get(i) + ":" + routeData.TM.get(i) + ",";
                } else {
                    txt = txt + subwayData.getName();
                    line = line + subwayData.getLine();
                    if (routeData.TM.get(i) < 10)
                        time = time + routeData.TH.get(i) + ":0" + routeData.TM.get(i) + ",";
                    else
                        time = time + routeData.TH.get(i) + ":" + routeData.TM.get(i) + ",";
                    int cost;
                    if (i == 0)
                        cost = routeData.LineCost.get(0);
                    else
                        cost = routeData.LineCost.get(i) - routeData.LineCost.get(i - 1) - 3;

                    int m = routeData.TM.get(i) + cost;
                    int h = routeData.TH.get(i);
                    while (m > 60) {
                        m = m - 60;
                        h = h + 1;
                    }
                    if (m >= 10)
                        time = time + h + ":" + m;
                    else
                        time = time + h + ":0" + m;
                }
            }
            data.getDataset();

            txt = txt + "," + to;
            SharedPreferences.Editor editor = sharedData.edit();
            editor.putString(INFO, txt);
            editor.putString(LINE, line);
            editor.putString(TIME, time);
            editor.putInt(COST, routeData.LineCost.getLast());

            editor.commit();

            startH = routeData.TH.getFirst();
            startM = routeData.TM.getFirst();
            Log.d("check alarm time", startH + ":" + startM);
            Log.d("check", line);
            Log.d("check", txt);
            Log.d("check", time);

            Log.d("check repeat", "repeat");
            setAlarmTime(subway.distanceTo(Criteria));
            sendTime();
        }
    }

}