package com.linklab.emmanuelogunjirin.besi_c;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.widget.TextView;

public class SensorData extends WearableActivity implements SensorEventListener {

    private TextView hrdisp;
    private SensorManager mSensorManager;
    private Sensor mHeartRate;

    public HeartRateMonitor hr = new HeartRateMonitor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_data);

        startService(new Intent(this, HeartRateMonitor.class));

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

