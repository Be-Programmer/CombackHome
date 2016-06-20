package com.example.songchiyun.comebackhome;

import android.app.ListActivity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Created by UK on 2016-05-28.
 */
public class musicList extends ListActivity {


    int count;
    Cursor cursor;
    String musicPath[];
    String musicTitle[];

    final String ShKey = "musicSelect";
    SharedPreferences musicSelect;
    SharedPreferences.Editor musicEditor;



    @Override
    protected void onCreate(Bundle savedinstanceState){
        super.onCreate(savedinstanceState);

        //if want dont use label ay this popup
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
       // WindowManager.LayoutParams abc = new WindowManager.LayoutParams();
        musicSelect = getSharedPreferences(ShKey,0);
        musicEditor = musicSelect.edit();

        ///////////get all audio media from sdcard

        cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
        musicPath = new String[cursor.getCount()];
        musicTitle= new String[cursor.getCount()];
        count=cursor.getCount();
        cursor.moveToFirst();
        do{
            musicPath[cursor.getPosition()]=cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
            musicTitle[cursor.getPosition()]=cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
            Log.i("dd"," "+cursor.getPosition()+"  "+musicTitle[cursor.getPosition()].toString());
            Log.i("dd"," "+cursor.getPosition()+"  "+musicPath[cursor.getPosition()].toString());

        }while(cursor.moveToNext());
        setListAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, musicTitle));

    }
    public void onListItemClick(ListView parent, View v,int position,long id){

        Log.i("dd!",musicPath[position].toString());
        musicEditor.putBoolean("first",true);
        musicEditor.putString("music",musicPath[position].toString());
        musicEditor.commit();
        Toast.makeText(getApplicationContext(),""+musicTitle[position].toString(),Toast.LENGTH_SHORT).show();
        finish();

    }
}
