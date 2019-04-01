package com.linklab.INERTIA.besi_c;

// Imports
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.Button;

import java.util.Timer;
import java.util.TimerTask;

public class EndOfDayPrompt2 extends WearableActivity       // Starts the EOD-EMA prompt after the first one was snoozed.
{
    private PowerManager.WakeLock wakeLock;     // Starts the power manager in the system.
    private Timer promptTimeOut = new Timer();
    private Vibrator v;
    @SuppressLint("WakelockTimeout")        // Suppresses the wakelock timer.

    @Override
    protected void onCreate(Bundle savedInstanceState)      // When it is created this is initially run.
    {
        super.onCreate(savedInstanceState);     // Creates a saved instance.
        setContentView(R.layout.activity_end_of_day_prompt);        // Gets the layout from the activity EOD-EMA

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);     // Gets the power manager from the system and controls the power distribution
        wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "EOD Prompt 2:wakeLock");     // Gets a full wakelock ability from the system
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
                String data =  ("Second End of Day EMA Prompt 'Proceed' Button Tapped at " + new SystemInformation().getTime());       // This is the format it is logged at.
                DataLogger datalog = new DataLogger("System_Activity.csv",data);      // Logs it into a file called System Activity.
                datalog.LogData();      // Saves the data into the directory.

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
                String data =  ("Second End of Day EMA Prompt 'Snooze' Button Tapped at " + new SystemInformation().getTime());       // This is the format it is logged at.
                DataLogger datalog = new DataLogger("System_Activity.csv",data);      // Logs it into a file called System Activity.
                datalog.LogData();      // Saves the data into the directory.

                Timer timer = new Timer();      // Starts a timer that runs for the specified time.
                timer.schedule(new TimerTask()      // When the timer is finished, the run void is run.
                {
                    @Override
                    public void run()       // Runs when the timer is finished on a loop.
                    {
                        Intent StartEMAActivity = new Intent(getBaseContext(), EndOfDayPrompt3.class);      // Starts the EOD EMA third activity.
                        startActivity(StartEMAActivity);    // Starts the activity.
                    }
                },new Preferences().EoDEMA_Timer_Delay);        // Runs based on the timer that is set in preferences.
                finish();       // Finishes the snooze button.
            }
        });

        dismiss.setOnClickListener(new View.OnClickListener()       // Constantly listens to the dismiss button and waits until it is clicked.
        {
            @Override
            public void onClick(View view)     // WHen it is clicked this is run.
            {
                String data =  ("Second End of Day EMA Prompt 'Dismiss' Button Tapped at " + new SystemInformation().getTime());       // This is the format it is logged at.
                DataLogger datalog = new DataLogger("System_Activity.csv",data);      // Logs it into a file called System Activity.
                datalog.LogData();      // Saves the data into the directory.

                finish();       // Finishes the EOD EMA.
            }
        });

        promptTimeOut.schedule(new TimerTask() {
            @Override
            public void run() {
                snooze.performClick();
            }
        },new Preferences().EoDPrompt_TimeOut);

        setAmbientEnabled();        // Enables the ambient mode on the system.
        setAutoResumeEnabled(true);     // Resumes the main activity.
    }

    @Override
    public void onDestroy()     // When the system is destroyed this is run
    {
        wakeLock.release();     // Kills the wakelock.
        promptTimeOut.cancel(); // Cancels snooze timer
        super.onDestroy();      // Kills the service.
    }
}
