package com.linklab.INERTIA.besi_c;

// Imports
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.Timer;
import java.util.TimerTask;

public class HeartRateSensor extends Service implements SensorEventListener     // This is the file heading, it listens to the physical Heart Rate Senor
{
    public long Duration = new Preferences().HRSampleDuration;        // This is the sampling rate in milliseconds gotten from preferences.
    private SensorManager mSensorManager;       // Creates the sensor manager that looks into the sensor
    int Time_zero;      // Time at start of measurement (milliseconds)
    final Timer HRSensorTimer = new Timer();          // Makes a new timer for HRSensorTimer.

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)    /* Establishes the sensor and the ability to collect data at the start of the data collection */
    {
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);       // Starts the sensor service for any sensor in the system.
        Sensor mHeartRate = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);        // Makes sure it is for the Heart Rate sensor.
        mSensorManager.registerListener(this, mHeartRate, SensorManager.SENSOR_DELAY_FASTEST);      // Registers the listener for the HR sensor in the system.
        Time_zero = getTime();      // Gets the current system time.

        HRSensorTimer.schedule( new TimerTask()     // Initializes a timer.
        {
            public void run()       // Runs the imported file based on the timer specified.
            {
                stopSelf();    // Stops the Heart Rate Sensor
            }
        }, Duration);       // Waits for this amount of duration.

        return START_NOT_STICKY;        // Makes sure the timer is started again if it is killed.
    }

    @Override
    public void onSensorChanged(SensorEvent event)      // This is where the data collected by the sensor is saved into a csv file which can be accessed.
    {
        String HeartRateMonitor = String.valueOf(event.values[0]);      // This changes the value of the sensor data to a string.
        final String logstring = new SystemInformation().getTime() + "," + String.valueOf(event.timestamp) + "," + HeartRateMonitor + "," + event.accuracy;     // Appends the Heart Rate value onto the string

        new Thread(new Runnable()       // Starts a new runnable file.
        {
            public void run()       // Runs when the runnable is called
            {
                DataLogger dataLogger = new DataLogger("Heart_Rate_Data.csv", logstring);       // Logs the data into a file that can be retrieved.
                dataLogger.LogData();   // Logs the data to the computer.
            }
        }).start();     // Starts the runnable.
    }

    private int getTime()   // Gets the time from the system
    {
        return (int)System.currentTimeMillis();     // Returns the system time.
    }

    @Override
    public void onDestroy()     // A destroy all activity switch (kill switch)
    {
        mSensorManager.unregisterListener(this);        // Kills the listener
        HRSensorTimer.cancel();     // Kills the timer.
    }

    @Override
    public void onAccuracyChanged(android.hardware.Sensor sensor, int accuracy)     // Changes the accuracy of the sensor data collected.
    {
        // Please do not remove this, the code needs this to function properly. Thank you :-)
    }

    @Override
    public IBinder onBind(Intent intent)    /* Unknown but necessary function */
    {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
