package com.example.songchiyun.comebackhome;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;
import android.content.pm.PackageManager;
/**
 * Created by LG on 2016-06-04.
 */public class LoadingPage extends Activity {
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loadingpage);


        if(Build.VERSION.SDK_INT >= 23) {
            checkPermission();

        }
        else {
            intent = new Intent(this, Google_Map.class);
            handler.sendEmptyMessageDelayed(0, 2000);
        }

    }
    Handler handler = new Handler(){
        public void  handleMessage(Message msg){
            startActivity(intent);
            finish();
        }
    };
    private boolean checkPermission() {
        if (
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.READ_CONTACTS)
                        != PackageManager.PERMISSION_GRANTED
                ) {

            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Explain to the user why we need to write the permission.
                Toast.makeText(this, "11111", Toast.LENGTH_SHORT).show();
            }

            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.READ_CONTACTS},
                    1);

            // MY_PERMISSION_REQUEST_STORAGE is an
            // app-defined int constant

        } else {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.READ_CONTACTS},
                    1);
            // 다음 부분은 항상 허용일 경우에 해당이 됩니다.
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED || grantResults[1] == PackageManager.PERMISSION_GRANTED || grantResults[2] == PackageManager.PERMISSION_GRANTED

                        ) {
                    intent = new Intent(this, Google_Map.class);
                    handler.sendEmptyMessageDelayed(0, 2000);

                    // permission was granted, yay! do the
                    // calendar task you need to do.

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                break;
        }
    }


}