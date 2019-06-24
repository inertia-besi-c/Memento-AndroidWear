package com.linklab.INERTIA.besi_c;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.Button;

public class LowBattery extends WearableActivity
{
    private final Preferences Preference = new Preferences();     // Gets the preferences list from preferences class
    private final SystemInformation SystemInformation = new SystemInformation();  // Gets an instance from the system information module
    private final int vibrationDuration = Preference.LowBatBuzzDuration;      // This is th vibration duration for the low battery
    private final String System = Preference.System;     // Gets the sensors from preferences.
    private final String Subdirectory_DeviceLogs = Preference.Subdirectory_DeviceLogs;        // This is where all the system logs and data are kept.

    @Override
    protected void onCreate(Bundle savedInstanceState)      // This is run on creation
    {
        super.onCreate(savedInstanceState);     // Makes the screen and saves the instance
        setContentView(R.layout.activity_low_battery);      // Sets the view to show the low battery screen

        if (SystemInformation.isSystemCharging(this))       // If the watch is currently charging,
        {
            finish();       // End
        }
        else    // If the system is not charging
        {
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);      // Sets the vibrator service.
            vibrator.vibrate(vibrationDuration);        // Sets the system to vibrate for that long.
            Button dismiss = findViewById(R.id.Dismiss);        // Sets the dismiss button

            String data =  ("Low Battery," + "Started at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
            DataLogger datalog = new DataLogger(Subdirectory_DeviceLogs, System, data);      // Logs it into a file called System Activity.
            datalog.LogData();      // Saves the data into the directory.

            dismiss.setOnClickListener(new View.OnClickListener() // Waits for the dismiss button to be clicked.
            {
                @Override
                public void onClick(View v)         // When the button is clicked
                {
                    String data =  ("Low Battery," + "Dismissed at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
                    DataLogger datalog = new DataLogger(Subdirectory_DeviceLogs, System, data);      // Logs it into a file called System Activity.
                    datalog.LogData();      // Saves the data into the directory.

                    finish();       // It stops the class and the buzzing
                }
            });
            setAmbientEnabled();            // Enables Always-on
        }
    }
}
