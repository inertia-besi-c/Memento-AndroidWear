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
import android.os.PowerManager;
import android.util.Log;

import java.io.File;

public class PedometerSensor extends Service implements SensorEventListener     // Starts a service for the pedometer.
{
    private SensorManager mSensorManager;       // Creates the sensor manager that looks into the sensor
    private PowerManager.WakeLock wakeLock;     // Creates a wakelock power manager for the service.
    private boolean Started = false;        // Creates a boolean to check if it is started.
    private Preferences Preference = new Preferences();     // Gets an instance from the preferences module.
    private SystemInformation SystemInformation = new SystemInformation();  // Gets an instance from the system information module
    private String Sensors = Preference.Sensors;     // Gets the sensors from preferences.
    private String Pedometer = Preference.Pedometer;     // Gets the file name from preferences.
    private String Steps = Preference.Steps;     // Gets the sensors from preferences.
    @SuppressLint("WakelockTimeout")        // Suppresses some error messages.

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)    /* Establishes the sensor and the ability to collect data at the start of the data collection */
    {
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);     // Regulates the power to the service.
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Pedometer Service:wakeLock");      // Calls a partial wakelock for the service.
        wakeLock.acquire();     // Calls the wakelock.

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);       // Starts a sensor data collection service
        Sensor mPedometer = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);      // Starts it for the sensor pedometer.
        mSensorManager.registerListener(this, mPedometer, SensorManager.SENSOR_DELAY_NORMAL);       // Calls the sensor delay to normal.
        return START_STICKY;    // Makes it so the service restarts if it is stopped.
    }

    @Override
    public void onSensorChanged(SensorEvent event)      // This is where the data collected by the sensor is saved into a csv file which can be accessed.
    {
        File sensors = new File(Preference.Directory + SystemInformation.Sensors_Path);     // Gets the path to the Sensors from the system.
        if (sensors.exists())      // If the file exists
        {
            Log.i("Pedometer Sensor", "No Header Created");     // Logs to console
        }
        else        // If the file does not exist
        {
            Log.i("Pedometer Sensor", "Creating Header");     // Logs on Console.

            DataLogger dataLogger = new DataLogger(Sensors, Preference.Sensor_Data_Headers);        /* Logs the Sensors data in a csv format */
            dataLogger.LogData();       // Saves the data to the directory.
        }

        File steps = new File(Preference.Directory + SystemInformation.Steps_Path);     // Gets the path to the Sensors from the system.
        if (steps.exists())      // If the file exists
        {
            Log.i("Pedometer Sensor", "No Header Created");     // Logs to console
        }
        else        // If the file does not exist
        {
            Log.i("Pedometer Sensor", "Creating Header");     // Logs on Console.

            DataLogger dataLogger = new DataLogger(Steps, Preference.Step_Data_Headers);        /* Logs the Sensors data in a csv format */
            dataLogger.LogData();       // Saves the data to the directory.
        }

        if (!Started)       // If the system is not started.
        {
            Started = true;     // Set the boolean value
        }
        else        // If the system has started.
        {
            new DataLogger(Steps,"yes").WriteData();       // Start logging yes to the file.
        }

        final String logstring = SystemInformation.getTimeStamp() + "," + String.valueOf(event.timestamp) + "," + String.valueOf(event.values[0]) + "," + String.valueOf(event.accuracy);     // Format the data

        new Thread(new Runnable()       // Starts a new thread for the service.
        {
            public void run()       // This is run in the thread.
            {
                File pedometer = new File(Preference.Directory + SystemInformation.Pedometer_Path);     // Gets the path to the Pedometer from the system.
                if (pedometer.exists())      // If the file exists
                {
                    Log.i("Pedometer Sensor", "No Header Created");     // Logs to console
                }
                else        // If the file does not exist
                {
                    Log.i("Pedometer Sensor", "Creating Header");     // Logs on Console.

                    DataLogger dataLogger = new DataLogger(Pedometer, Preference.Pedometer_Data_Headers);        /* Logs the Pedometer data in a csv format */
                    dataLogger.LogData();       // Saves the data to the directory.
                }

                DataLogger dataLogger = new DataLogger(Pedometer, logstring);       // Logs the data into a file that can be retrieved.
                dataLogger.LogData();   // Logs the data to the directory.
            }
        }).start();     // Starts the runnable.
    }

    @Override
    public void onDestroy()     // A destroy service switch (kill switch)
    {
        Log.i("Pedometer Sensor", "Stopping Sensor");     // Logs on Console.

        mSensorManager.unregisterListener(this);        // Kills the listener for the service.
        wakeLock.release();     // Releases the wakelock power.
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
