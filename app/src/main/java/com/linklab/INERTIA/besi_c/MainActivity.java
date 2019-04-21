package com.linklab.INERTIA.besi_c;

// Imports

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

/* ************************************************************************************* MAIN ACTIVITY OF THE APP ************************************************************************************************** */

public class MainActivity extends WearableActivity  // This is the activity that runs on the main screen. This is the main User interface and dominates the start of the app.
{
    private TextView batteryLevel, date, time;    // This is the variables that shows the battery level, date, and time
    private Button SLEEP, EMA_Start, EOD_EMA_Start, Daily_Survey;       // This is the sleep button
    private Preferences Preference = new Preferences();     // Gets an instance from the preferences module.
    private SystemInformation SystemInformation = new SystemInformation();  // Gets an instance from the system information module
    private String Sensors = Preference.Sensors;     // Gets the sensors from preferences.
    private String Battery = Preference.Battery;     // Gets the sensors from preferences.
    private String System = Preference.System;     // Gets the sensors from preferences.
    private String Estimote = Preference.Estimote;       // Gets the Estimote from preferences.
    private String Pedometer = Preference.Pedometer;       // Gets the Estimote from preferences.
    private int HapticFeedback = Preference.HapticFeedback;      // This is the haptic feedback for button presses.
    private boolean SleepMode = false;      // This is the boolean that runs the sleep cycle.
    private boolean BatteryCharge = false;      // This is the boolean that runs the battery charge cycle.
    private boolean isCharging;     // Boolean value that keeps track of if the watch is charging or not.
    private Vibrator vibrator;      // The vibrator that provides haptic feedback.

    @SuppressLint("WakelockTimeout")        // Suppresses errors.
    @Override
    protected void onCreate(Bundle savedInstanceState)      // This is created on startup
    {
        /* ***************************************************** PERMISSIONS NEED TO BE CHECKED AND REQUESTED BEFORE STARTING ANY SERVICES OR DRAWING UI ****************************************************** */
        String[] Required_Permissions =     // Checks if Device has permission to work on device.
                {
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,     // This is to access the storage
                        Manifest.permission.BODY_SENSORS,       // This is to access the sensors of the device
                        Manifest.permission.ACCESS_WIFI_STATE,      // This is to access the wifi of the device.
                        Manifest.permission.CHANGE_WIFI_STATE,      // This is to change the wifi state of the device.
                        Manifest.permission.ACCESS_NETWORK_STATE,       // This is to access the network
                        Manifest.permission.CHANGE_NETWORK_STATE,        // This is to change the network setting of the device.
                        Manifest.permission.ACCESS_COARSE_LOCATION,     // This is to access the location in a general sense
                        Manifest.permission.ACCESS_FINE_LOCATION,       // This is to access the location in a more specific manner
                        Manifest.permission.BLUETOOTH,      // This is to access th bluetooth
                        Manifest.permission.BLUETOOTH_ADMIN     // This is access the bluetooth and allow changes
                };

        boolean needPermissions = false;        // To begin the permission is set to false.

        for (String permission : Required_Permissions)     // For each of the permission listed above.
        {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED)       // Check if they have permission to work on the device.
            {
                needPermissions = true;     // if they do, grant them permission
            }
        }

        if (needPermissions)        // When they have permission
        {
            ActivityCompat.requestPermissions(this, Required_Permissions,0);     // Allow them to work on device.
        }

        /*  -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------  */
        /*  -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------  */
        /*  -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------  */
        /*  -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------  */
        /*  -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------  */

        super.onCreate(savedInstanceState);      // Creates the main screen.
        setContentView(R.layout.activity_main);     // This is where the texts and buttons seen were made. (Look into: res/layout/activity_main)

        Main_Timer.start();       // The time updater
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);          /* Vibrator values and their corresponding requirements */

        EOD_EMA_Start = findViewById(R.id.EOD_EMA_Start);        // This is the first ema button that is mainly used by the system
        EMA_Start = findViewById(R.id.EMA_Start);      // This is the second ema button that is used
        Daily_Survey = findViewById(R.id.DAILY_SURVEY);       // This is the end of day EMA button
        SLEEP = findViewById(R.id.SLEEP);        // The sleep button is made
        batteryLevel = findViewById(R.id.BATTERY_LEVEL);    // Battery level view ID
        date = findViewById(R.id.DATE);     // The date view ID
        time = findViewById(R.id.TIME);     // The time view ID

        File battery = new File(Preference.Directory + SystemInformation.Battery_Path);     // Gets the path to the Sensors from the system.
        if (battery.exists())      // If the file exists
        {
            Log.i("Main Activity", "No Header Created");     // Logs to console
        }
        else        // If the file does not exist
        {
            Log.i("Main Activity", "Creating Header");     // Logs on Console.

            DataLogger dataLogger = new DataLogger(Battery, Preference.Battery_Data_Headers);        /* Logs the Sensors data in a csv format */
            dataLogger.LogData();       // Saves the data to the directory.
        }

        File estimote = new File(Preference.Directory + SystemInformation.Estimote_Path);     // Gets the path to the Sensors from the system.
        if (estimote.exists())      // If the file exists
        {
            Log.i("Estimote Sensor", "No Header Created");     // Logs to console
        }
        else        // If the file does not exist
        {
            Log.i("Estimote Sensor", "Creating Header");     // Logs on Console.

            DataLogger dataLogger = new DataLogger(Estimote, Preference.Estimote_Data_Headers);        /* Logs the Sensors data in a csv format */
            dataLogger.LogData();       // Saves the data to the directory.
        }

        File pedometer = new File(Preference.Directory + SystemInformation.Pedometer_Path);     // Gets the path to the Pedometer from the system.
        if (pedometer.exists())      // If the file exists
        {
            Log.i("Pedometer Sensor", "No Header Created");     // Logs to console
        }
        else        // If the file does not exist
        {
            Log.i("Pedometer Sensor", "Creating Header");     // Logs on Console.

            DataLogger dataLogger = new DataLogger(Pedometer, Preference.Pedometer_Data_Headers);        /* Logs the Pedometer data in a csv format */
            dataLogger.LogData();       // Saves the data to the directory.
        }

        final Intent HRService = new Intent(getBaseContext(), HRTimerService.class);        // Gets an intent for the start of the heartrate sensor.
        if (!isRunning(HRTimerService.class))       // Starts the heart rate timer controller
        {
            String data =  ("Main Activity," + "Started Heart Rate Timer at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
            DataLogger datalog = new DataLogger(Sensors, data);      // Logs it into a file called System Activity.
            datalog.LogData();      // Saves the data into the directory.

            startService(HRService);        // That starts the heartrate sensor if it is not already running.
        }

        final Intent AccelService = new Intent(getBaseContext(), AccelerometerSensor.class);        // Creates an intent for calling the accelerometer service.
        if(!isRunning(AccelerometerSensor.class))       // If the accelerometer service is not running
        {
            String data =  ("Main Activity," + "Started Accelerometer Sensor at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
            DataLogger datalog = new DataLogger(Sensors, data);      // Logs it into a file called System Activity.
            datalog.LogData();      // Saves the data into the directory.

            startService(AccelService);        // Starts the service.
        }

        final Intent PedomService = new Intent(getBaseContext(), PedometerSensor.class);        // Creates an intent for calling the pedometer service.
        if(!isRunning(PedometerSensor.class))       // If the pedometer service is not running
        {
            String data =  ("Main Activity," + "Started Pedometer Sensor at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
            DataLogger datalog = new DataLogger(Sensors, data);      // Logs it into a file called System Activity.
            datalog.LogData();      // Saves the data into the directory.

            startService(PedomService);        // Starts the service.
        }

        final Intent EstimService = new Intent(getBaseContext(), ESTimerService.class);        // Creates an intent for calling the Estimote Timer service.
        final Intent EstimoteService = new Intent(getBaseContext(), EstimoteService.class);        // Creates an intent for calling the Estimote service.
        if(!isRunning(ESTimerService.class))       // If the Estimote Timer service is not running
        {
            String data =  ("Main Activity," + "Started Estimote Timer at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
            DataLogger datalog = new DataLogger(Sensors, data);      // Logs it into a file called System Activity.
            datalog.LogData();      // Saves the data into the directory.

            startService(EstimService);        // Starts the service.
        }


        EMA_Start.setOnClickListener(new View.OnClickListener()     /* Listens for the EMA button "START" to be clicked. */
        {
            public void onClick(View v)     // When the button is clicked the is run
            {
                File system = new File(Preference.Directory + SystemInformation.System_Path);     // Gets the path to the system from the system.
                if (system.exists())      // If the file exists
                {
                    Log.i("Main Activity", "No Header Created");     // Logs to console
                }
                else        // If the file does not exist
                {
                    Log.i("Main Activity", "Creating Header");     // Logs on Console.

                    DataLogger dataLogger = new DataLogger(System, Preference.System_Data_Headers);        /* Logs the system data in a csv format */
                    dataLogger.LogData();       // Saves the data to the directory.
                }

                File sensors = new File(Preference.Directory + SystemInformation.Sensors_Path);     // Gets the path to the Sensors from the system.
                if (sensors.exists())      // If the file exists
                {
                    Log.i("Main Activity", "No Header Created");     // Logs to console
                }
                else        // If the file does not exist
                {
                    Log.i("Main Activity", "Creating Header");     // Logs on Console.

                    DataLogger dataLogger = new DataLogger(Sensors, Preference.Sensor_Data_Headers);        /* Logs the Sensors data in a csv format */
                    dataLogger.LogData();       // Saves the data to the directory.
                }

                vibrator.vibrate(HapticFeedback);      // A slight haptic feedback is provided.

                String data =  ("Main Activity," + "'Start' Button Tapped at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
                DataLogger datalog = new DataLogger(System, data);      // Logs it into a file called System Activity.
                datalog.LogData();      // Saves the data into the directory.

                Intent StartEMAActivity = new Intent(getBaseContext(), PainEMA.class);      // Links to the Pain EMA File
                startActivity(StartEMAActivity);    // Starts the Pain EMA file
            }
        });

        EOD_EMA_Start.setOnClickListener(new View.OnClickListener()     /* Listens for the EMA button "START" to be clicked. */
        {
            public void onClick(View v)     // When the button is clicked the is run
            {
                File system = new File(Preference.Directory + SystemInformation.System_Path);     // Gets the path to the system from the system.
                if (system.exists())      // If the file exists
                {
                    Log.i("Main Activity", "No Header Created");     // Logs to console
                }
                else        // If the file does not exist
                {
                    Log.i("Main Activity", "Creating Header");     // Logs on Console.

                    DataLogger dataLogger = new DataLogger(System, Preference.System_Data_Headers);        /* Logs the system data in a csv format */
                    dataLogger.LogData();       // Saves the data to the directory.
                }

                File sensors = new File(Preference.Directory + SystemInformation.Sensors_Path);     // Gets the path to the Sensors from the system.
                if (sensors.exists())      // If the file exists
                {
                    Log.i("Main Activity", "No Header Created");     // Logs to console
                }
                else        // If the file does not exist
                {
                    Log.i("Main Activity", "Creating Header");     // Logs on Console.

                    DataLogger dataLogger = new DataLogger(Sensors, Preference.Sensor_Data_Headers);        /* Logs the Sensors data in a csv format */
                    dataLogger.LogData();       // Saves the data to the directory.
                }

                vibrator.vibrate(HapticFeedback);      // A slight haptic feedback is provided.

                String data =  ("Main Activity," + "'Start' Button Tapped at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
                DataLogger datalog = new DataLogger(System, data);      // Logs it into a file called System Activity.
                datalog.LogData();      // Saves the data into the directory.

                Intent StartEMAActivity = new Intent(getBaseContext(), PainEMA.class);      // Links to the Pain EMA File
                startActivity(StartEMAActivity);    // Starts the Pain EMA file
            }
        });

        SLEEP.setOnClickListener(new View.OnClickListener()        // Listens for the SLEEP button "SLEEP" to be clicked.
        {
            @SuppressLint("SetTextI18n")        // Suppresses some error messages.
            public void onClick(View v)     // When the sleep button is clicked
            {
                File system = new File(Preference.Directory + SystemInformation.System_Path);     // Gets the path to the system from the system.
                if (system.exists())      // If the file exists
                {
                    Log.i("Main Activity", "No Header Created");     // Logs to console
                }
                else        // If the file does not exist
                {
                    Log.i("Main Activity", "Creating Header");     // Logs on Console.

                    DataLogger dataLogger = new DataLogger(System, Preference.System_Data_Headers);        /* Logs the system data in a csv format */
                    dataLogger.LogData();       // Saves the data to the directory.
                }

                File sensors = new File(Preference.Directory + SystemInformation.Sensors_Path);     // Gets the path to the Sensors from the system.
                if (sensors.exists())      // If the file exists
                {
                    Log.i("Main Activity", "No Header Created");     // Logs to console
                }
                else        // If the file does not exist
                {
                    Log.i("Main Activity", "Creating Header");     // Logs on Console.

                    DataLogger dataLogger = new DataLogger(Sensors, Preference.Sensor_Data_Headers);        /* Logs the Sensors data in a csv format */
                    dataLogger.LogData();       // Saves the data to the directory.
                }

                vibrator.vibrate(HapticFeedback);      // A slight haptic feedback is provided.

                String data =  ("Main Activity," + "'Sleep' Button Tapped at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
                DataLogger datalog = new DataLogger(System, data);      // Logs it into a file called System Activity.
                datalog.LogData();      // Saves the data into the directory.

                if (isCharging)     // Checks if the watch is charging
                {
                    if (isRunning(HRTimerService.class))        // If the heart rate timer service is running
                    {
                        String dataHR =  ("Sleep Button," + "Stopped Heart Rate Sensor while charging at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
                        DataLogger datalogHR = new DataLogger(Sensors, dataHR);      // Logs it into a file called System Activity.
                        datalogHR.LogData();      // Saves the data into the directory.

                        stopService(HRService);     // It stops the service
                        SLEEP.setBackgroundColor(getResources().getColor(R.color.grey));    // It sets the color of the button to grey
                        SLEEP.setText("Sleep");      // It sets the text of the button to sleep
                        SleepMode = true;       // And it sets the boolean value to true.
                    }

                    if(isRunning(AccelerometerSensor.class))       // If the accelerometer service is running
                    {
                        String dataA =  ("Sleep Button," + "Stopped Accelerometer Sensor while charging at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
                        DataLogger datalogA = new DataLogger(Sensors, dataA);      // Logs it into a file called System Activity.
                        datalogA.LogData();      // Saves the data into the directory.

                        stopService(AccelService);        // Stop the service.
                    }

                    if(isRunning(ESTimerService.class) || isRunning(EstimoteService.class))       // If the Estimote service is running
                    {
                        String dataB =  ("Sleep Button," + "Stopped Estimote Sensor while charging at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
                        DataLogger datalogB = new DataLogger(Sensors, dataB);      // Logs it into a file called System Activity.
                        datalogB.LogData();      // Saves the data into the directory.

                        stopService(EstimService);        // Stop the service.
                        stopService(EstimoteService);       // Stops the service.
                    }

                    Intent upload = new Intent(getApplicationContext(), FireBase_Upload.class);      // Makes an intent of the system
                    if(!isRunning(FireBase_Upload.class))       // Checks if it is already running
                    {
                        startActivity(upload);      // If not, start it.
                    }

                    Charging();     // Calls the charging method to inform the person
                }

                else        // If the watch is not charging
                {
                    if (isRunning(HRTimerService.class))        // If the heart rate timer service is running
                    {
                        String dataHR =  ("Sleep Button," + "Stopped Heart Rate Sensor while NOT charging at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
                        DataLogger datalogHR = new DataLogger(Sensors, dataHR);      // Logs it into a file called System Activity.
                        datalogHR.LogData();      // Saves the data into the directory.

                        stopService(HRService);     // It stops the service
                        SLEEP.setBackgroundColor(getResources().getColor(R.color.grey));    // It sets the color of the button to grey
                        SLEEP.setText("Sleep");      // It sets the text of the button to sleep
                        SleepMode = true;       // And it sets the boolean value to true.
                    }

                    else        // If the heart rate timer is not running
                    {
                        String dataHR =  ("Sleep Button," + "Started Heart Rate Sensor while NOT charging at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
                        DataLogger datalogHR = new DataLogger(Sensors, dataHR);      // Logs it into a file called System Activity.
                        datalogHR.LogData();      // Saves the data into the directory.

                        startService(HRService);        // It starts the heart rate timer service
                        SLEEP.setBackgroundColor(getResources().getColor(R.color.blue));        // It sets the color of the button to blue
                        SLEEP.setText("Sleep");     // It sets the text of the button to sleep
                        SleepMode = false;      // It sets the boolean value to false.
                    }
                }
            }
        });

        setAutoResumeEnabled(true);     // Keeps the screen awake.

        try     // Try doing this to keep up
        {
            registerReceiver(mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));        // Register the battery level broadcaster
        }
        catch(Exception ignored)        // Catch exception
        {
            // Do nothing
        }
    }

    Thread Main_Timer = new Thread()    /* This Updates the Date and Time Every second when UI is in the foreground */
    {
        @Override
        public void run()       // When the timer updater is run, it starts the following.
        {
            try     // it tired to run the following
            {
                while (!Main_Timer.isInterrupted())       // While the timer updater is not interrupted by some other system.
                {
                    Thread.sleep(1000);     // Wait 1 second.
                    runOnUiThread(new Runnable()        // Run this while the user interface is on.
                    {
                        @SuppressLint("SetTextI18n")        // Suppresses some more errors.
                        @Override
                        public void run()       // This is run on the main system.
                        {
                            SystemInformation systemInformation = SystemInformation;      // Gets the methods from the system information class.
                            DataLogger stepActivity = new DataLogger("Step_Activity","no");      // Logs step data to the file.
                            WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);        // Gets the wifi system on the watch.
                            BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();      // Gets the bluetooth system on the watch

                            time.setText(systemInformation.getTime());       // The current time is set to show on the time text view.
                            date.setText(systemInformation.getDate());       // The current date is set to show on the date text view.
                            isCharging = systemInformation.isSystemCharging(getApplicationContext());     // Checks if the battery is currently charging.
                            batteryLevel.setText("Battery: " + String.valueOf(systemInformation.getBatteryLevel(getApplicationContext())) + "%");       // Sets the text view for the battery to show the battery level.

                            if (!bluetooth.isEnabled())     // If the bluetooth is not enabled on the watch
                            {
                                String data =  ("Main Thread," + "Enabled Bluetooth at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
                                DataLogger datalog = new DataLogger(Sensors, data);      // Logs it into a file called System Activity.
                                datalog.LogData();      // Saves the data into the directory.

                                bluetooth.enable();     // Enable it.
                            }

                            if (isCharging)     // If the battery is charging
                            {
                                if (!BatteryCharge)     // Checks if the battery is charging
                                {
                                    while (!isDeviceOnline())       // Checks if the device has an internet connection
                                    {
                                        if(systemInformation.isSystemCharging(getApplicationContext()))     // Gets the system status from system information.
                                        {break;}    // Stop infinite loop if no WiFi available and device is disconnected from charger

                                        String data =  ("Main Thread," + "Trying to enable an Internet connection at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
                                        DataLogger datalog = new DataLogger(Sensors, data);      // Logs it into a file called System Activity.
                                        datalog.LogData();      // Saves the data into the directory.

                                        wifi.setWifiEnabled(true);      // Sets the wifi of the system on.
                                    }
                                }

                                if (!BatteryCharge || !SleepMode)       // If the battery is not charging and it is not in sleep mode
                                {
                                    if (!BatteryCharge && isDeviceOnline())     // If there is a connection.
                                    {
                                        String data =  ("Main Thread," + "Uploading Data to Firebase at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
                                        DataLogger datalog = new DataLogger(Sensors, data);      // Logs it into a file called System Activity.
                                        datalog.LogData();      // Saves the data into the directory.

                                        uploadData();       // Calls the upload method.
                                    }

                                    if (!SleepMode)     // If it is not in sleep mode
                                    {
                                        SLEEP.performClick();       // Perform a coded click on the sleep button
                                        LogActivityCharge();        // Call the charging method to start logging.
                                    }

                                    BatteryCharge = true;       // Sets the battery charge status to true.
                                }
                            }

                            else        // If the watch is not charging.
                            {
                                if (isDeviceOnline())       // If the wifi system is enabled.
                                {
                                    String data =  ("Main Thread," + "Wifi is disabled at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
                                    DataLogger datalog = new DataLogger(Sensors, data);      // Logs it into a file called System Activity.
                                    datalog.LogData();      // Saves the data into the directory.

                                    wifi.setWifiEnabled(false);     // Disable the wifi.
                                }

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

                            UIUpdater();
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

    @Override
    public void onResume()      // When the system resumes
    {
        final Intent PedomService = new Intent(getBaseContext(), PedometerSensor.class);        // Creates an intent for calling the pedometer service.
        if(!isRunning(PedometerSensor.class))       // If the pedometer service is not running
        {
            String data =  ("Main Activity," + "Resuming Started Pedometer Sensor at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
            DataLogger datalog = new DataLogger(Sensors, data);      // Logs it into a file called System Activity.
            datalog.LogData();      // Saves the data into the directory.

            startService(PedomService);        // Starts the service.
        }

        final Intent EstimService = new Intent(getBaseContext(), ESTimerService.class);        // Creates an intent for calling the Estimote Timer service.
        if(!isRunning(ESTimerService.class))       // If the Estimote Timer service is not running
        {
            String data =  ("Main Activity," + "Resuming Started Estimote Timer at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
            DataLogger datalog = new DataLogger(Sensors, data);      // Logs it into a file called System Activity.
            datalog.LogData();      // Saves the data into the directory.

            startService(EstimService);        // Starts the service.
        }

        super.onResume();       // Restarts the thread left.
    }

    private void UIUpdater()
    {
        SystemInformation systemInformation = SystemInformation;
        int startHour = Preference.EoDEMA_ManualPop_Hour;     // Gets the hour of the day from the preference.
        int startMinute = Preference.EoDEMA_ManualPop_Minute;     // Gets the minutes of the day from the preference.
        int startSecond = Preference.EoDEMA_ManualPop_Second;
        int endHour = Preference.EoDEMA_Time_Hour;     // Gets the hour of the day from the preference.
        int endMinute = Preference.EoDEMA_Time_Minute;     // Gets the hour of the day from the preference.
        int endSecond = Preference.EoDEMA_Time_Second;     // Gets the hour of the day from the preference.

        Log.i("Main Activity", String.valueOf(systemInformation.isTimeBetweenTwoTimes(systemInformation.getTimeMilitary(), startHour, endHour, startMinute, endMinute, startSecond, endSecond)));

        if (systemInformation.isTimeBetweenTwoTimes(systemInformation.getTimeMilitary(), startHour, endHour, startMinute, endMinute, startSecond, endSecond))
        {
            EMA_Start.setVisibility(View.INVISIBLE);
            EOD_EMA_Start.setVisibility(View.VISIBLE);
            Daily_Survey.setVisibility(View.VISIBLE);
        }
        else
        {
            EMA_Start.setVisibility(View.VISIBLE);
            EOD_EMA_Start.setVisibility(View.INVISIBLE);
            Daily_Survey.setVisibility(View.INVISIBLE);
        }
    }

    private void LogActivityCharge()        // Logs the times when the battery is charging.
    {
        String data;        // This is the data to be logged

        if (isCharging)     // If the system is charging
        {
            data = SystemInformation.getTimeStamp() + ",Plugged," + SystemInformation.getBatteryLevel(getApplicationContext());        // Gets the battery level information and logs it
        }
        else      // If we are not charging
        {
            data = SystemInformation.getTimeStamp() + ",UnPlugged," + SystemInformation.getBatteryLevel(getApplicationContext());        // Gets the battery level information and logs it
        }

        DataLogger datalog = new DataLogger(Battery, data);      // Logs it into a file called System Activity.
        datalog.LogData();      // Saves the data into the directory.
    }

    private void Charging()     // This is a little charging toast notification.
    {
        Context context = getApplicationContext();      // Gets a context from the system.
        CharSequence showntext = "Disabled while Charging";       // Pop up information to the person
        int duration = Toast.LENGTH_SHORT;      // Shows the toast only for a short amount of time.

        Toast toast = Toast.makeText(context, showntext, duration);          // A short message at the end to say thank you.
        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);        // Sets the toast to show up at the center of the screen
        View view = toast.getView();        // Gets the view from the toast maker
        TextView text = view.findViewById(android.R.id.message);        // Finds the text being used
        text.setTextColor(Color.WHITE);     // Changes the color of the text
        toast.show();       // Shows the toast.
    }

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

    private void uploadData()       // This calls the Fire Base activity to upload of data
    {
        Intent upload = new Intent(getApplicationContext(), FireBase_Upload.class);      // Makes an intent of the system
        if(!isRunning(FireBase_Upload.class))       // Checks if it is already running
        {
            startActivity(upload);      // If not, start it.
        }
    }

    public boolean isDeviceOnline()     // This checks if the device is online and has an internet connection
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);     // Gets the connection service manager
        NetworkInfo activeNetworkinfo = connectivityManager.getActiveNetworkInfo();     // It checks if there is a connection to the system
        return activeNetworkinfo != null && activeNetworkinfo.isConnected();        // It returns the outcome.
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

    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver()    /* Gets the current battery level, date, and time and sets the text field data */
    {
        @Override
        public void onReceive(final Context context, Intent intent)     // Receives the broadcast.
        {
            // This is just a receiver.
        }
    };

    @Override
    public void onEnterAmbient (Bundle ambientDetails)      // When you enter ambient mode
    {
        super.onEnterAmbient(ambientDetails);       // Set it to the ambient details set.
    }
}
