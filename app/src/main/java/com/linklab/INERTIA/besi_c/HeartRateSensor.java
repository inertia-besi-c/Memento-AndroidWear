package com.linklab.INERTIA.besi_c;

// Imports
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class HeartRateSensor extends Service implements SensorEventListener     // This is the file heading, it listens to the physical Heart Rate Senor
{
    public long Duration = new Preferences().HRSampleDuration;        // This is the sampling rate in milliseconds
    private SensorManager mSensorManager;       // Creates the sensor manager that looks into the sensor
    int Time_zero;      // Time at start of measurement (milliseconds)
    final Timer timer = new Timer();          // Makes a new timer.

    @Override
    /* Establishes the sensor and the ability to collect data at the start of the data collection */
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.i("HRS","Starting HRS");
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        // Picks out the Heart Rate sensor specifically.
        Sensor mHeartRate = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        mSensorManager.registerListener(this, mHeartRate, SensorManager.SENSOR_DELAY_FASTEST);
        Time_zero = getTime();

        timer.schedule( new TimerTask()     // Initializes a timer.
                        {
                            public void run()       // Runs the imported file based on the timer specified.
                            {
                                stopSelf();    // Stops the Heart Rate Sensor
                            }
                        }, Duration);
        return START_NOT_STICKY;
    }

    private int getTime()
    {
        return (int)System.currentTimeMillis();
    }

    @Override

    public void onDestroy()     // A destroy all activity switch (kill switch)
    {
        mSensorManager.unregisterListener(this);
        timer.cancel();
    }

    @Override
    public void onAccuracyChanged(android.hardware.Sensor sensor, int accuracy)
    {
        // Please do not remove this, the code needs this to function properly. Thank you :-)
    }

    @Override

    public void onSensorChanged(SensorEvent event)      // This is where the data collected by the sensor is saved into a csv file which can be accessed.
    {
        Log.d("Test", "Heart Rate (bpm) : " + String.valueOf(event.values[0]));     // This is a log for the Logcat to be seen.
        String HeartRateMonitor = String.valueOf(event.values[0]);      // This changes the value of the sensor data to a string.

        final String logstring = new Utils().getTime() + "," +
                String.valueOf(event.timestamp) +
                "," +
                HeartRateMonitor +       // Appends the Heart Rate value onto the string
                "," +
                event.accuracy;

        new Thread(new Runnable()
        {
            public void run()
            {
                DataLogger dataLogger = new DataLogger("Heart_Rate_Data.csv", logstring);       // Logs the data into a file that can be retrieved.
                dataLogger.LogData();   // Logs the data to the computer.
            }
        }).start();
    }

    @Override

    /* Unknown but necessary function */
    public IBinder onBind(Intent intent)
    {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
