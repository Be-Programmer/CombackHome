package com.example.songchiyun.comebackhome;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

public class login extends Activity {
    EditText address;
    double desLat, desLng;
    double desSubLat, desSubLng;
    String desSubName;
    GetSubLocation desSub;
    SharedPreferences Data;
    static final String DATA_ID = "ComeBackHome2";
    static final String ADDRESS_ID = "Destination";
    static final String DESLAT= "desLat";
    static final String DESLNG= "desLng";
    static final String DESSubLat= "desSubLat";
    static final String DESSubLng= "desSubLng";
    static final String SETTING = "Setting";
    static final String DESSubNAME= "desSubName";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams abc = new WindowManager.LayoutParams();
        setContentView(R.layout.activity_login);
        address = (EditText)findViewById(R.id.address);
        Data = getSharedPreferences(DATA_ID, MODE_PRIVATE);
        SharedPreferences.Editor editor = Data.edit();
       // editor.remove(SETTING);
        editor.commit();
    }

    public void map(View v){
        finish();
    }
    @TargetApi(Build.VERSION_CODES.M)
    public void Register(View v) {
        if (isOnline()) {
            if (!address.getText().toString().equals("")) {

                if(setDesLatLng(address.getText().toString())) {
                    desSub =  new GetSubLocation(desLat,desLng);

                    try {

                        Dialog dia = new Dialog(this, R.style.Road);
                        dia.setCancelable(false);
                        dia.addContentView(new ProgressBar(this), new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, android.app.ActionBar.LayoutParams.WRAP_CONTENT));
                        dia.show();

                        desSub.getSubInfo();

                        dia.dismiss();


                        Data = getSharedPreferences(DATA_ID, MODE_PRIVATE);
                        SharedPreferences.Editor editor = Data.edit();
                        editor.putFloat(DESLAT, (float) desLat);
                        editor.putFloat(DESLNG, (float) desLng);
                        editor.putFloat(DESSubLat, (float) desSub.getSubCoor().getLatitude());
                        editor.putFloat(DESSubLng, (float) desSub.getSubCoor().getLongitude());
                        editor.putString(DESSubNAME, desSub.getSubName());
                        editor.putBoolean(SETTING, true);
                        editor.commit();


                        Bundle sign = new Bundle();

                        sign.putString("data", "setting");
                        Intent myFilteredResponse = new Intent("Setting");
                        myFilteredResponse.putExtras(sign);

                        sendBroadcast(myFilteredResponse);

                        Log.d("check", "broadcast send");

                    }catch (Exception e){

                    }

                }
                else{
                    Toast.makeText(getApplicationContext(),"input correct destination",Toast.LENGTH_SHORT).show();
                }
            } else {

            }
        }
        else{
            Toast.makeText(getApplicationContext(),"need internet",Toast.LENGTH_SHORT).show();
        }
    }



    public void startService(View v){
        Intent service = new Intent(getBaseContext(), BackgroundService.class);
        startService(service);
    }
    public boolean isOnline() {  //check to be connect Network
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
    public boolean setDesLatLng(String des) {
        try {
            if (des != null || !des.equals("")) {
                List<Address> addressList = null;
                Geocoder geocoder = new Geocoder(this);


                addressList = geocoder.getFromLocationName(des, 1);
                Address address = addressList.get(0);

                desLat = address.getLatitude();
                desLng = address.getLongitude();


            }


        } catch (Exception e) {
            Log.d("check","input des error :"+ e.toString());
            return false;
        }
        return true;
    }

}
