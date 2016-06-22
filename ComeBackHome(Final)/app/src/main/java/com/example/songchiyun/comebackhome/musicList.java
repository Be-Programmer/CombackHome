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

        musicSelect = getSharedPreferences(ShKey,0);
        musicEditor = musicSelect.edit();

        ///////////get all audio media from sdcard
        //쿼리문을 사용하여 SD카드내에 오디오 형식의 파일을 전부 긁어온다.
        cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
        musicPath = new String[cursor.getCount()];
        musicTitle= new String[cursor.getCount()];
        count=cursor.getCount();
        cursor.moveToFirst();
        do{
            musicPath[cursor.getPosition()]=cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
            musicTitle[cursor.getPosition()]=cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
            Log.i("InSD_Music"," "+cursor.getPosition()+"  "+musicTitle[cursor.getPosition()].toString());
            Log.i("InSD_Music"," "+cursor.getPosition()+"  "+musicPath[cursor.getPosition()].toString());

        }while(cursor.moveToNext());
        //긁어온 오디오형식의 파일을 리스트뷰형태로 띄워준다.
        setListAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, musicTitle));

    }
    //오디오 파일을 터치할시에 그 오디오 파일의 경로를
    //쉐어드 프리퍼런스에 저장해둔다.
    public void onListItemClick(ListView parent, View v,int position,long id){
        musicEditor.putBoolean("first",true);
        musicEditor.putString("music",musicPath[position].toString());
        musicEditor.commit();
        Toast.makeText(getApplicationContext(),""+musicTitle[position].toString(),Toast.LENGTH_SHORT).show();
        finish();

    }
}
