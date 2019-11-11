package com.linklab.INERTIA.besi_c;

// Imports

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;

import java.io.File;

public class PedometerSensor extends Service implements SensorEventListener     // Starts a service for the pedometer.
{
    private SensorManager mSensorManager;       // Creates the sensor manager that looks into the sensor
    private boolean Started = false;        // Creates a boolean to check if it is started.
    private final Preferences Preference = new Preferences();     // Gets an instance from the preferences module.
    private final SystemInformation SystemInformation = new SystemInformation();  // Gets an instance from the system information module
    private final String Sensors = Preference.Sensors;     // Gets the sensors from preferences.
    private final String Pedometer = Preference.Pedometer;     // Gets the file name from preferences.
    private final String Steps = Preference.Steps;     // Gets the sensors from preferences.
    private final String Subdirectory_DeviceActivities = Preference.Subdirectory_DeviceActivities;       // This is where the device data that is used to update something in the app is kept
    private final String Subdirectory_DeviceLogs = Preference.Subdirectory_DeviceLogs;        // This is where all the system logs and data are kept.
    @SuppressLint("WakelockTimeout")        // Suppresses some error messages.

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)    /* Establishes the sensor and the ability to collect data at the start of the data collection */
    {
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);       // Starts a sensor data collection service
        Sensor mPedometer = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);      // Starts it for the sensor pedometer.
        mSensorManager.registerListener(this, mPedometer, SensorManager.SENSOR_DELAY_NORMAL);       // Calls the sensor delay to normal.
        return START_STICKY;    // Makes it so the service restarts if it is stopped.
    }

    @Override
    public void onSensorChanged(SensorEvent event)      // This is where the data collected by the sensor is saved into a csv file which can be accessed.
    {
        CheckFiles();       // Checks that the files needed are present

        if (!Started)       // If the system is not started.
        {
            Started = true;     // Set the boolean value
        }
        else        // If the system has started.
        {
            DataLogger datalogger = new DataLogger(Subdirectory_DeviceActivities, Steps,"yes");      // Logs the file to a string.
            datalogger.WriteData();       // Start logging yes to the file.
        }

        final String logstring = SystemInformation.getTimeStamp() + "," +  event.values[0] + "," + event.accuracy;     // Format the data

        new Thread(new Runnable()       // Starts a new thread for the service.
        {
            public void run()       // This is run in the thread.
            {
                File pedometer = new File(new Preferences().Directory + SystemInformation.Pedometer_Path);     // Gets the path to the Pedometer from the system.
                if (!pedometer.exists())      // If the file exists
                {
                    Log.i("Pedometer Sensor", "Creating Header");     // Logs on Console.

                    DataLogger dataLogger = new DataLogger(Subdirectory_DeviceLogs, Pedometer, new Preferences().Pedometer_Data_Headers);        /* Logs the Pedometer data in a csv format */
                    dataLogger.LogData();       // Saves the data to the directory.
                }

                DataLogger dataLogger = new DataLogger(Subdirectory_DeviceLogs, Pedometer, logstring);       // Logs the data into a file that can be retrieved.
                dataLogger.LogData();   // Logs the data to the directory.
            }
        }).start();     // Starts the runnable.
    }

    private void CheckFiles()
    {
        File sensors = new File(new Preferences().Directory + SystemInformation.Sensors_Path);     // Gets the path to the Sensors from the system.
        if (!sensors.exists())      // If the file exists
        {
            Log.i("Pedometer Sensor", "Creating Header");     // Logs on Console.

            DataLogger dataLogger = new DataLogger(Subdirectory_DeviceLogs, Sensors, new Preferences().Sensor_Data_Headers);        /* Logs the Sensors data in a csv format */
            dataLogger.LogData();       // Saves the data to the directory.
        }

        File steps = new File(new Preferences().Directory + SystemInformation.Steps_Path);     // Gets the path to the Sensors from the system.
        if (!steps.exists())      // If the file exists
        {
            Log.i("Pedometer Sensor", "Creating Header");     // Logs on Console.

            DataLogger dataLogger = new DataLogger(Subdirectory_DeviceActivities, Steps, new Preferences().Step_Data_Headers);        /* Logs the Sensors data in a csv format */
            dataLogger.LogData();       // Saves the data to the directory.
        }
    }

    @Override
    public void onDestroy()     // A destroy service switch (kill switch)
    {
        Log.i("Pedometer Sensor", "Stopping Sensor");     // Logs on Console.

        mSensorManager.unregisterListener(this);        // Kills the listener for the service.
    }

    @Override
    public void onAccuracyChanged(android.hardware.Sensor sensor, int accuracy)     // Changes the accuracy of the system
    {
        // Please do not remove this, the code needs this to function properly. Thank you :-)
    }

    @Override
    /* Unknown but necessary function */
    public IBinder onBind(Intent intent)
    {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
