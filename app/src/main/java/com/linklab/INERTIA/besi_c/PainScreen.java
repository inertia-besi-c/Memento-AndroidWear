package com.linklab.INERTIA.besi_c;

// Imports
import android.app.ActivityManager;
import android.content.Context;
import android.os.Vibrator;
import android.support.wearable.activity.WearableActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class PainScreen extends WearableActivity        // This is the screen in-between the start screen and the Pain EMA.
{
    public Vibrator v;      // The vibrator service from the system.

    @Override
    protected void onCreate(Bundle savedInstanceState)      // When service is called, this is created first.
    {
        super.onCreate(savedInstanceState);     // It calls the saved instance from the res files.
        setContentView(R.layout.activity_pain_screen);      // It starts the pain screen that was made.

        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);      // It calls the vibrator service
        v.vibrate(300);     // It vibrates for 300 milliseconds.

        Button pain = findViewById(R.id.Pain);      // Assigns a variable to the Pain button
        Button cancel = findViewById(R.id.Cancel);      // Assigns a variable to the Cancel button.

        pain.setOnClickListener(new View.OnClickListener()      // Waits until the pain button is clicked.
        {
            @Override
            public void onClick(View v)     // When it is clicked, it runs these codes.
            {
                Intent StartPainEMA = new Intent(getBaseContext(), PainEMA.class);      // Links to the Pain-EMA Service.
                startActivity(StartPainEMA);    // Starts the Pain-EMA service.
                finish();       // Finishes the screen and moves on to the Pain-EMA Service.
            }
        });

        cancel.setOnClickListener(new View.OnClickListener()        // Waits until the cancel button is clicked.
        {
            @Override
            public void onClick(View v)     // When it is clicked, it runs these codes.
            {
                finish();       // Finishes the screen and moves back to the Main-Activity.
            }
        });

        setAmbientEnabled();        // Allows the screen to come on.
    }

    private boolean isRunning()        // A general file that checks if a system is running.
    {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);     // Starts the activity manager to check the service called.
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))        // For each service called by the running service.
        {
            if (PainScreen.class.getName().equals(service.service.getClassName()))      // It checks if it is running.
            {
                return true;        // Returns true
            }
        }
        return false;       // If not, it returns false.
    }
}
