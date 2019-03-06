package com.linklab.emmanuelogunjirin.besi_c;

// Imports
import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

import java.util.Timer;
import java.util.TimerTask;

/* This runs the delay timer, and also calls the heart rate sensor itself, the heart rate sensor kills itself and returns here when complete */
public class HRPeriodicService extends Service
{
    public int delay = 0;
    public int period;      // This is the duty cycle rate in format (minutes, seconds, milliseconds)
    private Timer timer;

    private void PeriodicService(int Duration, boolean Stop)
    {
        final int duration = Duration;
        final Intent HRService = new Intent(getBaseContext(), HeartRateSensor.class);
        if (Stop)
        {
            stopService(HRService);
        }
        else{
        timer = new Timer();          // Makes a new timer.
        timer.schedule( new TimerTask()     // Initializes a timer.
                        {
                            public void run()       // Runs the imported file based on the timer specified.
                            {
                                HRService.putExtra("SampleDuration",duration);
                                startService(HRService);    // Starts the Heart Rate Sensor
                            }
                        }, delay, period);
    }}

    @Override
    /* Establishes the sensor and the ability to collect data at the start of the data collection */
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Bundle extras = intent.getExtras();
        period = (int) extras.get("MeasurementInterval");
        PeriodicService((int)extras.get("SampleDuration"),false);
        return START_STICKY;    // Please do not remove. It is needed. (This allows it to restart if the service is killed)
    }
     @Override
     public void onDestroy()
     {
         timer.cancel();
         if (isRunning(HeartRateSensor.class))
         {
             PeriodicService(0,true);
         }
     }

    private boolean isRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
