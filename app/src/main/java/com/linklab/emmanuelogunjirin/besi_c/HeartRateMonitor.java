package com.linklab.emmanuelogunjirin.besi_c;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;

public class HeartRateMonitor extends Service implements SensorEventListener {
    public HeartRateMonitor() {
    }

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate();
        mSensorManager =
                (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(
                Sensor.TYPE_HEART_RATE);
        mSensorManager.registerListener(this, mAccelerometer,
                SensorManager.SENSOR_DELAY_NORMAL);



    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        mSensorManager =
                (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(
                Sensor.TYPE_HEART_RATE);
        mSensorManager.registerListener(this, mAccelerometer,
                SensorManager.SENSOR_DELAY_NORMAL);

        return START_STICKY;
    }


    public void onResume() {
        mSensorManager.registerListener(this, mAccelerometer,
                SensorManager.SENSOR_DELAY_NORMAL);
    }


    public void onPause() {
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(android.hardware.Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.d("Test", "Got the heart rate (beats per minute) : " +
                String.valueOf(event.values[0]));
        String hrm = String.valueOf(event.values[0]);
        if (hrm != null || hrm != "0")
        {
            DataLogger dataLogger = new DataLogger("HR_Sensor_Data.csv",hrm);
        }

    }



    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
