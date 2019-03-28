package com.linklab.INERTIA.besi_c;

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
import android.os.PowerManager;
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

public class MainActivity extends WearableActivity  // This is the activity that runs on the main screen. This is the main User interface and dominates the start of the app.
{
    private TextView batteryLevel, date, time;    // This is the variables that shows the battery level, date, and time
    private Button SLEEP, SLEEP2;       // This is the sleep button on the screen, along with the other button for aesthetics. 
    private boolean SleepMode = false;      // This is the boolean that runs the sleep cycle.
    private boolean BatteryCharge = false;      // This is the boolean that runs the battery charge cycle.

    @SuppressLint("WakelockTimeout")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);     // Power manager calls the power distribution service.
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MainActivity:wakeLock");
        wakeLock.acquire();      // The screen turns off after the timeout is passed.

        super.onCreate(savedInstanceState);      // Creates the main screen.
        setContentView(R.layout.activity_main);     // This is where the texts and buttons seen were made. (Look into: res/layout/activity_main)
        time_updater.start();       // The time updater
        startSensors();     // This starts all the sensors that are in the method.
        new DataLogger("StepActivity","no").WriteData();        // This is a data logger that logs data to a step activity file.

        Button EMA_Start = findViewById(R.id.EMA_Start);        // This is the Start button
        SLEEP = findViewById(R.id.SLEEP);        // The Sleep button is made
        SLEEP2 = findViewById(R.id.SLEEP2);
        batteryLevel = findViewById(R.id.BATTERY_LEVEL);    // Battery level view ID
        date = findViewById(R.id.DATE);     // The date view ID
        time = findViewById(R.id.TIME);     // The time view ID

        final Intent HRService = new Intent(getBaseContext(), HRTimerService.class);        // Gets an intent for the start of the heartrate sensor.
        if (!isRunning(HRTimerService.class))       // Starts the heart rate timer controller
        {
            startService(HRService);        // That starts the heartrate sensor if it is not already running.
        }

        EMA_Start.setOnClickListener(new View.OnClickListener()     /* Listens for the EMA button "START" to be clicked. */
        {
            public void onClick(View v)     // When the button is clicked the is run
            {
                Intent StartEMAActivity = new Intent(getBaseContext(), PainScreen.class);      // Links to the Pain EMA File
                startActivity(StartEMAActivity);    // Starts the Pain EMA file
            }
        });

        SLEEP.setOnClickListener(new View.OnClickListener()        // Listens for the SLEEP button "SLEEP" to be clicked. (Coming Soon)
        {
            @SuppressLint("SetTextI18n")        // Suppresses some error messages.
            public void onClick(View v)     // When the sleep button is clicked
            {
                if (isRunning(HRTimerService.class))        // If the heart rate timer service is running
                {
                    stopService(HRService);     // It stops the service
                    SLEEP.setBackgroundColor(getResources().getColor(R.color.grey));    // It sets the color of the button to grey
                    SLEEP2.setBackgroundColor(getResources().getColor(R.color.grey));    // It sets the color of the button to grey
                    SLEEP.setText("Sleep");      // It sets the text of the button to sleep
                    SleepMode = true;       // And it sets the boolean value to true.
                }
                else        // If the heart rate timer is not running
                {
                    startService(HRService);        // It starts the heart rate timer service
                    SLEEP.setBackgroundColor(getResources().getColor(R.color.blue));        // It sets the color of the button to blue
                    SLEEP2.setBackgroundColor(getResources().getColor(R.color.blue));        // It sets the color of the button to blue
                    SLEEP.setText("Sleep");     // It sets the text of the button to sleep
                    SleepMode = false;      // It sets the boolean value to false.
                }
            }
        });

        SLEEP.setOnLongClickListener(new View.OnLongClickListener()     // Listens for the sleep button to be clicked
        {
            @Override
            public boolean onLongClick(View v)      // If the sleep button is clicked
            {
                return false;       // It just returns false.
            }
        });

        //setAmbientEnabled();        // Keeps the screen awake.
        setAutoResumeEnabled(true);

        try     // Try doing this to keep up
        {
            registerReceiver(mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));        // Register the battery level broadcaster
        }
        catch(Exception ignored)        // Catch exception
        {
            // Do nothing
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
        public void run()       // When the timer updater is run, it starts the following.
        {
            try     // it tired to run the following
            {
                while (!time_updater.isInterrupted())       // While the timer updater is not interrupted by some other system.
                {
                    Thread.sleep(1000);     // Wait 1 second.
                    runOnUiThread(new Runnable()        // Run this while the user interface is on.
                    {
                        @SuppressLint("SetTextI18n")        // Suppresses some more errors.

                        @Override
                        public void run()       // This is run.
                        {
                            DateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.US);      // The time format is called in US format.
                            DateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy", Locale.US);     // The date is called in US format.
                            Date current = new Date();      // The current date and timer is set.
                            time.setText(timeFormat.format(current));       // The current time is set to show on the time text view.
                            date.setText(dateFormat.format(current));       // The current date is set to show on the date text view.

                            IntentFilter battery = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);     // Starts an intent that calls the battery level service.
                            Intent batteryStatus = getApplicationContext().registerReceiver(null, battery);     // This gets the battery status from that service.

                            assert batteryStatus != null;       // Asserts that the battery level is not null.
                            int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);      // Initializes an integer value for the battery level
                            int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);      // Scales the battery level to 100 from whatever default value it is.
                            int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);        //  Gets extra data from the battery level service.
                            int batteryPct = (level*100/scale);     // Sets the battery level as a percentage.

                            // Checks if the battery is currently charging.
                            boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL || status == BatteryManager.BATTERY_PLUGGED_AC;

                            batteryLevel.setText("Battery: " + String.valueOf(batteryPct) + "%");       // Sets the text view for the battery to show the battery level.
                            DataLogger stepActivity = new DataLogger("StepActivity","no");      // Logs step data to the file.

                            if (isCharging)     // If the battery is charging
                            {
                                if (!BatteryCharge || !SleepMode)       // If the battery is not charging and it is not in sleep mode
                                {
                                    if (!SleepMode)     // If it is not in sleep mode
                                    {
                                        SLEEP.performClick();       // Perform a coded click on the sleep button
                                        stopSensors();      // Call the stop sensors method.
                                        LogActivityCharge();        // Call the charging method to start logging.
                                    }

                                    BatteryCharge = true;       // Ser the battery charge status to true.
                                }
                            }
                            else        // If the watch is not charging.
                            {
                                startSensors();     // Start all the sensors.
                                BatteryCharge = false;      // Set the battery charge boolean to false.
                            }

                            if (SleepMode)      // If it is in sleep mode
                            {
                                if(stepActivity.ReadData().contains("yes"))     // And there are steps going on.
                                {
                                    SLEEP.performClick();       // Perform a coded click on the sleep button
                                    stepActivity.WriteData();   // Write the step activity data to the file.
                                }
                                stepActivity.WriteData();       // Else just keep writing tho the file.
                            }
                            else        // If it is not in sleep mode.
                            {
                                stepActivity.WriteData();       // Keep writing the data.
                            }
                        }
                    });
                }
            }
            catch (InterruptedException e)      // A catch for if it fails.
            {
                // Do nothing.
            }
        }
    };

    private void startSensors()     // Starts the sensors from their service branches
    {
        startAccelerometerSensor();     // Calls the accelerometer method to start it's intent (Stopped for now..Running a test)
//        startHeartRateSensor();     // Calls the heart rate method to start it's intent       (Needs to be called independently in onCreate)
        startPedometerSensor();     // Calls the pedometer method to start it's intent
    }

    private void stopSensors()     // Stops the sensors from their service branches
    {
//        stopAccelerometerSensor();     // Calls the accelerometer method to stop it's intent
//        stopHeartRateSensor();     // Calls the heart rate method to stop it's intent
        stopPedometerSensor();     // Calls the pedometer method to stop it's intent
    }

//    private void startHeartRateSensor()     // Starts the heart rate sensor
//    {
//        final Intent HRService = new Intent(getBaseContext(), HRTimerService.class);        // Creates an intent for calling the heart rate timer service.
//        if (!isRunning(HRTimerService.class))       // If the heart rate timer service is not running
//        {
//            startService(HRService);        // Starts the service.
//        }
//    }

//    private void stopHeartRateSensor()     // Stops the heart rate sensor
//    {
//        final Intent HRService = new Intent(getBaseContext(), HRTimerService.class);        // Creates an intent for calling the heart rate timer service.
//        if (isRunning(HRTimerService.class))       // If the heart rate timer service is running
//        {
//            stopService(HRService);        // Stops the service.
//        }
//    }

    private void startPedometerSensor()     // Starts the pedometer sensor
    {
        final Intent PedomService = new Intent(getBaseContext(), PedometerSensor.class);        // Creates an intent for calling the pedometer service.
        if(!isRunning(PedometerSensor.class))       // If the pedometer service is not running
        {
            startService(PedomService);        // Starts the service.
        }
    }

    private void stopPedometerSensor()     // Stops the pedometer sensor
    {
        final Intent PedomService = new Intent(getBaseContext(), PedometerSensor.class);       // Creates an intent for calling the pedometer service.
        if(isRunning(PedometerSensor.class))       // If the pedometer service is running
        {
            stopService(PedomService);        // Stops the service.
        }
    }

    private void startAccelerometerSensor()     // Starts the accelerometer sensor
    {
        final Intent AccelService = new Intent(getBaseContext(), AccelerometerSensor.class);        // Creates an intent for calling the accelerometer service.
        if(!isRunning(AccelerometerSensor.class))       // If the accelerometer service is not running
        {
            startService(AccelService);        // Starts the service.
        }
    }

//    private void stopAccelerometerSensor()     // Stops the accelerometer sensor
//    {
//        final Intent AccelService = new Intent(getBaseContext(), AccelerometerSensor.class);       // Creates an intent for calling the accelerometer service.
//        if(isRunning(AccelerometerSensor.class))       // If the accelerometer service is running
//        {
//            stopService(AccelService);        // Stops the service.
//        }
//    }

    private void LogActivityCharge()        // Logs the times when the battery is charging.
    {
        String data =  ("Charging at " + new SystemInformation().getTime());       // This is the format it is logged at.
        DataLogger datalog = new DataLogger("Charging_Time.csv",data);      // Logs it into a file called Charging time.
        datalog.LogData();      // Saves the data into the directory.
    }

    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver()    /* Gets the current battery level, date, and time and sets the text field data */
    {
        @Override
        public void onReceive(final Context context, Intent intent)     // Receives the broadcast.
        {
            // This is just a receiver.
        }
    };

    private boolean isRunning(Class<?> serviceClass)        // A general file that checks if a system is running.
    {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);     // Starts the activity manager to check the service called.
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))        // For each service called by the running service.
        {
            if (serviceClass.getName().equals(service.service.getClassName()))      // It checks if it is running.
            {
                return true;        // Returns true
            }
        }
        return false;       // If not, it returns false.
    }

    @Override
    public void onEnterAmbient (Bundle ambientDetails)
    {

        super.onEnterAmbient(ambientDetails);
    }

    @Override
    protected void onStop()     // To stop the activity.
    {
        try     // It tries to.
        {
            unregisterReceiver(mBatInfoReceiver);       // It unregisters the battery level listener.
        }
        catch(Exception ignored)        // A catch exception.
        {
            // Do nothing.
        }
        super.onStop();     // It stops the activity.
    }
}
