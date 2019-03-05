package com.linklab.emmanuelogunjirin.besi_c;

// Imports
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;

import java.util.Timer;
import java.util.TimerTask;

public class HeartRateSensorTimer extends WearableActivity      // File heading.
{
    private int HeartRateCounter = 0;       // Heart Rate Counter, alternates between even and odd numbers to alternate time.

    @Override

    protected void onCreate(Bundle savedInstanceState)      // This runs when you run the file.
    {
        super.onCreate(savedInstanceState);
        Movetasktoback(true);       // Hopefully this will be able to dismiss the UI screen that comes to the foreground.

        final Intent HRService = new Intent(this, HeartRateSensor.class);       // Calls the Heart Rate Sensor File.
        Timer timer = new Timer();          // Makes a new timer.
        timer.schedule( new TimerTask()     // Initializes a timer.
        {
            public void run()       // Runs the imported file based on the timer specified.
            {
                if (HeartRateCounter%2 == 0)
                {
                    startService(HRService);    // Starts the Heart Rate Sensor
                }
                else if (HeartRateCounter%2 == 1)
                {
                    stopService(HRService);     // Stops the Heart Rate Sensor
                }
                HeartRateCounter ++;    // Increments the Heart Rate Counter to alternate even and odd numbers.
            }
        },
                0, 10000);  // This is the timer control "Period" controls both the time on and time off for the Heart Rate Sensor in Milliseconds.
    }

    public void Movetasktoback(boolean b)
    {
        // Some function to push the UI to the back??? Leaving Main Activity in main UI...
    }
}

