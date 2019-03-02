package com.linklab.emmanuelogunjirin.besi_c;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends WearableActivity {

    private Button EMA, SLEEP;      // This is the list of buttons

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EMA = (Button) findViewById(R.id.EMA);
        SLEEP = (Button) findViewById(R.id.SLEEP);

        // Enables Always-on
        setAmbientEnabled();
    }
}
