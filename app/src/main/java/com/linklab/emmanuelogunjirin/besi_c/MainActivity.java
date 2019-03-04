package com.linklab.emmanuelogunjirin.besi_c;

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

public class MainActivity extends WearableActivity
{
    private TextView batteryLevel, date, time;    // This is what shows the battery level, date, and time

    // Updates the time Every second when UI is in front
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
                        DateFormat timeFormat = new SimpleDateFormat("H:mm a", Locale.US);
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
                System.out.print("Catch was run");       // Placeholder until the catch is needed to observe some response
            }
        }
    };

    // Gets the current battery level, date, and time and sets the text field data
    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(final Context context, Intent intent)
        {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
            int percent = (level*100)/scale;    // Shows the battery level in percentage value
            final String batLevel = "Battery: " + String.valueOf(percent) + "%";

            batteryLevel.setText(batLevel);
        }
    };

    @Override

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button EMA_Start = findViewById(R.id.EMA_Start);
        Button SLEEP = findViewById(R.id.SLEEP);

        batteryLevel = findViewById(R.id.BATTERY_LEVEL);
        registerReceiver(mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        date = findViewById(R.id.DATE);
        time = findViewById(R.id.TIME);

        time_updater.start();

        // Checks if Device has permission to write to external data (sdcard), if it does not it requests the permission from device
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            // Permission is not granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }

        // Listens for the EMA button "START" to be clicked.
        EMA_Start.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Intent i = new Intent(getBaseContext(), EMA.class);
                startActivity(i);
                // Toast.makeText(this, "Button Clicked", Toast.LENGTH_LONG).show();
            }
        });

        // Listens for the SLEEP button "SLEEP" to be clicked. (Coming Soon)
        SLEEP.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
//                Intent i = new Intent(getBaseContext(), SLEEP.class );
//                startActivity(i);
            }
        });

        // Enables Always-on
        setAmbientEnabled();
    }
}
