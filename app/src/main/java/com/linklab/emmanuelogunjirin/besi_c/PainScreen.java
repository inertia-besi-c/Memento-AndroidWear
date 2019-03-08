package com.linklab.emmanuelogunjirin.besi_c;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class PainScreen extends WearableActivity {

    private Button Pain,Cancel;
    public Vibrator v;      // The vibrator that provides haptic feedback.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pain_screen);

        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(300);

        Pain = findViewById(R.id.Pain);

        Pain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("PAIN","Starting EMA");
                Intent StartEMA = new Intent(getBaseContext(), PainEMA.class);      // Links to the EMA File
                startActivity(StartEMA);    // Starts the EMA file
                finish();
            }
        });

        Cancel = findViewById(R.id.Cancel);

        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("PAIN","Stopping Pain");
                finish();
            }
        });



        // Enables Always-on
        setAmbientEnabled();
    }
}
