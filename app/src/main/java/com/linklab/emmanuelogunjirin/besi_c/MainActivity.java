package com.linklab.emmanuelogunjirin.besi_c;

import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends WearableActivity {

    private Button Ema, Sleep;// This is the list of buttons
    private TextView batteryLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Ema = (Button) findViewById(R.id.EMA);
        Sleep = (Button) findViewById(R.id.SLEEP);

        batteryLevel = (TextView) findViewById(R.id.BATTERY_LEVEL);

        Ema.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), ema.class);
                startActivity(i);
                //Toast.makeText(this, "Button Clicked", Toast.LENGTH_LONG).show();

            }
        });

        // Enables Always-on
        setAmbientEnabled();
    }

}
