package com.example.songchiyun.comebackhome;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.Window;
import android.widget.ProgressBar;

/**
 * Created by UK on 2016-05-30.
 */
public class pop extends Activity {

    ProgressBar prog;

    @Override public void onCreate(Bundle a){
        super.onCreate(a);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.poplay);
        prog=(ProgressBar)findViewById(R.id.prog);
        prog.getIndeterminateDrawable().setColorFilter(0xC3683CC1, PorterDuff.Mode.SRC_ATOP);
        prog.setMax(60);
        prog.setProgress(1);
    }
}
