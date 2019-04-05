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
import android.widget.Button;

import java.util.Timer;
import java.util.TimerTask;

public class EndOfDayPrompt1 extends WearableActivity       // Starts the first EOD-EMA prompt.
{
    private PowerManager.WakeLock wakeLock;     // Starts the power manager in the system.
    private Timer promptTimeOut = new Timer();      // Creates a new timer.
    @SuppressLint("WakelockTimeout")        // Suppresses the wakelock timer.

    @Override
    protected void onCreate(Bundle savedInstanceState)      // When it is created this is initially run.
    {
        super.onCreate(savedInstanceState);     // Creates a saved instance.
        setContentView(R.layout.activity_end_of_day_prompt);        // Gets the layout from the activity EOD-EMA

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);     // Gets the power manager from the system and controls the power distribution
        wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "EOD Prompt 1:wakeLock");     // Gets a full wakelock ability from the system
        wakeLock.acquire();     // Acquires the wakelock without any timeout.

        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);     // Gets the vibrator service from system
        v.vibrate(600);     // Vibrates for the specified amount of milliseconds.

        Button proceed = findViewById(R.id.Proceed);        // Sets the button proceed to the variable proceed.
        final Button snooze = findViewById(R.id.Snooze);        // Sets the button snooze to the variable snooze.
        Button dismiss = findViewById(R.id.Dismiss);        // Sets the button dismiss to the variable dismiss.

        proceed.setOnClickListener(new View.OnClickListener()       // Constantly listens to the proceed button and waits until it is clicked.
        {
            @Override
            public void onClick(View view)     // When it is clicked, this is run
            {
                Log.i("End of Day EMA Prompts", "Prompt 1 - Proceed Clicked, Starting End of Day EMA");     // Logs on Console.

                String data =  ("First End of Day EMA Prompt 'Proceed' Button Tapped at " + new SystemInformation().getTimeStamp());       // This is the format it is logged at.
                DataLogger datalog = new DataLogger("System_Activity.csv",data);      // Logs it into a file called System Activity.
                datalog.LogData();      // Saves the data into the directory.

                String data1 =  ("Started End of Day EMA at " + new SystemInformation().getTimeStamp());       // This is the format it is logged at.
                DataLogger datalog1 = new DataLogger("Sensor_Activity.csv",data1);      // Logs it into a file called System Activity.
                datalog1.LogData();      // Saves the data into the directory.

                Intent StartEMAActivity = new Intent(getBaseContext(), EndOfDayEMA.class);      // Links to the EOD EMA File and starts it.
                startActivity(StartEMAActivity);        // Starts the EOD EMA file.
                finish();       // Finished the EOD EMA screen.
            }
        });

        snooze.setOnClickListener(new View.OnClickListener()       // Constantly listens to the snooze button and waits until it is clicked.
        {
            @Override
            public void onClick(View view)     // When it is clicked, this is run
            {
                Log.i("End of Day EMA Prompts", "Prompt 1 - Snooze Clicked, Starting End of Day EMA Prompt 2");     // Logs on Console.

                String data =  ("First End of Day EMA Prompt 'Snooze' Button Tapped at " + new SystemInformation().getTimeStamp());       // This is the format it is logged at.
                DataLogger datalog = new DataLogger("System_Activity.csv",data);      // Logs it into a file called System Activity.
                datalog.LogData();      // Saves the data into the directory.

                Timer timer = new Timer();      // Starts a timer that runs for the specified time.
                timer.schedule(new TimerTask()      // When the timer is finished, the run void is run.
                {
                    @Override
                    public void run()       // Runs when the timer is finished on a loop.
                    {
                        Log.i("End of Day EMA Prompts", "Prompt 1 - Timer Started Prompt 2");     // Logs on Console.

                        String data =  ("End of Day Prompt 1 Started Prompt 2 at " + new SystemInformation().getTimeStamp());       // This is the format it is logged at.
                        DataLogger datalog = new DataLogger("Sensor_Activity.csv",data);      // Logs it into a file called System Activity.
                        datalog.LogData();      // Saves the data into the directory.

                        Intent StartEMAActivity = new Intent(getBaseContext(), EndOfDayPrompt2.class);      // Starts the EOD EMA third activity.
                        startActivity(StartEMAActivity);    // Starts the activity.
                    }
                }, new Preferences().EoDEMA_Timer_Delay);        // Runs based on the timer that is set in preferences.

                finish();       // Finishes the snooze button.
            }
        });

        dismiss.setOnClickListener(new View.OnClickListener()       // Constantly listens to the dismiss button and waits until it is clicked.
        {
            @Override
            public void onClick(View view)     // WHen it is clicked this is run.
            {
                Log.i("End of Day EMA Prompts", "Prompt 1 - Dismiss Clicked, Stopping all End of Day EMA Services");     // Logs on Console.

                String data =  ("First End of Day EMA Prompt 'Dismiss' Button Tapped at " + new SystemInformation().getTimeStamp());       // This is the format it is logged at.
                DataLogger datalog = new DataLogger("System_Activity.csv",data);      // Logs it into a file called System Activity.
                datalog.LogData();      // Saves the data into the directory.

                String data1 =  ("End of Day Prompt 1 Dismissed End of Day EMA at " + new SystemInformation().getTimeStamp());       // This is the format it is logged at.
                DataLogger datalog1 = new DataLogger("Sensor_Activity.csv",data1);      // Logs it into a file called System Activity.
                datalog1.LogData();      // Saves the data into the directory.

                finish();       // Finishes the EOD EMA.
            }
        });

        promptTimeOut.schedule(new TimerTask()      // This timer is started by the service.
        {
            @Override
            public void run()       // This is run when the timer is started
            {
                Log.i("End of Day EMA Prompts", "Prompt 1 - Timeout Initiated");     // Logs on Console.

                String data =  ("End of Day Prompt 1 automatically Snoozed Prompt 1 at " + new SystemInformation().getTimeStamp());       // This is the format it is logged at.
                DataLogger datalog = new DataLogger("Sensor_Activity.csv",data);      // Logs it into a file called System Activity.
                datalog.LogData();      // Saves the data into the directory.

                snooze.performClick();      // Snooze is automatically clicked by the system.
            }
        }, new Preferences().EoDPrompt_TimeOut);        // Gets the timeout from preferences.

        setAmbientEnabled();        // Enables the ambient mode on the system.
        setAutoResumeEnabled(true);     // Resumes the main activity.
    }

    @Override
    public void onDestroy()     // When the system is destroyed this is run
    {
        Log.i("End of Day EMA Prompts", "Prompt 1 - Service is Destroyed");     // Logs on Console.

        wakeLock.release();     // Kills the wakelock.
        promptTimeOut.cancel(); // Cancels snooze timer
        super.onDestroy();      // Kills the service.
    }
}
