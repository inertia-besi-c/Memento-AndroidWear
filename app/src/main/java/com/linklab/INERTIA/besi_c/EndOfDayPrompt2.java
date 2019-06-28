package com.linklab.INERTIA.besi_c;

// Imports

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

public class EndOfDayPrompt2 extends WearableActivity       // This is the EOD EMA that is run for the second and last time.
{
    private PowerManager.WakeLock wakeLock;     // Starts the power manager and the wakelock from the system.
    private final Timer promptTimeOut = new Timer();
    private final Preferences Preference = new Preferences();     // Gets an instance from the preferences module.
    private final SystemInformation SystemInformation = new SystemInformation();  // Gets an instance from the system information module
    private final String Sensors = Preference.Sensors;     // Gets the sensors from preferences.
    private final String System = Preference.System;       // Gets the system from preferences.
    private final String Subdirectory_DeviceLogs = Preference.Subdirectory_DeviceLogs;        // This is where all the system logs and data are kept.
    private final int ActivityBeginning = Preference.ActivityBeginning;      // This is the haptic feedback for button presses.
    private final int HapticFeedback = Preference.HapticFeedback;      // This is the haptic feedback for button presses.
    @SuppressLint({"WakelockTimeout", "SetTextI18n"})       // Suppresses the timeouts.

    @Override
    protected void onCreate(Bundle savedInstanceState)      // When the service is created it runs this
    {
        CheckFiles();       // Checks for the files needed
        unlockScreen();     // Unlocks the screen

        super.onCreate(savedInstanceState);     // Starts a saved instance in the system.
        setContentView(R.layout.activity_end_of_day_prompt);        // Gets the EOD EMA prompt activity from the res files.

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);     // Gets the wakelock from the system
        wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "EOD Prompt 2:wakeLock");     // The system is started with a full wakelock.
        wakeLock.acquire();     // Keeps the wakelock from a timeout.

        final Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);     // Gets the vibrator service from the system
        v.vibrate(ActivityBeginning);     // Vibrates for the specified amount of time in milliseconds.

        Button proceed = findViewById(R.id.Proceed);        // Sets the button proceed to the variable proceed.
        final Button snooze = findViewById(R.id.Snooze);        // Sets the button snooze to the variable snooze.
        Button dismiss = findViewById(R.id.Dismiss);        // Sets the button dismiss to the variable dismiss.

        dismiss.setVisibility(View.INVISIBLE);      // Hides the dismiss button from view and disables the button.
        snooze.setText("Dismiss");      // Changes the text on the snooze button to dismiss
        snooze.setBackgroundColor(getResources().getColor(R.color.dark_red));       // Changes the color of the snooze button to dismiss.

        proceed.setOnClickListener(new View.OnClickListener()       // Constantly listens to the proceed button, If proceed is clicked
        {
            @Override
            public void onClick(View view)     // When it is clicked.
            {
                v.vibrate(HapticFeedback);     // Vibrates for the specified amount of time in milliseconds.

                Log.i("End of Day EMA Prompts", "Prompt 2 - Proceed Clicked, Starting End of Day EMA");     // Logs on Console.

                String data =  ("Second End of Day EMA Prompt," + "'Proceed' Button Tapped at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
                String data1 =  ("End of Day Prompt 2," + "Started End of Day EMA at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.

                DataLogger datalog = new DataLogger(Subdirectory_DeviceLogs, System, data);      // Logs it into a file called System Activity.
                DataLogger datalog1 = new DataLogger(Subdirectory_DeviceLogs, Sensors, data1);      // Logs it into a file called System Activity.

                datalog.LogData();      // Saves the data into the directory.
                datalog1.LogData();      // Saves the data into the directory.

                Intent StartEMAActivity = new Intent(getBaseContext(), EndOfDayEMA.class);      // Starts the EOD EMA file
                startActivity(StartEMAActivity);    // Moves to the new activity.

                finish();       // Finishes the EOD EMA prompt 3.
            }
        });

        snooze.setOnClickListener(new View.OnClickListener()        // Constantly listens until the snooze button is clicked.
        {
            @Override
            public void onClick(View view)     // If the button is clicked
            {
                v.vibrate(HapticFeedback);     // Vibrates for the specified amount of time in milliseconds.

                Log.i("End of Day EMA Prompts", "Prompt 2 - Dismiss Clicked, Destroying End of Day EMA");     // Logs on Console.

                String data =  ("Second End of Day EMA Prompt," + "'Dismiss' Button Tapped at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
                String data1 =  ("End of Day Prompt 2," + "Dismissed End of Day EMA at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.

                DataLogger datalog = new DataLogger(Subdirectory_DeviceLogs, System, data);      // Logs it into a file called System Activity.
                DataLogger datalog1 = new DataLogger(Subdirectory_DeviceLogs, Sensors, data1);      // Logs it into a file called System Activity.

                datalog.LogData();      // Saves the data into the directory.
                datalog1.LogData();      // Saves the data into the directory.

                finish();       // Finish and end the service.
            }
        });

        promptTimeOut.schedule(new TimerTask()      // A timer is started by the service
        {
            @Override
            public void run()       // This is run when the timeout is initiated
            {
                v.vibrate(HapticFeedback);     // Vibrates for the specified amount of time in milliseconds.

                Log.i("End of Day EMA Prompts", "Prompt 2 - Timeout Initiated");     // Logs on Console.

                String data =  ("End of Day Prompt 2," + "Dismissed End of Day EMA at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
                DataLogger datalog = new DataLogger(Subdirectory_DeviceLogs, Sensors, data);      // Logs it into a file called System Activity.
                datalog.LogData();      // Saves the data into the directory.

                snooze.performClick();      // Snooze is automatically clicked by the program
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
            Log.i("End of Day EMA prompts", "Creating Header");     // Logs on Console.

            DataLogger dataLogger = new DataLogger(Subdirectory_DeviceLogs, Sensors, Preference.Sensor_Data_Headers);        /* Logs the Sensors data in a csv format */
            dataLogger.LogData();       // Saves the data to the directory.
        }

        File system = new File(Preference.Directory + SystemInformation.System_Path);     // Gets the path to the system from the system.
        if (!system.exists())      // If the file exists
        {
            Log.i("End of Day EMA prompts", "Creating Header");     // Logs on Console.

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
    }

    @Override
    public void onDestroy()     // When the system is destroyed.
    {
        Log.i("End of Day EMA Prompts", "Prompt 2 - Service is Destroyed");     // Logs on Console.

        wakeLock.release();     // The wakelock is released.
        promptTimeOut.cancel(); // Cancels dismiss timer
        super.onDestroy();      // The service is killed.
    }
}
