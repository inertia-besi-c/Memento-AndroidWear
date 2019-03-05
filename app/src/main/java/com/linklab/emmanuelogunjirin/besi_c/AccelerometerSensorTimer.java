package com.linklab.emmanuelogunjirin.besi_c;

// Imports
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;

import java.util.Timer;
import java.util.TimerTask;

public class AccelerometerSensorTimer extends WearableActivity      // File heading.
{
    private int AccelerometerCounter = 0;       // Accelerometer Counter, alternates between even and odd numbers to alternate time.

    @Override

    protected void onCreate(Bundle savedInstanceState)      // This runs when you run the file.
    {
        super.onCreate(savedInstanceState);
        Movetasktoback(true);       // Hopefully this will be able to dismiss the UI screen that comes to the foreground.

        final Intent AService = new Intent(this, AccelerometerSensor.class);       // Calls the Accelerometer sensor File.
        Timer timer = new Timer();          // Makes a new timer.
        timer.schedule( new TimerTask()     // Initializes a timer.
                        {
                            public void run()       // Runs the imported file based on the timer specified.
                            {
                                if (AccelerometerCounter%2 == 0)
                                {
                                    startService(AService);    // Starts the Accelerometer Sensor
                                }
                                else if (AccelerometerCounter%2 == 1)
                                {
                                    stopService(AService);     // Stops the Accelerometer Sensor
                                }
                                AccelerometerCounter ++;    // Increments the Accelerometer Counter to alternate even and odd numbers.
                            }
                        },
                0, 10000);  // This is the timer control "Period" controls both the time on and time off for the Accelerometer in Milliseconds.
    }

    public void Movetasktoback(boolean b)
    {
        // Some function to push the UI to the back??? Leaving Main Activity in main UI...
    }
}

