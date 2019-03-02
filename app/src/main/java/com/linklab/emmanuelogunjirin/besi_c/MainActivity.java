package com.linklab.emmanuelogunjirin.besi_c;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends WearableActivity {

    private Button EMA;
    private Button Sleep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EMA = (Button) findViewById(R.id.EMA);
        Sleep = (Button) findViewById(R.id.Sleep);

        // Enables Always-on
        setAmbientEnabled();
    }
}
