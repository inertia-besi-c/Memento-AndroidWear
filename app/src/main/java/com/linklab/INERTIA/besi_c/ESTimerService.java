package com.linklab.INERTIA.besi_c;

// Imports

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class ESTimerService extends Service         /* This runs the delay timer, and also calls the heart rate sensor itself, the heart rate sensor kills itself and returns here when complete */
{
    private final Preferences Preference = new Preferences();     // Gets an instance from the preferences module.
    private final SystemInformation SystemInformation = new SystemInformation();  // Gets an instance from the system information module
    private final long period = Preference.ESMeasurementInterval + 30000;      // This is the duty cycle rate in format (minutes, seconds, milliseconds)
    private final String Sensors = Preference.Sensors;     // Gets the sensors from preferences.
    private Timer ESTimerService;         // Starts the variable timer.
    private final String Step = Preference.Steps;     // Gets the step file from preferences.
    private final String Subdirectory_DeviceLogs = Preference.Subdirectory_DeviceLogs;        // This is where all the system logs and data are kept.
    private final String Subdirectory_DeviceActivities = Preference.Subdirectory_DeviceActivities;        // This is where all the system logs and data are kept.
    private int ActivityCycleCount = 0;     // This is the amount of times the watch has not moved in a given time
    private final int MaxActivityCycleCount = Preference.MaxActivityCycleCount;       // This is the maximum amount of inactivity that turns off the estimote
    @SuppressLint("WakelockTimeout")        // Suppresses the wakelock.

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)    /* Establishes the sensor and the ability to collect data at the start of the data collection */
    {
        File sensors = new File(Preference.Directory + SystemInformation.Sensors_Path);     // Gets the path to the Sensors from the system.
        if (!sensors.exists())      // If the file exists
        {
            Log.i("Estimote Timer Sensor", "Creating Header");     // Logs on Console.

            DataLogger dataLogger = new DataLogger(Subdirectory_DeviceLogs, Sensors, Preference.Sensor_Data_Headers);        /* Logs the Sensors data in a csv format */
            dataLogger.LogData();       // Saves the data to the directory.
        }

        Log.i("Estimote Timer Sensor", "Starting Estimote Timer Service");     // Logs on Console.

        PeriodicService(false);     // Makes the periodic service false initially.
        return START_STICKY;    // This allows it to restart if the service is killed
    }

    private void PeriodicService(boolean Stop)      // Starts the periodic data sampling.
    {
        final Intent ESService = new Intent(getBaseContext(), EstimoteService.class);       // Starts a ES service intent from the sensor class.

        if (Stop)       // If it says stop, it kills the HRService.
        {
            Log.i("Estimote Timer Sensor", "Stopping Estimote Timer Service");     // Logs on Console.

            String data =  ("Estimote Timer Service," + "Stopped Estimote Timer Service at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
            DataLogger datalog = new DataLogger(Subdirectory_DeviceLogs, Sensors, data);      // Logs it into a file called System Activity.
            datalog.LogData();      // Saves the data into the directory.

            stopService(ESService);     // Stops the Heart Rate Sensor
        }
        else        // If the system is not charging or is not asked to stop
        {
            ESTimerService = new Timer();          // Makes a new timer.
            int delay = 0;            // Starts a delay of 0
            ESTimerService.schedule(new TimerTask()     // Initializes a timer.
            {
                public void run()       // Runs the imported file based on the timer specified.
                {
                    Log.i("Estimote", "Starting Estimote Service");     // Logs on Console.

                    String data = ("Estimote Timer," + "Started Estimote Service at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
                    DataLogger datalog = new DataLogger(Subdirectory_DeviceLogs, Sensors, data);      // Logs it into a file called System Activity.
                    datalog.LogData();      // Saves the data into the directory.

                    if (Active())       // If the estimote is asked to be active
                        startService(ESService);    // Starts the Estimote service
                    else        // If the estimote is to be killed
                    {
                        cancel();       // Cancel the run
                        stopSelf();     // Stop the timer
                    }
                }
            }, delay, period);      // Waits for this amount of delay and runs every stated period.
        }
    }

    private boolean isRunning()         // IF the system is running.
    {
        ActivityManager ESManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);     // Get the activity manager for heartrate.

        for (ActivityManager.RunningServiceInfo service : ESManager.getRunningServices(Integer.MAX_VALUE))      // For every running service.
        {
            if (ESTimerService.class.getName().equals(service.service.getClassName()))     // If HRService is equal to the class name
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
        DataLogger datalog = new DataLogger(Subdirectory_DeviceLogs, Sensors, data);      // Logs it into a file called System Activity.
        datalog.LogData();      // Saves the data into the directory.

        if (isRunning())        // If the periodic service is running
        {
            PeriodicService(true);      // Stops the periodic service.
        }
        ESTimerService.cancel();        //  Cancels the ES Timer Service.
    }

    private boolean Active()     // Checks if the person is active
    {
        DataLogger stepActivity = new DataLogger(Subdirectory_DeviceActivities, Step,"no");        // Logs data to the step file
        if(stepActivity.ReadData().contains("yes"))     // And there are steps going
        {
            ActivityCycleCount = 0;     // Resets the activity
            stepActivity.WriteData();       // writes the data

            String data =  ("Estimote Timer," + "Starting the Estimote," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
            DataLogger datalog = new DataLogger(Subdirectory_DeviceLogs, Sensors, data);      // Logs it into a file called System Activity.
            datalog.LogData();      // Saves the data into the directory.

            return true;        // Returns true
        }
        else        // If there are no steps
        {
            ActivityCycleCount ++;      // Increment activity cycle

            String data =  ("Estimote Timer," + "No Activity at " + ActivityCycleCount + "," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
            DataLogger datalog = new DataLogger(Subdirectory_DeviceLogs, Sensors, data);      // Logs it into a file called System Activity.
            datalog.LogData();      // Saves the data into the directory.
        }
        if (ActivityCycleCount >= MaxActivityCycleCount)        // If the activity cycle is greater than the max set
        {
            String data =  ("Estimote Timer," + "Stopped the Estimote Timer," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
            DataLogger datalog = new DataLogger(Subdirectory_DeviceLogs, Sensors, data);      // Logs it into a file called System Activity.
            datalog.LogData();      // Saves the data into the directory.

            return false;       // Return false
        }
        else        // If not
        {
            return true;        // return true
        }
    }

    @Override
    /* Unknown but necessary function */
    public IBinder onBind(Intent intent)
    {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
