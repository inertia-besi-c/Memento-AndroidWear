package com.linklab.emmanuelogunjirin.besi_c;

import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;

import java.util.Timer;
import java.util.TimerTask;

public class SensorData extends WearableActivity{
    private int HeartRateCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Intent HRService = new Intent(this, HeartRateMonitor.class);

        Timer timer = new Timer();
        timer.schedule( new TimerTask()
        {
            public void run()
            {
                if (HeartRateCounter%2==0)
                {
                    startService(HRService);
                }
                else
                {
                    stopService(HRService);
                }
                HeartRateCounter ++;
            }
        },
                0, 15000);

        // Enables Always-on
        setAmbientEnabled();
    }


}

