package com.linklab.INERTIA.besi_c;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class ManualDailyEMA extends WearableActivity
{
    private final Timer promptTimeOut = new Timer();
    private final Preferences Preference = new Preferences();     // Gets an instance from the preferences module.
    private final SystemInformation SystemInformation = new SystemInformation();  // Gets an instance from the system information module
    private final String Sensors = Preference.Sensors;     // Gets the sensors from preferences.
    private final String System = Preference.System;       // Gets the system from preferences.
    private final String Subdirectory_DeviceLogs = Preference.Subdirectory_DeviceLogs;        // This is where all the system logs and data are kept.
    private final int ActivityBeginning = Preference.ActivityBeginning;      // This is the haptic feedback for button presses.
    private final int HapticFeedback = Preference.HapticFeedback;      // This is the haptic feedback for button presses.
    @SuppressLint("SetTextI18n")       // Suppresses the timeouts.

    @Override
    protected void onCreate(Bundle savedInstanceState)      // When the service is created it runs this
    {
        CheckFiles();       // Checks for the files needed
        unlockScreen();     // Unlocks the screen

        super.onCreate(savedInstanceState);     // Starts a saved instance in the system.
        setContentView(R.layout.activity_end_of_day_prompt);        // Gets the EOD EMA prompt activity from the res files.

        final Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);     // Gets the vibrator service from the system
        v.vibrate(ActivityBeginning);     // Vibrates for the specified amount of time in milliseconds.

        Button proceed = findViewById(R.id.Proceed);        // Sets the button proceed to the variable proceed.
        Button snooze = findViewById(R.id.Snooze);        // Sets the button snooze to the variable snooze.
        Button dismiss = findViewById(R.id.Dismiss);        // Sets the button dismiss to the variable dismiss.

        dismiss.setVisibility(View.INVISIBLE);      // Hides the dismiss button from view and disables the button.
        snooze.setVisibility(View.INVISIBLE);       // Hides the snooze button from view and disables the button
        proceed.setText("GO");      // Changes the text on the snooze button to dismiss

        proceed.setOnClickListener(new View.OnClickListener()       // Constantly listens to the proceed button, If proceed is clicked
        {
            @Override
            public void onClick(View view)     // When it is clicked.
            {
                v.vibrate(HapticFeedback);     // Vibrates for the specified amount of time in milliseconds.

                Log.i("Manual EMA Prompts", "Go Clicked, Starting End of Day EMA");     // Logs on Console.

                String data =  ("Manual EMA Prompt," + "'Go' Button Tapped at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
                String data1 =  ("Manual EMA Prompt," + "Started End of Day EMA at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.

                DataLogger datalog = new DataLogger(Subdirectory_DeviceLogs, System, data);      // Logs it into a file called System Activity.
                DataLogger datalog1 = new DataLogger(Subdirectory_DeviceLogs, Sensors, data1);      // Logs it into a file called System Activity.

                datalog.LogData();      // Saves the data into the directory.
                datalog1.LogData();      // Saves the data into the directory.

                Intent StartEMAActivity = new Intent(getBaseContext(), EndOfDayEMA.class);      // Starts the EOD EMA file
                startActivity(StartEMAActivity);    // Moves to the new activity.

                finish();       // Finishes the EOD EMA prompt 3.
            }
        });

        promptTimeOut.schedule(new TimerTask()      // A timer is started by the service
        {
            @Override
            public void run()       // This is run when the timeout is initiated
            {
                v.vibrate(HapticFeedback);     // Vibrates for the specified amount of time in milliseconds.

                Log.i("Manual EMA", "Timeout Initiated");     // Logs on Console.

                String data =  ("Manual EMA," + "Dismissed End of Day EMA at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
                DataLogger datalog = new DataLogger(Subdirectory_DeviceLogs, Sensors, data);      // Logs it into a file called System Activity.
                datalog.LogData();      // Saves the data into the directory.

                finish();       // Finishes the screen
            }
        }, Preference.EoDPrompt_TimeOut);        // Gets the timeout from preferences

        setAmbientEnabled();        // Makes the system ambient.
        setAutoResumeEnabled(true);     // Resumes the main activity.
    }

    private void CheckFiles()       // Checks that the files in the system needed are present
    {
        File sensors = new File(Preference.Directory + SystemInformation.Sensors_Path);     // Gets the path to the Sensors from the system.
        if (!sensors.exists())      // If the file exists
        {
            Log.i("Manual EMA prompts", "Creating Header");     // Logs on Console.

            DataLogger dataLogger = new DataLogger(Subdirectory_DeviceLogs, Sensors, Preference.Sensor_Data_Headers);        /* Logs the Sensors data in a csv format */
            dataLogger.LogData();       // Saves the data to the directory.
        }

        File system = new File(Preference.Directory + SystemInformation.System_Path);     // Gets the path to the system from the system.
        if (!system.exists())      // If the file exists
        {
            Log.i("Manual EMA prompts", "Creating Header");     // Logs on Console.

            DataLogger dataLogger = new DataLogger(Subdirectory_DeviceLogs, System, Preference.System_Data_Headers);        /* Logs the system data in a csv format */
            dataLogger.LogData();       // Saves the data to the directory.
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
        Log.i("Manual EMA", "Destroying Low Battery Screen");     // Logs on Console.

        promptTimeOut.cancel(); // Cancels dismiss timer
        super.onDestroy();      // The activity is killed.
    }
}
