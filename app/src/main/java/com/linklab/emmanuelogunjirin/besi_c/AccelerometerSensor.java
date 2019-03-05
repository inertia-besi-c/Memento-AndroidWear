package com.linklab.emmanuelogunjirin.besi_c;

// Imports
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
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

public class AccelerometerSensor extends Service implements SensorEventListener     // This is the file heading, it listens to the physical Heart Rate Senor
{
    private SensorManager mSensorManager;       // Creates the sensor manager that looks into the sensor
    private Sensor mAccelerometer;      // Picks out the Accelerometer sensor specifically.

    protected void onCreate(Bundle savedInstanceState)      // Runs when the function is created.
    {
        super.onCreate();
        /* Establishes the sensor and ability to collect data */
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    /* Establishes the sensor and the ability to collect data at the start of the data collection */
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        return START_STICKY;
    }


    public void onResume()  // A resume activity switch
    {
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }


    public void onPause()   // A pause activity switch
    {
        mSensorManager.unregisterListener(this);
    }

    @Override

    public void onDestroy()     // A destroy all activity switch (kill switch)
    {
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(android.hardware.Sensor sensor, int accuracy)
    {
        // Please do not remove this, the code needs this to function properly. Thank you :-)
    }

    @Override

    public void onSensorChanged(SensorEvent event)      // This is where the data collected by the sensor is saved into a csv file which can be accessed.
    {
        Log.d("Test", "Accelerometer : " + String.valueOf(event.values[0]));     // This is a log for the Logcat to be seen.
        String AccelerometerMonitor = String.valueOf(event.values[0]);      // This changes the value of the sensor data to a string.

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss,", Locale.US);        // Accesses a date format so be appended to the file
        Date date = new Date();     // Opens a new data variable
        StringBuilder log = new StringBuilder(dateFormat.format(date));     // Creates a string out of the date format
        log.append(AccelerometerMonitor);       // Appends the accelerometer value onto the string

        DataLogger dataLogger = new DataLogger("Accelerometer Data.csv", log.toString());       // Logs the data into a file that can be retrieved.
        dataLogger.LogData();   // Logs the data to the computer.
    }

    @Override

    /* Unknown but necessary function */
    public IBinder onBind(Intent intent)
    {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
