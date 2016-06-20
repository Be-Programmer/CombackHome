package com.example.songchiyun.comebackhome;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

/**
 * Created by songchiyun on 16. 6. 3..
 */

public class BootingReceiver extends BroadcastReceiver{

    static final String DATA_ID = "ComeBackHome2";
    static final String SETTING = "Setting";
    SharedPreferences Data;
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            if(isSetting(context)) {
                Bundle data = new Bundle();
                data.putInt("Interval", 10000);
                Intent i = new Intent(context, BackgroundService.class);
                context.startService(i);
            }
        }
    }
    private boolean isSetting(Context c) {
        Data = c.getSharedPreferences(DATA_ID, Context.MODE_PRIVATE);
        return Data.getBoolean(SETTING, false);
    }
}