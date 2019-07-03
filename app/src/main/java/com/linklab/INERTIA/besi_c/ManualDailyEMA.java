package com.linklab.INERTIA.besi_c;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class ManualDailyEMA extends WearableActivity
{
    private final Preferences Preference = new Preferences();     // Gets the preferences list from preferences class
    private final SystemInformation SystemInformation = new SystemInformation();  // Gets an instance from the system information module
    private final int vibrationDuration = Preference.LowBatBuzzDuration;      // This is th vibration duration for the low battery
    private final String System = Preference.System;     // Gets the sensors from preferences.
    private final String Subdirectory_DeviceLogs = Preference.Subdirectory_DeviceLogs;        // This is where all the system logs and data are kept.

    @SuppressLint("WakelockTimeout")
    @Override
    protected void onCreate(Bundle savedInstanceState)      // This is run on creation
    {
        unlockScreen();     // Unlocks the screen

        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);      // Sets the vibrator service.

        super.onCreate(savedInstanceState);     // Makes the screen and saves the instance
        setContentView(R.layout.activity_manual_daily_survey);      // Sets the view to show the manual ema screen
        Button OK = findViewById(R.id.OK);        // Sets the dismiss button

        vibrator.vibrate(vibrationDuration);        // Sets the system to vibrate for that long.

        String data =  ("Manual Daily EMA," + "Started at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
        DataLogger datalog = new DataLogger(Subdirectory_DeviceLogs, System, data);      // Logs it into a file called System Activity.
        datalog.LogData();      // Saves the data into the directory.

        OK.setOnClickListener(new View.OnClickListener() // Waits for the dismiss button to be clicked.
        {
            @Override
            public void onClick(View v)         // When the button is clicked
            {
                String data =  ("Manual Daily EMA," + " Clicked at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
                DataLogger datalog = new DataLogger(Subdirectory_DeviceLogs, System, data);      // Logs it into a file called System Activity.
                datalog.LogData();      // Saves the data into the directory.

                Intent EODEMA = new Intent(getApplicationContext(), EndOfDayEMA.class);       // Calls the low battery class
                startActivity(EODEMA);      // Starts low battery screen

                finish();       // Ends the screen
            }
        });
        setAmbientEnabled();            // Enables Always-on
    }

    private void unlockScreen()         // This unlocks the screen if called
    {
        Window window = this.getWindow();       // Gets the window that is being used
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);      // Dismisses the button
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);      // Ignores the screen if locked
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);        // Turns on the screen
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);        // Keeps the Screen on
        window.addFlags(WindowManager.LayoutParams.FLAG_TOUCHABLE_WHEN_WAKING);        // Keeps the Screen on while waking up
    }

    @Override
    public void onDestroy()     // This is called when the activity is destroyed.
    {
        Log.i("Low Battery", "Destroying Low  Battery Screen");     // Logs on Console.

        super.onDestroy();      // The activity is killed.
    }
}
