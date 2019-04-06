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

public class HRTimerService extends Service         /* This runs the delay timer, and also calls the heart rate sensor itself, the heart rate sensor kills itself and returns here when complete */
{
    public int delay = 0;       // Starts a delay of 0
    public long period = new Preferences().HRMeasurementInterval;      // This is the duty cycle rate in format (minutes, seconds, milliseconds)
    private String Sensors = new Preferences().Sensors;     // Gets the sensors from preferences.
    private String Battery = new Preferences().Battery;     // Gets the sensors from preferences.
    private Timer HRTimerService;         // Starts the variable timer.
    private PowerManager.WakeLock wakeLock;     // Starts the wakelock service from the system.
    @SuppressLint("WakelockTimeout")        // Suppresses the wakelock.

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)    /* Establishes the sensor and the ability to collect data at the start of the data collection */
    {
        File sensors = new File(new Preferences().Directory + new SystemInformation().Sensors_Path);     // Gets the path to the Sensors from the system.
        if (sensors.exists())      // If the file exists
        {
            Log.i("Heart Rate Timer Sensor", "No Header Created");     // Logs to console
        }
        else        // If the file does not exist
        {
            Log.i("Heart Rate Timer Sensor", "Creating Header");     // Logs on Console.

            DataLogger dataLogger = new DataLogger(Sensors, new Preferences().Sensor_Data_Headers);        /* Logs the Sensors data in a csv format */
            dataLogger.LogData();       // Saves the data to the directory.
        }

        File battery = new File(new Preferences().Directory + new SystemInformation().Battery_Path);     // Gets the path to the Sensors from the system.
        if (battery.exists())      // If the file exists
        {
            Log.i("Heart Rate Timer Sensor", "No Header Created");     // Logs to console
        }
        else        // If the file does not exist
        {
            Log.i("Heart Rate Timer Sensor", "Creating Header");     // Logs on Console.

            DataLogger dataLogger = new DataLogger(Battery, new Preferences().Battery_Data_Headers);        /* Logs the Sensors data in a csv format */
            dataLogger.LogData();       // Saves the data to the directory.
        }


        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);     // Starts the power manager service from the system
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "HRService: wakeLock");         // Starts a partial wakelock for the heartrate sensor.
        wakeLock.acquire();     // Starts the wakelock without any timeout.
        PeriodicService(false);     // Makes the periodic service false initially.

        return START_STICKY;    // This allows it to restart if the service is killed
    }

    private void PeriodicService(boolean Stop)      // Starts the periodic data sampling.
    {
        final Intent HRService = new Intent(getBaseContext(), HeartRateSensor.class);       // Starts a HR service intent from the sensor class.

        if (Stop)       // If it says stop, it kills the HRService.
        {
            Log.i("Heart Rate Timer Sensor", "Stopping Heart Rate Sensor");     // Logs on Console.

            String data =  ("Heart Rate Timer Service," + "Stopped Heart Rate Sensor at," + new SystemInformation().getTimeStamp());       // This is the format it is logged at.
            DataLogger datalog = new DataLogger(Sensors, data);      // Logs it into a file called System Activity.
            datalog.LogData();      // Saves the data into the directory.

            stopService(HRService);     // Stops the Heart Rate Sensor
        }
        else    // Else it just keeps going.
        {
            HRTimerService = new Timer();          // Makes a new timer.
            HRTimerService.schedule( new TimerTask()     // Initializes a timer.
            {
                public void run()       // Runs the imported file based on the timer specified.
                {
                    Log.i("Heart Rate Timer Sensor", "Starting Heart Rate Sensor");     // Logs on Console.

                    SystemInformation info = new SystemInformation();       // Gets system information into the system
                    String data = info.getTimeStamp() + ",Discharging," + info.getBatteryLevel(getApplicationContext());        // Gets the battery level information and logs it
                    DataLogger datalog = new DataLogger(Battery, data);      // Logs it into a file called Charging time.
                    datalog.LogData();      // Saves the data into the directory.

                    String dataHRT =  ("Heart Rate Timer Service," + "Started Heart Rate Sensor at," + new SystemInformation().getTimeStamp());       // This is the format it is logged at.
                    DataLogger datalogHRT = new DataLogger(Sensors, dataHRT);      // Logs it into a file called System Activity.
                    datalogHRT.LogData();      // Saves the data into the directory.

                    startService(HRService);    // Starts the Heart Rate Sensor
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
        Log.i("Heart Rate Timer Sensor", "Destroying Timer Service");     // Logs on Console.

        HRTimerService.cancel();        //  Cancels the HR Timer Service.
        wakeLock.release();     // Releases the wakelock

        if (isRunning())        // If the periodic service is running
        {
            PeriodicService(true);      // Stops the periodic service.
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
