package com.linklab.INERTIA.besi_c;

// Imports

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class ESTimerService extends Service         /* This runs the delay timer, and also calls the heart rate sensor itself, the heart rate sensor kills itself and returns here when complete */
{
    public int delay = 0;       // Starts a delay of 0
    private Preferences Preference = new Preferences();     // Gets an instance from the preferences module.
    private SystemInformation SystemInformation = new SystemInformation();  // Gets an instance from the system information module
    public long period = Preference.ESMeasurementInterval + 30000;      // This is the duty cycle rate in format (minutes, seconds, milliseconds)
    private String Sensors = Preference.Sensors;     // Gets the sensors from preferences.
    private Timer ESTimerService;         // Starts the variable timer.
    private PowerManager.WakeLock wakeLock;     // Starts the wakelock service from the system.
    @SuppressLint("WakelockTimeout")        // Suppresses the wakelock.

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)    /* Establishes the sensor and the ability to collect data at the start of the data collection */
    {
        File sensors = new File(Preference.Directory + SystemInformation.Sensors_Path);     // Gets the path to the Sensors from the system.
        if (sensors.exists())      // If the file exists
        {
            Log.i("End of Day EMA Prompts", "No Header Created");     // Logs to console
        }
        else        // If the file does not exist
        {
            Log.i("End of Day EMA prompts", "Creating Header");     // Logs on Console.

            DataLogger dataLogger = new DataLogger(Sensors, Preference.Sensor_Data_Headers);        /* Logs the Sensors data in a csv format */
            dataLogger.LogData();       // Saves the data to the directory.
        }

        Log.i("Estimote", "Starting Estimote Timer Service");     // Logs on Console.

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);     // Starts the power manager service from the system
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "ESService: wakeLock");         // Starts a partial wakelock for the heartrate sensor.
        wakeLock.acquire();     // Starts the wakelock without any timeout.
        PeriodicService(false);     // Makes the periodic service false initially.
        return START_STICKY;    // This allows it to restart if the service is killed
    }

    private void PeriodicService(boolean Stop)      // Starts the periodic data sampling.
    {
        final Intent ESService = new Intent(getBaseContext(), EstimoteService.class);       // Starts a ES service intent from the sensor class.

        if (Stop)       // If it says stop, it kills the HRService.
        {
            Log.i("Estimote Timer Sensor", "Stopping Heart Rate Sensor");     // Logs on Console.

            String data =  ("Estimote Timer Service," + "Stopped Heart Rate Sensor at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
            DataLogger datalog = new DataLogger(Sensors, data);      // Logs it into a file called System Activity.
            datalog.LogData();      // Saves the data into the directory.

            stopService(ESService);     // Stops the Heart Rate Sensor
        }
        else        // If the system is not charging or is not asked to stop
        {
            ESTimerService = new Timer();          // Makes a new timer.
            ESTimerService.schedule(new TimerTask()     // Initializes a timer.
            {
                public void run()       // Runs the imported file based on the timer specified.
                {
                    Log.i("Estimote", "Starting Estimote Service");     // Logs on Console.

                    String data = ("Estimote Timer," + "Started Estimote Service at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
                    DataLogger datalog = new DataLogger(Sensors, data);      // Logs it into a file called System Activity.
                    datalog.LogData();      // Saves the data into the directory.

                    startService(ESService);    // Starts the Estimote service
                }
            }, delay, period);      // Waits for this amount of delay and runs every stated period.
        }
    }

    private boolean isRunning()         // IF the system is running.
    {
        ActivityManager HRManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);     // Get the activity manager for heartrate.

        for (ActivityManager.RunningServiceInfo service : HRManager.getRunningServices(Integer.MAX_VALUE))      // For every running service.
        {
            if (HeartRateSensor.class.getName().equals(service.service.getClassName()))     // If HRService is equal to the class name
            {
                return true;        // Return true.
            }
        }

        return false;       // If not, return false.
    }

    @Override
    public void onDestroy()     // When the service is destroyed.
    {
        Log.i("Estimote", "Destroying Estimote Timer Service");     // Logs on Console.

        String data =  ("Estimote Timer," + "Stopped Estimote Timer at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
        DataLogger datalog = new DataLogger(Sensors, data);      // Logs it into a file called System Activity.
        datalog.LogData();      // Saves the data into the directory.
        if (isRunning())        // If the periodic service is running
        {
            PeriodicService(true);      // Stops the periodic service.
        }
        ESTimerService.cancel();        //  Cancels the ES Timer Service.
        wakeLock.release();     // Releases the wakelock
    }

    @Override
    /* Unknown but necessary function */
    public IBinder onBind(Intent intent)
    {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
