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

public class EndOfDayPrompt2 extends WearableActivity       // This is the EOD EMA that is run for the third and last time.
{
    private PowerManager.WakeLock wakeLock;     // Starts the power manager and the wakelock from the system.
    private Timer promptTimeOut = new Timer();

    @SuppressLint({"WakelockTimeout", "SetTextI18n"})       // Suppresses the timeouts.

    @Override
    protected void onCreate(Bundle savedInstanceState)      // When the service is created it runs this
    {
        super.onCreate(savedInstanceState);     // Starts a saved instance in the system.
        setContentView(R.layout.activity_end_of_day_prompt);        // Gets the EOD EMA prompt activity from the res files.

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);     // Gets the wakelock from the system
        wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "EOD Prompt 3:wakeLock");     // The system is started with a full wakelock.
        wakeLock.acquire();     // Keeps the wakelock from a timeout.

        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(600);     // Vibrates for the specified amount of time in milliseconds.

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
                String data =  ("Third End of Day EMA Prompt 'Proceed' Button Tapped at " + new SystemInformation().getTimeStamp());       // This is the format it is logged at.
                DataLogger datalog = new DataLogger("System_Activity.csv",data);      // Logs it into a file called System Activity.
                datalog.LogData();      // Saves the data into the directory.

                String data1 =  ("End of Day prompt 3 started End of Day EMA at " + new SystemInformation().getTimeStamp());       // This is the format it is logged at.
                DataLogger datalog1 = new DataLogger("Sensor_Activity.csv",data1);      // Logs it into a file called System Activity.
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
                String data =  ("Third End of Day EMA Prompt 'Dismiss' Button Tapped at " + new SystemInformation().getTimeStamp());       // This is the format it is logged at.
                DataLogger datalog = new DataLogger("System_Activity.csv",data);      // Logs it into a file called System Activity.
                datalog.LogData();      // Saves the data into the directory.

                String data1 =  ("End of Day prompt 3 Dismissed End of Day EMA at " + new SystemInformation().getTimeStamp());       // This is the format it is logged at.
                DataLogger datalog1 = new DataLogger("Sensor_Activity.csv",data1);      // Logs it into a file called System Activity.
                datalog1.LogData();      // Saves the data into the directory.

                finish();       // Finish and end the service.
            }
        });

        promptTimeOut.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                String data =  ("End of Day prompt 3 dismissed End of Day EMA at " + new SystemInformation().getTimeStamp());       // This is the format it is logged at.
                DataLogger datalog = new DataLogger("Sensor_Activity.csv",data);      // Logs it into a file called System Activity.
                datalog.LogData();      // Saves the data into the directory.

                snooze.performClick();
            }
        },new Preferences().EoDPrompt_TimeOut);

        setAmbientEnabled();        // Makes the system ambient.
        setAutoResumeEnabled(true);     // Resumes the main activity.
    }

    @Override
    public void onDestroy()     // When the system is destroyed.
    {
        wakeLock.release();     // The wakelock is released.
        promptTimeOut.cancel(); // Cancels dismiss timer
        super.onDestroy();      // The service is killed.
    }
}
