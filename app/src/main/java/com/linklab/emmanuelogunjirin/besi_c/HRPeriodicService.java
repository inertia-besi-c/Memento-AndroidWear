package com.linklab.emmanuelogunjirin.besi_c;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.time.Period;
import java.util.Timer;
import java.util.TimerTask;

public class HRPeriodicService extends Service {
    public int delay = 0;
    public int period = 5*60*1000;

    public HRPeriodicService() {
    }

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
                        },
                delay, period);
    }
    @Override
    /* Establishes the sensor and the ability to collect data at the start of the data collection */
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        PeriodicService();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
