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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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

    public void onDestory()
    {
        mSensorManager.unregisterListener(this);
    }
    public void onDestoryed()
    {
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

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss,", Locale.US);
        Date date = new Date();
        StringBuilder log = new StringBuilder(dateFormat.format(date));
        log.append(hrm);

        if (hrm != null || hrm != "0")
        {
            DataLogger dataLogger = new DataLogger("HR_Sensor_Data.csv",log.toString());
            dataLogger.LogData();
        }

    }



    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
