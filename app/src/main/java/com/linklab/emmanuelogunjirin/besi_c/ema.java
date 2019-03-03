package com.linklab.emmanuelogunjirin.besi_c;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.widget.TextView;

public class ema extends WearableActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ema);


        // Enables Always-on
        setAmbientEnabled();
    }
}
