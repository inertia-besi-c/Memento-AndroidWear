package com.linklab.emmanuelogunjirin.besi_c;

// Imports
import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends WearableActivity  // This is the activity that runs on the main screen. This is the main UI
{
    private TextView batteryLevel, date, time;    // This is the variables that shows the battery level, date, and time

    /* This Updates the Date and Time Every second when UI is in the foreground */
    Thread time_updater = new Thread()
    {
        @Override
        public void run()
        {
            try
            {
                while (!time_updater.isInterrupted())
                {
                    Thread.sleep(1000);runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        DateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.US);
                        DateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy", Locale.US);
                        Date current = new Date();
                        time.setText(timeFormat.format(current));
                        date.setText(dateFormat.format(current));
                    }
                });
                }
            }
            catch (InterruptedException e)
            {
                System.out.print("Catch was run");       // Placeholder until the catch is needed to observe some response from the system
            }
        }
    };

    /* Gets the current battery level, date, and time and sets the text field data */
    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver()        // Just a receiver that gets data from the system
    {
        @Override
        public void onReceive(final Context context, Intent intent)
        {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
            int percent = (level*100)/scale;    // Shows the battery level in percentage value
            final String batLevel = "Battery: " + String.valueOf(percent) + "%";        // Appends the battery level
            batteryLevel.setText(batLevel);     // Sets the battery level text view to show the battery level in percentage.
        }
    };

    @Override

    protected void onCreate(Bundle savedInstanceState)
    {
        Intent HeartRateMeasurement = new Intent(getBaseContext(), HeartRateSensorTimer.class );        // This is where the Heart Rate Measurement is initialized.
        startActivity(HeartRateMeasurement);        // This is where it starts.

        Intent AccelerometerMeasurement = new Intent(getBaseContext(), HeartRateSensorTimer.class );        // This is where the Accelerometer Measurement is initialized.
        startActivity(AccelerometerMeasurement);        // This is where it starts.

        super.onCreate(savedInstanceState);      // Creates the main screen.
        setContentView(R.layout.activity_main);     // This is where the texts and buttons seen were made. (Look into: res/layout/activity_main)

        Button EMA_Start = findViewById(R.id.EMA_Start);    // The Start button is made
        Button SLEEP = findViewById(R.id.SLEEP);        // The Sleep button is made

        batteryLevel = findViewById(R.id.BATTERY_LEVEL);    // Battery level ID
        registerReceiver(mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        date = findViewById(R.id.DATE);     // The date ID
        time = findViewById(R.id.TIME);     // The time ID

        time_updater.start();       // The time updater

        // Checks if Device has permission to write to external data (sdcard), if it does not it requests the permission from device
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            // Permission is not granted, Request permissions
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
        // Checks if device has permission to read Body Sensor Data
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BODY_SENSORS) != PackageManager.PERMISSION_GRANTED)
        {
            // Permission is not granted, Request permissions
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BODY_SENSORS}, 0);
        }

        /* Listens for the EMA button "START" to be clicked. */
        EMA_Start.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Intent StartEMAActivity = new Intent(getBaseContext(), EMA.class);      // Links to the EMA File
                startActivity(StartEMAActivity);    // Starts the EMA file
            }
        });

        // Listens for the SLEEP button "SLEEP" to be clicked. (Coming Soon)
        SLEEP.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                // Future spot for a shutdown button for bedtime.
            }
        });

        // Enables Always-on
        setAmbientEnabled();
    }
}
