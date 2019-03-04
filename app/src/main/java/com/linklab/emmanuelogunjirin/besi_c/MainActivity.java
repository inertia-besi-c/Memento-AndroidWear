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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends WearableActivity {

    private Button Ema, Sleep;// This is the list of buttons
    private TextView batteryLevel,date,time;


    // Updates the time Every second when UI is in front
    Thread time_updater = new Thread() {
        @Override
        public void run() {
            try { while (!time_updater.isInterrupted()) { Thread.sleep(1000);runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    DateFormat timeFormat = new SimpleDateFormat("H:mm a");
                    DateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy");
                    Date now = new Date();
                    time.setText(timeFormat.format(now));
                    date.setText(dateFormat.format(now));
                }
            });
            }
            } catch (InterruptedException e) {
            }
        }};

    // Gets the current battery level and sets the Text of the Battery Indicator
    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
            int percent = (level*100)/scale;

            final String batLevel = "Battery: " + String.valueOf(percent) + "%";

            batteryLevel.setText(batLevel);
        }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Ema = (Button) findViewById(R.id.EMA);
        Sleep = (Button) findViewById(R.id.SLEEP);

        batteryLevel = (TextView) findViewById(R.id.BATTERY_LEVEL);

        date = (TextView) findViewById(R.id.DATE);
        time = (TextView) findViewById(R.id.TIME);

        time_updater.start();

        registerReceiver(mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        // Checks if Device has permission to write to external data (sdcard), if it does not
        // it requests the permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    0);
        }

        Ema.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), ema.class);
                startActivity(i);
                //Toast.makeText(this, "Button Clicked", Toast.LENGTH_LONG).show();

            }
        });

        // Enables Always-on
        setAmbientEnabled();
    }

}
