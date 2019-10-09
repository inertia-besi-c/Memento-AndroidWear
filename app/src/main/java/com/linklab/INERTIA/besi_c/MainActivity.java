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
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.view.View.INVISIBLE;

/* ************************************************************************************* MAIN ACTIVITY OF THE APP ************************************************************************************************** */
@SuppressWarnings("ALL")
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
    private String EODEMA_Date = Preference.EODEMA_Date;        // Gets the EODEMA file from preferences.
    private String Step = Preference.Steps;     // Gets the step file from preferences.
    private String Directory = Preference.Directory;     // Gets the directory from the preferences class.
    private String FileName = SystemInformation.EODEMA_Date_Path;        // Initiates a variable for the filename from preferences
    private String Pain_Activity = Preference.Pain_Activity;      // Gets the Followup Activity File label from Preferences
    private String Subdirectory_Accelerometer = Preference.Subdirectory_Accelerometer;       // This is where the accelerometer data is kept
    private String Subdirectory_Heartrate = Preference.Subdirectory_HeartRate;      // This is where the Heartrate data is kept
    private String Subdirectory_Estimote = Preference.Subdirectory_Estimote;        // This is where the estimote is kept
    private String Subdirectory_EMAActivities = Preference.Subdirectory_EMAActivities;      // This is where the EMA activity data are kept
    private String Subdirectory_EMAResults = Preference.Subdirectory_EMAResults;        // This is where the EMA responses data are kept
    private String Subdirectory_DeviceActivities = Preference.Subdirectory_DeviceActivities;       // This is where the device data that is used to update something in the app is kept
    private String Subdirectory_DeviceLogs = Preference.Subdirectory_DeviceLogs;        // This is where all the system logs and data are kept.
    private String lastLine;        // Last line variable
    private String currentLine;         // Current line being read by the system
    private File EODEMAfile = new File(Directory, FileName);       // Looks for a filename with the new filename
    private int startHour = Preference.EoDEMA_ManualStart_Hour;     // This is the hour the button pops up
    private int startMinute = Preference.EoDEMA_ManualStart_Minute;     // This is the minute the button pops up
    private int startSecond = Preference.EoDEMA_ManualStart_Second;     // This is the second the button pops up
    private int endHour = Preference.EoDEMA_ManualEnd_Hour;     // This is the hour the button goes away
    private int endMinute = Preference.EoDEMA_ManualEnd_Minute;     // This is the minute the button goes away
    private int endSecond = Preference.EoDEMA_ManualEnd_Second;     // This is the seconds the button goes away
    private int HapticFeedback = Preference.HapticFeedback;      // This is the haptic feedback for button presses.
    private int LowBatteryAlert = Preference.LowBatteryAlert;       // This is the low batteryu alert.
    private int UIUpdater = Preference.UIUpdate;        // This is the UI Update variable from Preferences
    private int ThreadUpdater = Preference.ThreadUpdater;       // This is how often the main thread is run
    private int UIUpdatevariable = 0;       // This is the update cycle of the screen
    private int LowBatteryTimer = 0;        // This is the update cycle for the low battery screen
    private int BatteryLevelText;        // This is the battery level
    public boolean SleepMode = false;      // This is the boolean that runs the sleep cycle.
    private boolean BatteryCharge = false;      // This is the boolean that runs the battery charge cycle.
    private boolean isCharging;     // Boolean value that keeps track of if the watch is charging or not.
    private Vibrator vibrator;      // The vibrator that provides haptic feedback.

    @SuppressLint("WakelockTimeout")        // Suppresses errors.
    @Override
    protected void onCreate(Bundle savedInstanceState)      // This is created on startup
    {
        super.onCreate(savedInstanceState);      // Creates the main screen.
        setContentView(R.layout.activity_main);     // This is where the texts and buttons seen were made. (Look into: res/layout/activity_main)

        final Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);          /* Vibrator values and their corresponding requirements */
        final WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);        // Gets the wifi system on the watch.

        CheckPermissions();     // Checks the permissions needed for the device to save files and operate within normal parameter.
        CheckFiles();       // Runs the check files method to make sure all the files needed are up
        unlockScreen();     // Unlocks the screen

        Main_Timer.start();       // The time updater

        EMA_Start = findViewById(R.id.EMA_Start);      // This is the second ema button that is used
        Daily_Survey = findViewById(R.id.DAILY_SURVEY);       // This is the end of day EMA button
        SLEEP = findViewById(R.id.SLEEP);        // The sleep button is made
        batteryLevel = findViewById(R.id.BATTERY_LEVEL);    // Battery level view ID
        date = findViewById(R.id.DATE);     // The date view ID
        time = findViewById(R.id.TIME);     // The time view ID

        final Intent HRService = new Intent(getBaseContext(), HRTimerService.class);        // Gets an intent for the start of the heartrate sensor.
        if (!isRunning(HRTimerService.class))       // Starts the heart rate timer controller
        {
            String data =  ("Main Activity," + "Started Heart Rate Timer at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
            DataLogger datalog = new DataLogger(Subdirectory_DeviceLogs, Sensors, data);      // Logs it into a file called System Activity.
            datalog.LogData();      // Saves the data into the directory.

            startService(HRService);        // That starts the heartrate sensor if it is not already running.
        }

        final Intent AccelService = new Intent(getBaseContext(), AccelerometerSensor.class);        // Creates an intent for calling the accelerometer service.
        if(!isRunning(AccelerometerSensor.class))       // If the accelerometer service is not running
        {
            String data =  ("Main Activity," + "Started Accelerometer Sensor at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
            DataLogger datalog = new DataLogger(Subdirectory_DeviceLogs, Sensors, data);      // Logs it into a file called System Activity.
            datalog.LogData();      // Saves the data into the directory.

            startService(AccelService);        // Starts the service.
        }

        final Intent PedomService = new Intent(getBaseContext(), PedometerSensor.class);        // Creates an intent for calling the pedometer service.
        if(!isRunning(PedometerSensor.class))       // If the pedometer service is not running
        {
            String data =  ("Main Activity," + "Started Pedometer Sensor at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
            DataLogger datalog = new DataLogger(Subdirectory_DeviceLogs, Sensors, data);      // Logs it into a file called System Activity.
            datalog.LogData();      // Saves the data into the directory.

            startService(PedomService);        // Starts the service.
        }

        final Intent ESService = new Intent(getBaseContext(), ESTimerService.class);        // Creates an intent for calling the Estimote Timer service.
        if(!isRunning(ESTimerService.class))       // If the Estimote Timer service is not running
        {
            String data =  ("Main Activity," + "Started Estimote Timer at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
            DataLogger datalog = new DataLogger("Activity", Sensors, data);      // Logs it into a file called System Activity.
            datalog.LogData();      // Saves the data into the directory.

            startService(ESService);        // Starts the service.
        }

        final Intent EODScheduler = new Intent(getBaseContext(), EODTimerService.class);        // Creates an intent for calling the End of Day Timer service.
        if(!isRunning(EODTimerService.class))            // If the End of Day Timer is not running
        {
            String data =  ("Main Activity," + "Restarted EOD Timer at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
            DataLogger datalog = new DataLogger(Subdirectory_DeviceLogs, Sensors, data);      // Logs it into a file called Sensors Activity.
            datalog.LogData();      // Saves the data into the directory.

            startService(EODScheduler);        // Starts the service.
        }

        EMA_Start.setOnClickListener(new View.OnClickListener()     /* Listens for the EMA button "START" to be clicked. */
        {
            public void onClick(View v)     // When the button is clicked the is run
            {
                Log.i("Main Activity", "Main Activity Start Button Clicked, Starting Pain EMA");     // Logs on Console.

                File system = new File(Preference.Directory + SystemInformation.System_Path);     // Gets the path to the system from the system.
                if (!system.exists())      // If the file exists
                {
                    Log.i("Main Activity", "Creating Header");     // Logs on Console.

                    DataLogger dataLogger = new DataLogger(Subdirectory_DeviceLogs, System, Preference.System_Data_Headers);        /* Logs the system data in a csv format */
                    dataLogger.LogData();       // Saves the data to the directory.
                }

                File sensors = new File(Preference.Directory + SystemInformation.Sensors_Path);     // Gets the path to the Sensors from the system.
                if (!sensors.exists())      // If the file exists
                {
                    Log.i("Main Activity", "Creating Header");     // Logs on Console.

                    DataLogger dataLogger = new DataLogger(Subdirectory_DeviceLogs, Sensors, Preference.Sensor_Data_Headers);        /* Logs the Sensors data in a csv format */
                    dataLogger.LogData();       // Saves the data to the directory.
                }

                vibrator.vibrate(HapticFeedback);      // A slight haptic feedback is provided.

                String data =  ("Main Activity," + "'Start' Button Tapped at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.

                DataLogger datalog = new DataLogger(Subdirectory_DeviceLogs, System, data);      // Logs it into a file called System Activity.
                DataLogger datalog1 = new DataLogger(Subdirectory_EMAActivities, Pain_Activity, data);      // Logs it into a file called Preferences.

                datalog.LogData();      // Saves the data into the directory.
                datalog1.LogData();      // Saves the data into the directory.

                Intent StartEMAActivity = new Intent(getBaseContext(), PainEMA.class);      // Links to the Pain EMA File
                startActivity(StartEMAActivity);    // Starts the Pain EMA file

                finish();       // Finished the Main Activity screen.
            }
        });

        Daily_Survey.setOnClickListener(new View.OnClickListener()     // Listens for an action on the button.
        {
            public void onClick(View v)     // When the button is clicked
            {
                Log.i("Main Activity", "Main Activity End of Day EMA Clicked, Starting End of Day EMA");     // Logs on Console.

                String data =  ("End of Day EMA Prompt," + "'Daily Survey' Button Tapped at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
                String data1 =  ("Main Activity 'Daily Survey' Button," + "Started End of Day EMA at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.

                DataLogger datalog = new DataLogger(Subdirectory_DeviceLogs, System, data);      // Logs it into a file called System Activity.
                DataLogger datalog1 = new DataLogger(Subdirectory_DeviceLogs, Sensors, data1);      // Logs it into a file called System Activity.

                datalog.LogData();      // Saves the data into the directory.
                datalog1.LogData();      // Saves the data into the directory.

                vibrator.vibrate(HapticFeedback);     // Vibrates for the specified amount of time in milliseconds.

                Intent StartEMAActivity = new Intent(getBaseContext(), ManualDailyEMA.class);      // Links to the EOD EMA File prompt and starts it.
                startActivity(StartEMAActivity);        // Starts the EOD EMA file.

                finish();       // Finished the Main Activity screen.
            }
        });

        SLEEP.setOnClickListener(new View.OnClickListener()        // Listens for the SLEEP button "SLEEP" to be clicked.
        {
            public void onClick(View v)     // When the sleep button is clicked
            {
                File system = new File(Preference.Directory + SystemInformation.System_Path);     // Gets the path to the system from the system.
                if (!system.exists())      // If the file exists
                {
                    Log.i("Main Activity", "Creating Header");     // Logs on Console.

                    DataLogger dataLogger = new DataLogger(Subdirectory_DeviceLogs, System, Preference.System_Data_Headers);        /* Logs the system data in a csv format */
                    dataLogger.LogData();       // Saves the data to the directory.
                }

                File sensors = new File(Preference.Directory + SystemInformation.Sensors_Path);     // Gets the path to the Sensors from the system.
                if (!sensors.exists())      // If the file exists
                {
                    Log.i("Main Activity", "Creating Header");     // Logs on Console.

                    DataLogger dataLogger = new DataLogger(Subdirectory_DeviceLogs, Sensors, Preference.Sensor_Data_Headers);        /* Logs the Sensors data in a csv format */
                    dataLogger.LogData();       // Saves the data to the directory.
                }

                vibrator.vibrate(HapticFeedback);      // A slight haptic feedback is provided.

                if (isCharging)     // Checks if the watch is charging
                {
                    SLEEP.setBackgroundColor(Color.GRAY);      // Changes the color of the Sleep button.
                    SleepMode = true;       // And it sets the boolean value to true.
                    wifi.setWifiEnabled(true);      // Sets the wifi of the system on.

                    String datawifi =  ("Main Thread," + "Enabled an Internet Connection at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
                    String data =  ("Main Activity," + "'Sleep' Button Tapped while charging at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.

                    DataLogger datalog = new DataLogger(Subdirectory_DeviceLogs, System, data);      // Logs it into a file called System Activity.
                    DataLogger datalogwifi = new DataLogger(Subdirectory_DeviceLogs, Sensors, datawifi);      // Logs it into a file called System Activity.
                    DataLogger stepActivity = new DataLogger(Subdirectory_DeviceActivities, Step,"no");      // Logs step data to the file.

                    datalog.LogData();      // Saves the data into the directory.
                    datalogwifi.LogData();      // Saves the data into the directory.
                    stepActivity.WriteData();       // Writes no to the system to stop repetitive clicking of sleep button.

                    if (isRunning(HRTimerService.class))        // If the heart rate timer service is running
                    {
                        String dataHR =  ("Sleep Button," + "Stopped Heart Rate Sensor while charging at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
                        String dataA =  ("Sleep Button," + "Stopped Accelerometer Sensor while charging at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
                        String dataB =  ("Sleep Button," + "Stopped Estimote Sensor while charging at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.

                        DataLogger datalogHR = new DataLogger(Subdirectory_DeviceLogs, Sensors, dataHR);      // Logs it into a file called System Activity.
                        DataLogger datalogA = new DataLogger(Subdirectory_DeviceLogs, Sensors, dataA);      // Logs it into a file called System Activity.
                        DataLogger datalogB = new DataLogger(Subdirectory_DeviceLogs, Sensors, dataB);      // Logs it into a file called System Activity.

                        datalogHR.LogData();      // Saves the data into the directory.
                        datalogA.LogData();      // Saves the data into the directory.
                        datalogB.LogData();      // Saves the data into the directory.

                        stopService(ESService);        // Stop the service.
                        stopService(AccelService);        // Stop the service.
                        stopService(HRService);     // It stops the service
                    }

//                    Intent upload = new Intent(getApplicationContext(), FireBase_Upload.class);      // Makes an intent of the system
//                    if(!isRunning(FireBase_Upload.class))       // Checks if it is already running
//                    {
//                        startActivity(upload);      // If not, start it.
//                    }

                    Charging();     // Calls the charging method to inform the person
                }

                else        // If the watch is not charging
                {
                    wifi.setWifiEnabled(false);     // Disable the wifi.

                    String datawifi =  ("Main Thread," + "Wifi is disabled at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
                    String data =  ("Main Activity," + "'Sleep' Button Tapped while NOT charging at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.

                    DataLogger datalogwifi = new DataLogger(Subdirectory_DeviceLogs, Sensors, datawifi);      // Logs it into a file called System Activity.
                    DataLogger datalog = new DataLogger(Subdirectory_DeviceLogs, System, data);      // Logs it into a file called System Activity.

                    datalogwifi.LogData();      // Saves the data into the directory.
                    datalog.LogData();      // Saves the data into the directory.

                    if (isRunning(HRTimerService.class))        // If the heart rate timer service is running
                    {
                        SLEEP.setBackgroundColor(Color.GRAY);      // Changes the color of the Sleep button.

                        String dataHR =  ("Sleep Button," + "Stopped Heart Rate Sensor while NOT charging at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
                        String dataA =  ("Sleep Button," + "Stopped Accelerometer Sensor while NOT charging at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
                        String dataB =  ("Sleep Button," + "Stopped Estimote Sensor while NOT charging at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.

                        DataLogger datalogHR = new DataLogger(Subdirectory_DeviceLogs, Sensors, dataHR);      // Logs it into a file called System Activity.
                        DataLogger datalogA = new DataLogger(Subdirectory_DeviceLogs, Sensors, dataA);      // Logs it into a file called System Activity.
                        DataLogger datalogB = new DataLogger(Subdirectory_DeviceLogs, Sensors, dataB);      // Logs it into a file called System Activity.

                        datalogHR.LogData();      // Saves the data into the directory.
                        datalogA.LogData();      // Saves the data into the directory.
                        datalogB.LogData();      // Saves the data into the directory.

                        stopService(HRService);     // Stops the service
                        stopService(AccelService);        // Stop the service.
                        stopService(ESService);        // Stop the service.

                        SleepMode = true;       // And it sets the boolean value to true.
                    }

                    else        // If the heart rate timer is not running
                    {
                        SLEEP.setBackgroundColor(Color.BLUE);      // Changes the color of the Sleep button.

                        if (LowBatteryTimer >= LowBatteryAlert)     // This makes the low batery screen at the specified rate multiplied by the delay abocve.
                        {
                            if (BatteryLevelText <= Preference.LowBatPercent)   // Checks whether battery is low
                            {
                                String dataLB =  ("Main Thread," + "Enabling Low Battery Warning at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
                                DataLogger datalogLB = new DataLogger(Subdirectory_DeviceLogs, Sensors, dataLB);      // Logs it into a file called System Activity.
                                datalogLB.LogData();      // Saves the data into the directory.

                                Intent intent = new Intent(getApplicationContext(), LowBattery.class);       // Calls the low battery class
                                startActivity(intent);      // Starts low battery screen

                                LowBatteryTimer = 0;        // Resets the low battery timer screen
                            }
                        }

                        String dataHR =  ("Sleep Button," + "Started Heart Rate Sensor while NOT charging at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
                        String dataA =  ("Sleep Button," + "Started Accelerometer Sensor while NOT charging at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
                        String dataB =  ("Sleep Button," + "Started Estimote Sensor while NOT charging at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.

                        DataLogger datalogA = new DataLogger(Subdirectory_DeviceLogs, Sensors, dataA);      // Logs it into a file called System Activity.
                        DataLogger datalogHR = new DataLogger(Subdirectory_DeviceLogs, Sensors, dataHR);      // Logs it into a file called System Activity.
                        DataLogger datalogB = new DataLogger(Subdirectory_DeviceLogs, Sensors, dataB);      // Logs it into a file called System Activity.

                        datalogHR.LogData();      // Saves the data into the directory.
                        datalogA.LogData();      // Saves the data into the directory.
                        datalogB.LogData();      // Saves the data into the directory.

                        startService(HRService);     // It starts the service
                        startService(AccelService);        // It starts the service.
                        startService(ESService);        // It starts the service.

                        SleepMode = false;      // It sets the boolean value to false.
                    }
                }
            }
        });

        try     // Try doing this to keep up
        {
            registerReceiver(mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));        // Register the battery level broadcaster
        }
        catch(Exception ignored)        // Catch exception
        {
            String data =  ("Main Activity," + "Error in getting battery infromation from Battery Changed Action at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
            DataLogger datalog = new DataLogger(Subdirectory_DeviceLogs, Sensors, data);      // Logs it into a file called System Activity.
            datalog.LogData();      // Saves the data into the directory.
        }

        setAutoResumeEnabled(true);     // Keeps the screen awake.
        EODEMAUIUpdater();      // Calls the system to update itself immediatelty
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
                    Thread.sleep(ThreadUpdater);     // Wait the alloted time in seconds.
                    runOnUiThread(new Runnable()        // Run this while the user interface is on.
                    {
                        @Override
                        public void run()       // This is run on the main system.
                        {
                            SystemInformation systemInformation = SystemInformation;      // Gets the methods from the system information class.
                            BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();      // Gets the bluetooth system on the watch
                            WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);        // Gets the wifi system on the watch.
                            isCharging = systemInformation.isSystemCharging(getApplicationContext());     // Checks if the battery is currently charging.
                            BatteryLevelText = Integer.parseInt(systemInformation.getBatteryLevel(getApplicationContext()));     // Gets the battery level of the system in text

                            DataLogger stepActivity = new DataLogger(Subdirectory_DeviceActivities, Step,"no");      // Logs step data to the file.
                            batteryLevel.setText("Battery: " + String.valueOf(BatteryLevelText) + "%");       // Sets the text view for the battery to show the battery level.

                            UIUpdatevariable += ThreadUpdater/1000;     // Increments the timer to update the UI
                            LowBatteryTimer += ThreadUpdater/1000;      // Increments the timer for the low battery screen
                            time.setText(systemInformation.getTime());       // The current time is set to show on the time text view.
                            date.setText(systemInformation.getDate());       // The current date is set to show on the date text view.

                            if (UIUpdatevariable >= UIUpdater)     // This makes the UI update at the specified rate multiplied by the delay abocve.
                            {
                                EODEMAUIUpdater();      // Calls the UI updater method
                                UIUpdatevariable = 0;       // Resets the variable
                            }

                            if (!bluetooth.isEnabled())     // If the bluetooth is not enabled on the watch
                            {
                                String data =  ("Main Thread," + "Enabled Bluetooth at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
                                DataLogger datalog = new DataLogger(Subdirectory_DeviceLogs, Sensors, data);      // Logs it into a file called System Activity.
                                datalog.LogData();      // Saves the data into the directory.

                                bluetooth.enable();     // Enable it.
                            }

                            if (stepActivity.ReadData().contains("yes"))        // If the file contains yes
                            {
                                if (!isRunning(ESTimerService.class))       // And you are not running the estimote timer
                                {
                                    Intent Estimote = new Intent(getBaseContext(),ESTimerService.class);        // Create an intent
                                    startService(Estimote);     // Start the estimote timer
                                }
                            }

                            if (!SleepMode)     // If it is not in sleep mode
                            {
                                if (LowBatteryTimer >= LowBatteryAlert)     // This makes the low batery screen at the specified rate multiplied by the delay abocve.
                                {
                                    if (BatteryLevelText <= Preference.LowBatPercent)   // Checks whether battery is low
                                    {
                                        String dataLB =  ("Main Thread," + "Enabling Low Battery Warning at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
                                        DataLogger datalogLB = new DataLogger(Subdirectory_DeviceLogs, Sensors, dataLB);      // Logs it into a file called System Activity.
                                        datalogLB.LogData();      // Saves the data into the directory.

                                        Intent lowbattery = new Intent(getApplicationContext(), LowBattery.class);       // Calls the low battery class
                                        startActivity(lowbattery);      // Starts low battery screen

                                        LowBatteryTimer = 0;        // Resets the low battery timer screen
                                    }
                                }
                            }

                            if (isCharging)     // If the battery is charging
                            {
                                if (!BatteryCharge)     // Checks if the battery is charging
                                {
                                    while (!isDeviceOnline())       // Checks if the device has an internet connection
                                    {
                                        if(systemInformation.isSystemCharging(getApplicationContext()))     // Gets the system status from system information.
                                        {break;}    // Stop infinite loop if no WiFi available and device is disconnected from charger

                                        String data =  ("Main Thread," + "Enabling an Internet connection at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
                                        DataLogger datalog = new DataLogger(Subdirectory_DeviceLogs, Sensors, data);      // Logs it into a file called System Activity.
                                        datalog.LogData();      // Saves the data into the directory.

                                        wifi.setWifiEnabled(true);      // Sets the wifi of the system on.
                                    }
                                }

                                if (!BatteryCharge || !SleepMode)       // If the battery is not charging and it is not in sleep mode
                                {
                                    if (!BatteryCharge && isDeviceOnline())     // If there is a connection.
                                    {
                                        String data =  ("Main Thread," + "Uploading to BESI Basestation at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
                                        DataLogger datalog = new DataLogger(Subdirectory_DeviceLogs, Sensors, data);      // Logs it into a file called System Activity.
                                        datalog.LogData();      // Saves the data into the directory.

//                                        String data =  ("Main Thread," + "Uploading Data to Firebase at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
//                                        DataLogger datalog = new DataLogger(Subdirectory_DeviceLogs, Sensors, data);      // Logs it into a file called System Activity.
//                                        datalog.LogData();      // Saves the data into the directory.

//                                        uploadData();       // Calls the upload method.
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
                                wifi.setWifiEnabled(false);     // Disable the wifi.

                                if (isDeviceOnline())       // If the wifi is still when the watch is not charging
                                {
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
                        }
                    });
                }
            }
            catch (InterruptedException e)      // A catch for if it fails.
            {
                String data =  ("Main Activity," + "Failed to Launch Timer at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
                DataLogger datalog = new DataLogger(Subdirectory_DeviceLogs, Sensors, data);      // Logs it into a file called System Activity.
                datalog.LogData();      // Saves the data into the directory.
            }
        }
    };

    public void CheckPermissions()
    {
        String[] Required_Permissions =     // Checks if Device has permission to work on device.
                {
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,     // This is to access the storage
                        Manifest.permission.READ_EXTERNAL_STORAGE,      // This is to access the storage
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
    }

    @Override
    public void onResume()      // When the system resumes
    {
        CheckPermissions();     // Checks that all the permissions needed are enabled. If not, it request them.
        CheckFiles();       // Checks the files and subdirectories to make sure the system files are up

        final Intent HRService = new Intent(getBaseContext(), HRTimerService.class);        // Gets an intent for the start of the heartrate sensor.
        if (!isRunning(HRTimerService.class))       // Starts the heart rate timer controller
        {
            String data =  ("Main Activity," + "Restarted Heart Rate Timer at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
            DataLogger datalog = new DataLogger(Subdirectory_DeviceLogs, Sensors, data);      // Logs it into a file called System Activity.
            datalog.LogData();      // Saves the data into the directory.

            startService(HRService);        // That starts the heartrate sensor if it is not already running.
        }

        final Intent AccelService = new Intent(getBaseContext(), AccelerometerSensor.class);        // Creates an intent for calling the accelerometer service.
        if(!isRunning(AccelerometerSensor.class))       // If the accelerometer service is not running
        {
            String data =  ("Main Activity," + "Restarted Accelerometer Sensor at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
            DataLogger datalog = new DataLogger(Subdirectory_DeviceLogs, Sensors, data);      // Logs it into a file called System Activity.
            datalog.LogData();      // Saves the data into the directory.

            startService(AccelService);        // Starts the service.
        }

        final Intent PedomService = new Intent(getBaseContext(), PedometerSensor.class);        // Creates an intent for calling the pedometer service.
        if(!isRunning(PedometerSensor.class))       // If the pedometer service is not running
        {
            String data =  ("Main Activity," + "Restarted Pedometer Sensor at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
            DataLogger datalog = new DataLogger(Subdirectory_DeviceLogs, Sensors, data);      // Logs it into a file called System Activity.
            datalog.LogData();      // Saves the data into the directory.

            startService(PedomService);        // Starts the service.
        }

        final Intent EstimService = new Intent(getBaseContext(), ESTimerService.class);        // Creates an intent for calling the Estimote Timer service.
        if(!isRunning(ESTimerService.class))       // If the Estimote Timer service is not running
        {
            String data =  ("Main Activity," + "Restarted Estimote Timer at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
            DataLogger datalog = new DataLogger(Subdirectory_DeviceLogs, Sensors, data);      // Logs it into a file called Sensors Activity.
            datalog.LogData();      // Saves the data into the directory.

            startService(EstimService);        // Starts the service.
        }

        final Intent EODScheduler = new Intent(getBaseContext(), EODTimerService.class);        // Creates an intent for calling the Estimote Timer service.
        if(true)//!isRunning(EODTimerService.class))       // If the Estimote Timer service is not running
        {
            String data =  ("Main Activity," + "Restarted EOD Timer at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
            DataLogger datalog = new DataLogger(Subdirectory_DeviceLogs, Sensors, data);      // Logs it into a file called Sensors Activity.
            datalog.LogData();      // Saves the data into the directory.

            startService(EODScheduler);        // Starts the service.
        }

        super.onResume();       // Forces the resume.
    }

    private void EODEMAUIUpdater()      // Updates the user interface for the daily EMA if the time is right.
    {
        SystemInformation systemInformation = SystemInformation;        // This is the system information center for the app.
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.US);     // A date variable is initialized
        Date date = new Date();     // Starts a new date call.

        try     // Tries to run the following.
        {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(EODEMAfile));      // Reads the buffer in the system

            while ((currentLine = bufferedReader.readLine()) != null)     // While the line is not blank
            {
                lastLine = currentLine;     // Set the last line to the last line with words
            }
            bufferedReader.close() ;       // Close the buffer reader.
        }
        catch (IOException e)   // Catch statement
        {
            e.printStackTrace();        // Ignore this.
        }

        if(!EODEMAfile.exists())      // Checks if the file even exist in the system. If not, it makes one and calls the EMA.
        {
            DataLogger DailyActivity = new DataLogger(Subdirectory_DeviceActivities, EODEMA_Date, "Date");      // Logs date data to the file.
            DailyActivity.LogData();      // Logs the data to the BESI_C directory.
        }

        if (systemInformation.isTimeBetweenTimes(systemInformation.getTimeMilitary(), startHour, endHour, startMinute, endMinute, startSecond, endSecond))         /* Checks if the daily EMA button should be up */
        {
            String data =  ("Main Activity," + "Displayed Daily Survey Button," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
            DataLogger datalog = new DataLogger(Subdirectory_DeviceLogs, System, data);      // Logs it into a file called System Activity.
            datalog.LogData();      // Saves the data into the directory.

            Daily_Survey.setVisibility(View.VISIBLE);       // Sets the daily EMA button to visible.
            SLEEP.setVisibility(INVISIBLE);     // Sets the sleep button to invisible
        }
        else        // If we are not in the range of time we are looking for.
        {
            Daily_Survey.setVisibility(INVISIBLE);     // Sets the daily EMA button to invisible.
            SLEEP.setVisibility(View.VISIBLE);      // Sets the sleep button to visibile
        }

        if (lastLine.equals(String.valueOf(dateFormat.format(date))))       // If the EOD EMA has been done for that day
        {
            Daily_Survey.setVisibility(INVISIBLE);       // Sets the daily EMA button to visible.
            SLEEP.setVisibility(View.VISIBLE);      // Sets the sleep button to visible.
        }
    }

    private void CheckFiles()
    {
        for(int subdirectory = 0; subdirectory < SystemInformation.Subdirectories.size(); subdirectory++)        // For every file and directory listed above
        {
            File BESI_subdirectory = new File(Directory, String.valueOf(SystemInformation.Subdirectories.get(subdirectory)));    // Path to file in the storage of the device

            if (!BESI_subdirectory.isDirectory())    // If there is no directory with that name
            {
                Log.i("Data Logger", "Making a directory called " + BESI_subdirectory + " in BESI-C");     // Logs on Console.
                BESI_subdirectory.mkdirs();        // Make a directory with the name.
            }
        }

        File battery = new File(Preference.Directory + SystemInformation.Battery_Path);     // Gets the path to the Sensors from the system.
        if (!battery.exists())      // If the file exists
        {
            Log.i("Main Activity", "Creating Header");     // Logs on Console.

            DataLogger dataLogger = new DataLogger(Subdirectory_DeviceLogs, Battery, Preference.Battery_Data_Headers);        /* Logs the Sensors data in a csv format */
            dataLogger.LogData();       // Saves the data to the directory.
        }

        File estimote = new File(Preference.Directory + SystemInformation.Estimote_Path);     // Gets the path to the Sensors from the system.
        if (!estimote.exists())      // If the file exists
        {
            Log.i("Estimote Sensor", "Creating Header");     // Logs on Console.

            DataLogger dataLogger = new DataLogger(Subdirectory_Estimote, Estimote, Preference.Estimote_Data_Headers);        /* Logs the Sensors data in a csv format */
            dataLogger.LogData();       // Saves the data to the directory.
        }

        File pedometer = new File(Preference.Directory + SystemInformation.Pedometer_Path);     // Gets the path to the Pedometer from the system.
        if (!pedometer.exists())      // If the file exists
        {
            Log.i("Pedometer Sensor", "Creating Header");     // Logs on Console.

            DataLogger dataLogger = new DataLogger(Subdirectory_DeviceLogs, Pedometer, Preference.Pedometer_Data_Headers);        /* Logs the Pedometer data in a csv format */
            dataLogger.LogData();       // Saves the data to the directory.
        }

        if(!EODEMAfile.exists())      // Checks if the file even exist in the system. If not, it makes one and calls the EMA.
        {
            DataLogger DailyActivity = new DataLogger(Subdirectory_DeviceActivities, EODEMA_Date, "Date");      // Logs date data to the file.
            DailyActivity.LogData();      // Logs the data to the BESI_C directory.
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

        DataLogger datalog = new DataLogger(Subdirectory_DeviceLogs, Battery, data);      // Logs it into a file called System Activity.
        datalog.LogData();      // Saves the data into the directory.
    }

    private void Charging()     // This is a little charging toast notification.
    {
        Context context = getApplicationContext();      // Gets a context from the system.
        CharSequence showntext = "Charging";       // Pop up information to the person
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

//    private void uploadData()       // This calls the Fire Base activity to upload of data
//    {
//        Intent upload = new Intent(getApplicationContext(), FireBase_Upload.class);      // Makes an intent of the system
//        if(!isRunning(FireBase_Upload.class))       // Checks if it is already running
//        {
//            startActivity(upload);      // If not, start it.
//        }
//    }

    @Override
    protected void onStop()     // To stop the activity.
    {
        try     // It tries to.
        {
            unregisterReceiver(mBatInfoReceiver);       // It unregisters the battery level listener.
        }
        catch(Exception ignored)        // A catch exception.
        {
            String data =  ("Main Activity," + "Failed to unregister battery information at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
            DataLogger datalog = new DataLogger(Subdirectory_DeviceLogs, Sensors, data);      // Logs it into a file called System Activity.
            datalog.LogData();      // Saves the data into the directory.
        }
        super.onStop();     // It stops the activity.
    }

    private void Toast(String message)
    {
        int duration = Toast.LENGTH_LONG;      // Shows the toast only for a short amount of time.
        Toast msg = Toast.makeText(getApplicationContext(), message, duration);          // A short message at the end to say thank you.
        msg.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);        // Sets the toast to show up at the center of the screen
        View view = msg.getView();        // Gets the view from the toast maker
        TextView text = view.findViewById(android.R.id.message);        // Finds the text being used
        text.setTextColor(Color.WHITE);     // Changes the color of the text
        msg.show();       // Shows the toast.
    }

    private void unlockScreen()         // This unlocks the screen if called
    {
        Window window = this.getWindow();       // Gets the window that is being used
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);      // Dismisses the button
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);      // Ignores the screen if locked
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);        // Turns on the screen
        window.addFlags(WindowManager.LayoutParams.FLAG_TOUCHABLE_WHEN_WAKING);        // Keeps the Screen on while waking up
    }

    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver()    /* Gets the current battery level, date, and time and sets the text field data */
    {
        @Override
        public void onReceive(final Context context, Intent intent)     // Receives the broadcast.
        {
            // This is just a receiver, do nothing
        }
    };

    public boolean isDeviceOnline()     // This checks if the device is online and has an internet connection
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);     // Gets the connection service manager
        NetworkInfo activeNetworkinfo = connectivityManager.getActiveNetworkInfo();     // It checks if there is a connection to the system
        return activeNetworkinfo != null && activeNetworkinfo.isConnected();        // It returns the outcome.
    }

    @Override
    public void onEnterAmbient (Bundle ambientDetails)      // When you enter ambient mode
    {
        super.onEnterAmbient(ambientDetails);       // Set it to the ambient details set.
    }
}
