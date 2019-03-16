package com.linklab.emmanuelogunjirin.besi_c;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class PainScreen extends WearableActivity
{
    public Vibrator v;      // The vibrator that provides haptic feedback.

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pain_screen);

        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(300);

        Button pain = findViewById(R.id.Pain);
        Button cancel = findViewById(R.id.Cancel);

        pain.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.i("PAIN","Starting Pain EMA");
                Intent StartPainEMA = new Intent(getBaseContext(), PainEMA.class);      // Links to the EMA File
                startActivity(StartPainEMA);    // Starts the Pain EMA file
                finish();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.i("PAIN","Stopping Pain EMA");
                finish();
            }
        });

        setAmbientEnabled();
    }
}
