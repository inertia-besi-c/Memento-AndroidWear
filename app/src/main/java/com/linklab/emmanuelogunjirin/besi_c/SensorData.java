package com.linklab.emmanuelogunjirin.besi_c;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SensorData extends WearableActivity implements SensorEventListener {

    private TextView hrdisp;
    private SensorManager mSensorManager;
    private Sensor mHeartRate;

    private Button button;
    private int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_data);

        button = findViewById(R.id.button);

        final Intent HRService = new Intent(this, HeartRateMonitor.class);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (counter%2==0)
                {
                    button.setText("Stop");
                    startService(HRService);
                    button.setBackgroundColor(0xFF252);
                }
                else
                {
                    button.setText("Start");
                    stopService(HRService);
                    button.setBackgroundColor(0x00C853);
                }
                counter ++;

            }
        });



        // Enables Always-on
        setAmbientEnabled();

                Intent i = new Intent(getBaseContext(), SensorData.class );
                startActivity(i);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mHeartRate,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(android.hardware.Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.d("Test", "Got the heart rate (beats per minute) : " +
                String.valueOf(event.values[0]));
        hrdisp.setText(String.valueOf(event.values[0]));
    }
}

