//package com.linklab.INERTIA.besi_c;
//
//// Imports
//
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.PowerManager;
//import android.os.Vibrator;
//import android.support.wearable.activity.WearableActivity;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//
//import java.io.File;
//import java.util.Timer;
//import java.util.TimerTask;
//
//public class EndOfDayPrompt1 extends WearableActivity       // Starts the first EOD-EMA prompt.
//{
//    private PowerManager.WakeLock wakeLock;     // Starts the power manager in the system.
//    private Timer promptTimeOut = new Timer();      // Creates a new timer.
//    private Preferences Preference = new Preferences();     // Gets an instance from the preferences module.
//    private SystemInformation SystemInformation = new SystemInformation();  // Gets an instance from the system information module
//    private String Sensors = Preference.Sensors;     // Gets the sensors from preferences.
//    private String System = Preference.System;       // Gets the system from preferences.
//    private int ActivityBeginning = Preference.ActivityBeginning;      // This is the haptic feedback for button presses.
//    private int HapticFeedback = Preference.HapticFeedback;      // This is the haptic feedback for button presses.
//    @SuppressLint("WakelockTimeout")        // Suppresses the wakelock timer.
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState)      // When it is created this is initially run.
//    {
//        File sensors = new File(Preference.Directory + SystemInformation.Sensors_Path);     // Gets the path to the Sensors from the system.
//        if (sensors.exists())      // If the file exists
//        {
//            Log.i("End of Day EMA Prompts", "No Header Created");     // Logs to console
//        }
//        else        // If the file does not exist
//        {
//            Log.i("End of Day EMA prompts", "Creating Header");     // Logs on Console.
//
//            DataLogger dataLogger = new DataLogger(Sensors, Preference.Sensor_Data_Headers);        /* Logs the Sensors data in a csv format */
//            dataLogger.LogData();       // Saves the data to the directory.
//        }
//
//        File system = new File(Preference.Directory + SystemInformation.System_Path);     // Gets the path to the system from the system.
//        if (system.exists())      // If the file exists
//        {
//            Log.i("End of Day EMA Prompts", "No Header Created");     // Logs to console
//        }
//        else        // If the file does not exist
//        {
//            Log.i("End of Day EMA prompts", "Creating Header");     // Logs on Console.
//
//            DataLogger dataLogger = new DataLogger(System, Preference.System_Data_Headers);        /* Logs the system data in a csv format */
//            dataLogger.LogData();       // Saves the data to the directory.
//        }
//
//        super.onCreate(savedInstanceState);     // Creates a saved instance.
//        setContentView(R.layout.activity_end_of_day_prompt);        // Gets the layout from the activity EOD-EMA
//
//        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);     // Gets the power manager from the system and controls the power distribution
//        wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "EOD Prompt 1:wakeLock");     // Gets a full wakelock ability from the system
//        wakeLock.acquire();     // Acquires the wakelock without any timeout.
//
//        final Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);     // Gets the vibrator service from system
//        v.vibrate(ActivityBeginning);     // Vibrates for the specified amount of milliseconds.
//
//        Button proceed = findViewById(R.id.Proceed);        // Sets the button proceed to the variable proceed.
//        final Button snooze = findViewById(R.id.Snooze);        // Sets the button snooze to the variable snooze.
//        Button dismiss = findViewById(R.id.Dismiss);        // Sets the button dismiss to the variable dismiss.
//
//        proceed.setOnClickListener(new View.OnClickListener()       // Constantly listens to the proceed button and waits until it is clicked.
//        {
//            @Override
//            public void onClick(View view)     // When it is clicked, this is run
//            {
//                v.vibrate(HapticFeedback);     // Vibrates for the specified amount of time in milliseconds.
//
//                Log.i("End of Day EMA Prompts", "Prompt 1 - Proceed Clicked, Starting End of Day EMA");     // Logs on Console.
//
//                String data =  ("First End of Day EMA Prompt," + "'Proceed' Button Tapped at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
//                DataLogger datalog = new DataLogger(System, data);      // Logs it into a file called System Activity.
//                datalog.LogData();      // Saves the data into the directory.
//
//                String data1 =  ("First End of Day EMA Prompt," + "Started End of Day EMA at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
//                DataLogger datalog1 = new DataLogger(Sensors, data1);      // Logs it into a file called System Activity.
//                datalog1.LogData();      // Saves the data into the directory.
//
//                Intent StartEMAActivity = new Intent(getBaseContext(), EndOfDayEMA.class);      // Links to the EOD EMA File and starts it.
//                startActivity(StartEMAActivity);        // Starts the EOD EMA file.
//                finish();       // Finished the EOD EMA screen.
//            }
//        });
//
//        snooze.setOnClickListener(new View.OnClickListener()       // Constantly listens to the snooze button and waits until it is clicked.
//        {
//            @Override
//            public void onClick(View view)     // When it is clicked, this is run
//            {
//                v.vibrate(HapticFeedback);     // Vibrates for the specified amount of time in milliseconds.
//
//                Log.i("End of Day EMA Prompts", "Prompt 1 - Snooze Clicked, Starting End of Day EMA Prompt 2");     // Logs on Console.
//
//                String data =  ("First End of Day EMA Prompt," + "'Snooze' Button Tapped at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
//                DataLogger datalog = new DataLogger(System, data);      // Logs it into a file called System Activity.
//                datalog.LogData();      // Saves the data into the directory.
//
//                Timer timer = new Timer();      // Starts a timer that runs for the specified time.
//                timer.schedule(new TimerTask()      // When the timer is finished, the run void is run.
//                {
//                    @Override
//                    public void run()       // Runs when the timer is finished on a loop.
//                    {
//                        Log.i("End of Day EMA Prompts", "Prompt 1 - Timer Started Prompt 2");     // Logs on Console.
//
//                        String data =  ("First End of Day Prompt 1," + "Started Prompt 2 at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
//                        DataLogger datalog = new DataLogger(Sensors, data);      // Logs it into a file called System Activity.
//                        datalog.LogData();      // Saves the data into the directory.
//
//                        Intent StartEMAActivity = new Intent(getBaseContext(), EndOfDayPrompt2.class);      // Starts the EOD EMA third activity.
//                        startActivity(StartEMAActivity);    // Starts the activity.
//                    }
//                }, Preference.EoDEMA_Timer_Delay);        // Runs based on the timer that is set in preferences.
//
//                finish();       // Finishes the snooze button.
//            }
//        });
//
//        dismiss.setOnClickListener(new View.OnClickListener()       // Constantly listens to the dismiss button and waits until it is clicked.
//        {
//            @Override
//            public void onClick(View view)     // WHen it is clicked this is run.
//            {
//                v.vibrate(HapticFeedback);     // Vibrates for the specified amount of time in milliseconds.
//
//                Log.i("End of Day EMA Prompts", "Prompt 1 - Dismiss Clicked, Stopping all End of Day EMA Services");     // Logs on Console.
//
//                String data =  ("First End of Day EMA Prompt," + "'Dismiss' Button Tapped at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
//                DataLogger datalog = new DataLogger(System, data);      // Logs it into a file called System Activity.
//                datalog.LogData();      // Saves the data into the directory.
//
//                String data1 =  ("End of Day Prompt 1," + "Dismissed End of Day EMA at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
//                DataLogger datalog1 = new DataLogger(Sensors, data1);      // Logs it into a file called System Activity.
//                datalog1.LogData();      // Saves the data into the directory.
//
//                finish();       // Finishes the EOD EMA.
//            }
//        });
//
//        promptTimeOut.schedule(new TimerTask()      // This timer is started by the service.
//        {
//            @Override
//            public void run()       // This is run when the timer is started
//            {
//                v.vibrate(HapticFeedback);     // Vibrates for the specified amount of time in milliseconds.
//
//                Log.i("End of Day EMA Prompts", "Prompt 1 - Timeout Initiated");     // Logs on Console.
//
//                String data =  ("End of Day Prompt 1," + "Automatically Snoozed Prompt 1 at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
//                DataLogger datalog = new DataLogger(Sensors, data);      // Logs it into a file called System Activity.
//                datalog.LogData();      // Saves the data into the directory.
//
//                snooze.performClick();      // Snooze is automatically clicked by the system.
//            }
//        }, Preference.EoDPrompt_TimeOut);        // Gets the timeout from preferences.
//
//        setAmbientEnabled();        // Enables the ambient mode on the system.
//        setAutoResumeEnabled(true);     // Resumes the main activity.
//    }
//
//    @Override
//    public void onDestroy()     // When the system is destroyed this is run
//    {
//        Log.i("End of Day EMA Prompts", "Prompt 1 - Service is Destroyed");     // Logs on Console.
//
//        wakeLock.release();     // Kills the wakelock.
//        promptTimeOut.cancel(); // Cancels snooze timer
//        super.onDestroy();      // Kills the service.
//    }
//}
