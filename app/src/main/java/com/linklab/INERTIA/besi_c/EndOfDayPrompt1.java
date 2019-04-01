package com.linklab.INERTIA.besi_c;

// Imports.
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Vibrator;
import android.support.wearable.activity.WearableActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;
import android.widget.Button;

import java.util.TimerTask;
import java.util.Timer;

public class EndOfDayPrompt1 extends WearableActivity        // This is the class that starts the first EOD-EMA prompt.
{
    private PowerManager.WakeLock wakeLock;     // This is the power regulator of the system.
    private Timer promptTimeOut = new Timer();
    private Vibrator v;
    @SuppressLint("WakelockTimeout")        // Suppresses the error from the wakelock.

    @Override
    protected void onCreate(Bundle savedInstanceState)      // This is run when the activity is first created.
    {
        super.onCreate(savedInstanceState);     // It creates an instance that was saved in the system.
        setContentView(R.layout.activity_end_of_day_prompt);        // Sets the view to the layout that was made in the End of Day layout in the res files.

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);     // Get the power service from the system.
        wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "EOD-EMA Prompt 1: WakeLock");        // The wakelock that turns on the screen.
        wakeLock.acquire();     // Gets the system to turn on the screen.

        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);        // The vibrator that provides haptic feedback.
        v.vibrate(600);     // Vibrates for 600 milliseconds.

        Button proceed = findViewById(R.id.Proceed);    // Sets a variable equal to the Proceed button
        final Button snooze = findViewById(R.id.Snooze);      // Sets a variable equal to the Snooze button
        Button dismiss = findViewById(R.id.Dismiss);    // Sets a variable equal to the Dismiss button
        dismiss.setVisibility(View.INVISIBLE);      // Set the Dismiss button to tbe invisible because it is not needed in this activity.

        proceed.setOnClickListener(new View.OnClickListener()       // Waits for the proceed button to be clicked.
        {
            @Override
            public void onClick(View view)      // When it is clicked.
            {
                String data =  ("First End of Day EMA Prompt 'Proceed' Button Tapped at " + new SystemInformation().getTime());       // This is the format it is logged at.
                DataLogger datalog = new DataLogger("System_Activity.csv",data);      // Logs it into a file called System Activity.
                datalog.LogData();      // Saves the data into the directory.

                Intent StartEMAActivity = new Intent(getBaseContext(), EndOfDayEMA.class);      // Links to the EOD-EMA service.
                startActivity(StartEMAActivity);        // Starts the EOD-EMA file.
                finish();       // Finishes the screen.
            }
        });

        snooze.setOnClickListener(new View.OnClickListener()          // Waits for the Snooze button to be clicked.
        {
            @Override
            public void onClick(View view)      // When it is clicked
            {
                String data =  ("First End of Day EMA Prompt 'Snooze' Button Tapped at " + new SystemInformation().getTime());       // This is the format it is logged at.
                DataLogger datalog = new DataLogger("System_Activity.csv",data);      // Logs it into a file called System Activity.
                datalog.LogData();      // Saves the data into the directory.

                Timer snooze = new Timer();      // A timer is started.
                snooze.schedule(new TimerTask()     // The snooze timer is scheduled to run,
                {
                    @Override
                    public void run()       // When it runs
                    {
                        Intent StartEMAActivity = new Intent(getBaseContext(), EndOfDayPrompt2.class);      // Start the EOD-EMA prompt 2.
                        startActivity(StartEMAActivity);        // Starts the activity.
                    }
                }, new Preferences().EoDEMA_Timer_Delay);       // Calls the delay from the preferences files.
                finish();       // Finishes the screen
            }
        });

        promptTimeOut.schedule(new TimerTask() {
            @Override
            public void run() {
                snooze.performClick();
            }
        },new Preferences().EoDPrompt_TimeOut);

        setAmbientEnabled();    // Turns on the screen.
        setAutoResumeEnabled(true);     // Resumes the main activity.
    }

    @Override
    public void onDestroy()     // When the activity is killed, it calls the onDestroy function.
    {
        wakeLock.release();     // Releases the wakelock.
        promptTimeOut.cancel(); // Cancels snooze timer
        super.onDestroy();      // Kills all methods.
    }
}
