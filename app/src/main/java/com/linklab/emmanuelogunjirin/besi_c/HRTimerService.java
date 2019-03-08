package com.linklab.emmanuelogunjirin.besi_c;

// Imports
import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/* This runs the delay timer, and also calls the heart rate sensor itself, the heart rate sensor kills itself and returns here when complete */
public class HRTimerService extends Service
{
    public int delay = 0;
    public long period = new Preferences().HRMeasurementInterval;      // This is the duty cycle rate in format (minutes, seconds, milliseconds)
    private Timer timer;
    private PowerManager.WakeLock wakeLock;


    @Override
    /* Establishes the sensor and the ability to collect data at the start of the data collection */
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.i("Heart Rate Sensor","Starting Heart Rate Sensor");
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "HRService:wakeLock");
        wakeLock.acquire();
        PeriodicService(false);
        return START_STICKY;    // Please do not remove. It is needed. (This allows it to restart if the service is killed)
    }

     @Override

     public void onDestroy()
     {
         timer.cancel();
         wakeLock.release();
         if (isRunning())
         {
             PeriodicService(true);
         }
     }

    private boolean isRunning()
    {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
        {
            if (HeartRateSensor.class.getName().equals(service.service.getClassName()))
            {
                return true;
            }
        }
        return false;
    }

    private void PeriodicService(boolean Stop)
    {
        final Intent HRService = new Intent(getBaseContext(), HeartRateSensor.class);
        if (Stop)
        {
            stopService(HRService);     // Stops the Heart Rate Sensor
        }
        else
        {
            timer = new Timer();          // Makes a new timer.
            timer.schedule( new TimerTask()     // Initializes a timer.
            {
                public void run()       // Runs the imported file based on the timer specified.
                {
                    startService(HRService);    // Starts the Heart Rate Sensor
                }
            }, delay, period);
        }}

    @Override

    public IBinder onBind(Intent intent)
    {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
