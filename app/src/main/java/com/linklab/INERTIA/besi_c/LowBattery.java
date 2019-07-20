package com.linklab.INERTIA.besi_c;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class LowBattery extends WearableActivity
{
    private final Preferences Preference = new Preferences();     // Gets the preferences list from preferences class
    private final SystemInformation SystemInformation = new SystemInformation();  // Gets an instance from the system information module
    private final int vibrationDuration = Preference.LowBatBuzzDuration;      // This is th vibration duration for the low battery
    private final String System = Preference.System;     // Gets the sensors from preferences.
    private final String Subdirectory_DeviceLogs = Preference.Subdirectory_DeviceLogs;        // This is where all the system logs and data are kept.
    private int HapticFeedback = Preference.HapticFeedback;      // This is the haptic feedback for button presses.
    private int startHour = Preference.LowBattery_ManualStart_Hour;     // This is the hour the button pops up
    private int startMinute = Preference.LowBattery_ManualStart_Minute;     // This is the minute the button pops up
    private int startSecond = Preference.LowBattery_ManualStart_Second;     // This is the second the button pops up
    private int endHour = Preference.LowBattery_ManualEnd_Hour;     // This is the hour the button goes away
    private int endMinute = Preference.LowBattery_ManualEnd_Minute;     // This is the minute the button goes away
    private int endSecond = Preference.LowBattery_ManualEnd_Second;     // This is the seconds the button goes away

    @Override
    protected void onCreate(Bundle savedInstanceState)      // This is run on creation
    {
        unlockScreen();     // Unlocks the screen

        final Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);      // Sets the vibrator service.

        super.onCreate(savedInstanceState);     // Makes the screen and saves the instance
        setContentView(R.layout.activity_low_battery);      // Sets the view to show the low battery screen

        if (SystemInformation.isSystemCharging(this) ||
                SystemInformation.isTimeBetweenTimes(SystemInformation.getTimeMilitary(), startHour, endHour, startMinute, endMinute, startSecond, endSecond))    // If the watch is currently charging or it is sleeping time,
        {
            finish();       // End
        }
        else    // If the system is not charging
        {
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
                    vibrator.vibrate(HapticFeedback);       // Haptic feedback for the dismiss button

                    String data =  ("Low Battery," + "Dismissed at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
                    DataLogger datalog = new DataLogger(Subdirectory_DeviceLogs, System, data);      // Logs it into a file called System Activity.
                    datalog.LogData();      // Saves the data into the directory.

                    finish();       // It stops the class and the buzzing
                }
            });
            setAmbientEnabled();            // Enables Always-on
        }
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
