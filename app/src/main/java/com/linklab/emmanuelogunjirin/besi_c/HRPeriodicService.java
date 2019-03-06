package com.linklab.emmanuelogunjirin.besi_c;

// Imports
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.util.Timer;
import java.util.TimerTask;

/* This runs the delay timer, and also calls the heart rate sensor itself, the heart rate sensor kills itself and returns here when complete */
public class HRPeriodicService extends Service
{
    public int delay = 0;
    public int period = 4*60*1000;      // This is the duty cycle rate in format (minutes, seconds, milliseconds)

    private void PeriodicService()
    {
        Timer timer = new Timer();          // Makes a new timer.
        timer.schedule( new TimerTask()     // Initializes a timer.
                        {
                            public void run()       // Runs the imported file based on the timer specified.
                            {
                                final Intent HRService = new Intent(getBaseContext(), HeartRateSensor.class);
                                startService(HRService);    // Starts the Heart Rate Sensor
                            }
                        }, delay, period);
    }

    @Override
    /* Establishes the sensor and the ability to collect data at the start of the data collection */
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        PeriodicService();
        return START_STICKY;    // Please do not remove. It is needed. (This allows it to restart if the service is killed)
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
