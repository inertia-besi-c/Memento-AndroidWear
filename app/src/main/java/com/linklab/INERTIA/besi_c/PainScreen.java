package com.linklab.INERTIA.besi_c;

// Imports
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
                String data =  ("Pain Screen 'Pain' Button Tapped at " + new SystemInformation().getTimeStamp());       // This is the format it is logged at.
                DataLogger datalog = new DataLogger("System_Activity.csv",data);      // Logs it into a file called System Activity.
                datalog.LogData();      // Saves the data into the directory.

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
                String data =  ("Pain Screen 'Cancel' Button Tapped at " + new SystemInformation().getTimeStamp());       // This is the format it is logged at.
                DataLogger datalog = new DataLogger("System_Activity.csv",data);      // Logs it into a file called System Activity.
                datalog.LogData();      // Saves the data into the directory.

                finish();       // Finishes the screen and moves back to the Main-Activity.
            }
        });

        setAmbientEnabled();        // Allows the screen to come on.
    }
}
