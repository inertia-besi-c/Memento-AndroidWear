package com.linklab.emmanuelogunjirin.besi_c;

// Imports
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends WearableActivity  // This is the activity that runs on the main screen. This is the main UI
{
    private TextView batteryLevel, date, time;    // This is the variables that shows the battery level, date, and time
    private Button SLEEP;
    private boolean SleepMode = false;
    private boolean BatteryCharge = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);      // Creates the main screen.
        setContentView(R.layout.activity_main);     // This is where the texts and buttons seen were made. (Look into: res/layout/activity_main)
        time_updater.start();       // The time updater
        new DataLogger("StepActivity","no").WriteData();

        Button EMA_Start = findViewById(R.id.EMA_Start);
        SLEEP = findViewById(R.id.SLEEP);        // The Sleep button is made
        batteryLevel = findViewById(R.id.BATTERY_LEVEL);    // Battery level ID
        date = findViewById(R.id.DATE);     // The date ID
        time = findViewById(R.id.TIME);     // The time ID

        final Intent HRService = new Intent(getBaseContext(), HRTimerService.class);
        if (!isRunning(HRTimerService.class))
        {
            startService(HRService);
        }

        /* Listens for the EMA button "START" to be clicked. */
        EMA_Start.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Intent StartEMAActivity = new Intent(getBaseContext(), PainScreen.class);      // Links to the EMA File
                startActivity(StartEMAActivity);    // Starts the EMA file
            }
        });

        SLEEP.setOnClickListener(new View.OnClickListener()        // Listens for the SLEEP button "SLEEP" to be clicked. (Coming Soon)
        {
            @SuppressLint("SetTextI18n")
            public void onClick(View v)
            {
                //Log.i("Main","Sleep Clicked");
                if (isRunning(HRTimerService.class))
                {
                    //Log.i("Main","HRS is running. stopping it");
                    stopService(HRService);
                    SLEEP.setBackgroundColor(getResources().getColor(R.color.grey));
                    SLEEP.setText("Wake ");
                    SleepMode = true;
                }
                else
                {
                    //Log.i("Main","HRS is not running, starting it");
                    startService(HRService);
                    SLEEP.setBackgroundColor(getResources().getColor(R.color.blue));
                    SLEEP.setText("Sleep");
                    SleepMode = false;
                }
            }
        });

        SLEEP.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                return false;
            }
        });
        setAmbientEnabled();

        try
        {
            registerReceiver(mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        }
        catch(Exception ignored)
        {
            // Ignore this catch.
        }

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
    }

    Thread time_updater = new Thread()    /* This Updates the Date and Time Every second when UI is in the foreground */
    {
        @Override
        public void run()
        {
            try
            {
                while (!time_updater.isInterrupted())
                {
                    Thread.sleep(1000);
                    runOnUiThread(new Runnable()
                    {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void run()
                        {
                            DateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.US);
                            DateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy", Locale.US);
                            Date current = new Date();
                            time.setText(timeFormat.format(current));
                            date.setText(dateFormat.format(current));

                            IntentFilter battery = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                            Intent batteryStatus = getApplicationContext().registerReceiver(null, battery);

                            assert batteryStatus != null;
                            int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                            int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                            int batteryPct = (level*100/scale);

                            int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
                            boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;

                            batteryLevel.setText("Battery: " + String.valueOf(batteryPct) + "%");

                            if (isCharging)
                            {
                                if (!BatteryCharge || !SleepMode)
                                {
                                    if (!SleepMode)
                                    {
                                        SLEEP.performClick();
                                        stopSensors();
                                        LogActivityCharge();
                                    }

                                    BatteryCharge = true;
                                }
                            }
                            else
                            {
                                startSensors();
                                BatteryCharge = false;
                            }

                            DataLogger stepActivity = new DataLogger("StepActivity","no");
                            Log.i("Step","Is Ped Running:" + isRunning(PedometerSensor.class) + " What does StepActivity Say? " + stepActivity.ReadData());

                            if (SleepMode)
                            {
                                if(stepActivity.ReadData().contains("yes"))
                                {
                                    SLEEP.performClick();
                                    stepActivity.WriteData();
                                }
                                stepActivity.WriteData();
                            }
                            else
                            {
                                stepActivity.WriteData();
                            }
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

    private void startSensors()     // Calls the sensors from their service branches
    {
        final Intent AccelService = new Intent(getBaseContext(), AccelerometerSensor.class);    // Calls Accelerometer
        if(!isRunning(AccelerometerSensor.class))
        {
            startService(AccelService);
        }

        final Intent PedomService = new Intent(getBaseContext(), PedometerSensor.class);    // Calls Pedometer
        if(!isRunning(PedometerSensor.class))
        {
            startService(PedomService);
        }
    }

    private void stopSensors()     // Calls the sensors from their service branches
    {
        final Intent AccelService = new Intent(getBaseContext(), AccelerometerSensor.class);    // Calls Accelerometer
        if(isRunning(AccelerometerSensor.class))
        {
            stopService(AccelService);
        }

        final Intent PedomService = new Intent(getBaseContext(), PedometerSensor.class);    // Calls Pedometer
        if(isRunning(PedometerSensor.class))
        {
            stopService(PedomService);
        }

        final Intent HRService = new Intent(getBaseContext(), HRTimerService.class);
        if (isRunning(HRTimerService.class))
        {
            stopService(HRService);
        }
    }

    private void LogActivityCharge()
    {
        String data =  ("Charging at " + new SystemTime().getTime());
        DataLogger datalog = new DataLogger("Charging_Time.csv",data);
        datalog.LogData();
    }

    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver()    /* Gets the current battery level, date, and time and sets the text field data */
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

    private boolean isRunning(Class<?> serviceClass)
    {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
        {
            if (serviceClass.getName().equals(service.service.getClassName()))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onStop()
    {
        try
        {
            unregisterReceiver(mBatInfoReceiver);
        }
        catch(Exception ignored)
        {
            // Ignored
        }
        super.onStop();
    }
}
