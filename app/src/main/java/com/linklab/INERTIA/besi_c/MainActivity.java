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
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends WearableActivity  // This is the activity that runs on the main screen. This is the main User interface and dominates the start of the app.
{
    private TextView batteryLevel, date, time;    // This is the variables that shows the battery level, date, and time
    private Button SLEEP, SLEEP2;       // This is the sleep button on the screen, along with the other button for aesthetics.
    private boolean SleepMode = false;      // This is the boolean that runs the sleep cycle.
    private boolean BatteryCharge = false;      // This is the boolean that runs the battery charge cycle.
    boolean isCharging;     // Boolean value that keeps track of if the watch is charging or not.
    @SuppressLint("WakelockTimeout")        // Suppresses errors.

    @Override
    protected void onCreate(Bundle savedInstanceState)      // This is created on startup
    {
        super.onCreate(savedInstanceState);      // Creates the main screen.
        setContentView(R.layout.activity_main);     // This is where the texts and buttons seen were made. (Look into: res/layout/activity_main)
        time_updater.start();       // The time updater

        Button EMA_Start = findViewById(R.id.EMA_Start);        // This is the Start button
        SLEEP = findViewById(R.id.SLEEP);        // The Sleep button is made
        SLEEP2 = findViewById(R.id.SLEEP2);     // A fake button that compensates for the extra spaces in some watches.
        batteryLevel = findViewById(R.id.BATTERY_LEVEL);    // Battery level view ID
        date = findViewById(R.id.DATE);     // The date view ID
        time = findViewById(R.id.TIME);     // The time view ID

        final Intent HRService = new Intent(getBaseContext(), HRTimerService.class);        // Gets an intent for the start of the heartrate sensor.
        if (!isRunning(HRTimerService.class))       // Starts the heart rate timer controller
        {
            String data =  ("Main Activity Started Heart Rate Timer at " + new SystemInformation().getTimeStamp());       // This is the format it is logged at.
            DataLogger datalog = new DataLogger("Sensor_Activity.csv",data);      // Logs it into a file called System Activity.
            datalog.LogData();      // Saves the data into the directory.

            startService(HRService);        // That starts the heartrate sensor if it is not already running.
        }

        final Intent AccelService = new Intent(getBaseContext(), AccelerometerSensor.class);        // Creates an intent for calling the accelerometer service.
        if(!isRunning(AccelerometerSensor.class))       // If the accelerometer service is not running
        {
            String data =  ("Main Activity Started Accelerometer Sensor at " + new SystemInformation().getTimeStamp());       // This is the format it is logged at.
            DataLogger datalog = new DataLogger("Sensor_Activity.csv",data);      // Logs it into a file called System Activity.
            datalog.LogData();      // Saves the data into the directory.

            startService(AccelService);        // Starts the service.
        }

        final Intent PedomService = new Intent(getBaseContext(), PedometerSensor.class);        // Creates an intent for calling the pedometer service.
        if(!isRunning(PedometerSensor.class))       // If the pedometer service is not running
        {
            String data =  ("Main Activity Started Pedometer Sensor at " + new SystemInformation().getTimeStamp());       // This is the format it is logged at.
            DataLogger datalog = new DataLogger("Sensor_Activity.csv",data);      // Logs it into a file called System Activity.
            datalog.LogData();      // Saves the data into the directory.

            startService(PedomService);        // Starts the service.
        }

        final Intent EstimService = new Intent(getBaseContext(), ESTimerService.class);        // Creates an intent for calling the Estimote Timer service.
        if(!isRunning(ESTimerService.class))       // If the Estimote Timer service is not running
        {
            String data =  ("Main Activity Started Estimote Timer at " + new SystemInformation().getTimeStamp());       // This is the format it is logged at.
            DataLogger datalog = new DataLogger("Sensor_Activity.csv",data);      // Logs it into a file called System Activity.
            datalog.LogData();      // Saves the data into the directory.

            startService(EstimService);        // Starts the service.
        }

        EMA_Start.setOnClickListener(new View.OnClickListener()     /* Listens for the EMA button "START" to be clicked. */
        {
            public void onClick(View v)     // When the button is clicked the is run
            {
                String data =  ("Main Activity 'Start' Button Tapped at " + new SystemInformation().getTimeStamp());       // This is the format it is logged at.
                DataLogger datalog = new DataLogger("System_Activity.csv",data);      // Logs it into a file called System Activity.
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
                String data =  ("Main Activity 'Sleep' Button Tapped at " + new SystemInformation().getTimeStamp());       // This is the format it is logged at.
                DataLogger datalog = new DataLogger("System_Activity.csv",data);      // Logs it into a file called System Activity.
                datalog.LogData();      // Saves the data into the directory.

                if (isCharging)     // Checks if the watch is charging
                {
                    Charging();     // Calls the charging method to inform the person

                    if (isRunning(HRTimerService.class))        // If the heart rate timer service is running
                    {
                        String dataHR =  ("Sleep Button Stopped Heart Rate Sensor while charging at " + new SystemInformation().getTimeStamp());       // This is the format it is logged at.
                        DataLogger datalogHR = new DataLogger("Sensor_Activity.csv",dataHR);      // Logs it into a file called System Activity.
                        datalogHR.LogData();      // Saves the data into the directory.

                        stopService(HRService);     // It stops the service
                        SLEEP.setBackgroundColor(getResources().getColor(R.color.grey));    // It sets the color of the button to grey
                        SLEEP2.setBackgroundColor(getResources().getColor(R.color.grey));    // It sets the color of the button to grey
                        SLEEP.setText("Sleep");      // It sets the text of the button to sleep
                        SleepMode = true;       // And it sets the boolean value to true.

                        if(isRunning(AccelerometerSensor.class))       // If the accelerometer service is running
                        {
                            String dataA =  ("Sleep Button Stopped Accelerometer Sensor while charging at " + new SystemInformation().getTimeStamp());       // This is the format it is logged at.
                            DataLogger datalogA = new DataLogger("Sensor_Activity.csv",dataA);      // Logs it into a file called System Activity.
                            datalogA.LogData();      // Saves the data into the directory.

                            stopService(AccelService);        // Stop the service.
                        }
                    }
                }

                else        // If the watch is not charging
                {
                    if (isRunning(HRTimerService.class))        // If the heart rate timer service is running
                    {
                        String dataHR =  ("Sleep Button Stopped Heart Rate Sensor while NOT charging at " + new SystemInformation().getTimeStamp());       // This is the format it is logged at.
                        DataLogger datalogHR = new DataLogger("Sensor_Activity.csv",dataHR);      // Logs it into a file called System Activity.
                        datalogHR.LogData();      // Saves the data into the directory.

                        stopService(HRService);     // It stops the service
                        SLEEP.setBackgroundColor(getResources().getColor(R.color.grey));    // It sets the color of the button to grey
                        SLEEP2.setBackgroundColor(getResources().getColor(R.color.grey));    // It sets the color of the button to grey
                        SLEEP.setText("Sleep");      // It sets the text of the button to sleep
                        SleepMode = true;       // And it sets the boolean value to true.

                        if(isRunning(AccelerometerSensor.class))       // If the accelerometer service is running
                        {
                            String dataA =  ("Sleep Button Stopped Accelerometer Sensor while NOT charging at " + new SystemInformation().getTimeStamp());       // This is the format it is logged at.
                            DataLogger datalogA = new DataLogger("Sensor_Activity.csv",dataA);      // Logs it into a file called System Activity.
                            datalogA.LogData();      // Saves the data into the directory.

                            stopService(AccelService);        // Stop the service.
                        }
                    }

                    else        // If the heart rate timer is not running
                    {
                        String dataHR =  ("Sleep Button Started Heart Rate Sensor while NOT charging at " + new SystemInformation().getTimeStamp());       // This is the format it is logged at.
                        DataLogger datalogHR = new DataLogger("Sensor_Activity.csv",dataHR);      // Logs it into a file called System Activity.
                        datalogHR.LogData();      // Saves the data into the directory.

                        startService(HRService);        // It starts the heart rate timer service
                        SLEEP.setBackgroundColor(getResources().getColor(R.color.blue));        // It sets the color of the button to blue
                        SLEEP2.setBackgroundColor(getResources().getColor(R.color.blue));        // It sets the color of the button to blue
                        SLEEP.setText("Sleep");     // It sets the text of the button to sleep
                        SleepMode = false;      // It sets the boolean value to false.

                        if(!isRunning(AccelerometerSensor.class))       // If the accelerometer service is not running
                        {
                            String dataA =  ("Sleep Button Started Accelerometer Sensor while NOT charging at " + new SystemInformation().getTimeStamp());       // This is the format it is logged at.
                            DataLogger datalogA = new DataLogger("Sensor_Activity.csv",dataA);      // Logs it into a file called System Activity.
                            datalogA.LogData();      // Saves the data into the directory.

                            startService(AccelService);        // Starts the service.
                        }
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
                            SystemInformation systemInformation = new SystemInformation();      // Gets the methods from the system information class.
                            DataLogger stepActivity = new DataLogger("StepActivity","no");      // Logs step data to the file.

                            time.setText(systemInformation.getTime());       // The current time is set to show on the time text view.
                            date.setText(systemInformation.getDate());       // The current date is set to show on the date text view.
                            isCharging = systemInformation.isSystemCharging(getApplicationContext());     // Checks if the battery is currently charging.

                            batteryLevel.setText("Battery: " + String.valueOf(systemInformation.getBatteryLevel(getApplicationContext())) + "%");       // Sets the text view for the battery to show the battery level.

                            WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);        // Gets the wifi system on the watch.
                            BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();      // Gets the bluetooth system on the watch

                            if (!bluetooth.isEnabled())     // If the bluetooth is not enabled on the watch
                            {
                                String data =  ("Bluetooth enabled at " + new SystemInformation().getTimeStamp());       // This is the format it is logged at.
                                DataLogger datalog = new DataLogger("Sensor_Activity.csv",data);      // Logs it into a file called System Activity.
                                datalog.LogData();      // Saves the data into the directory.

                                bluetooth.enable();     // Enable it.
                            }

                            if (isCharging)     // If the battery is charging
                            {
                                if (!wifi.isWifiEnabled())      // If the wifi is not enabled
                                {
                                    String data =  ("Wifi enabled at " + new SystemInformation().getTimeStamp());       // This is the format it is logged at.
                                    DataLogger datalog = new DataLogger("Sensor_Activity.csv",data);      // Logs it into a file called System Activity.
                                    datalog.LogData();      // Saves the data into the directory.

                                    wifi.setWifiEnabled(true);      // Enable the wifi.
                                }

                                if (!BatteryCharge || !SleepMode)       // If the battery is not charging and it is not in sleep mode
                                {
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
                                if (wifi.isWifiEnabled())       // If the wifi system is enabled.
                                {
                                    String data =  ("Wifi is disabled at " + new SystemInformation().getTimeStamp());       // This is the format it is logged at.
                                    DataLogger datalog = new DataLogger("Sensor_Activity.csv",data);      // Logs it into a file called System Activity.
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

    private void Charging()     // This is a little charging toast notification.
    {
        Context context = getApplicationContext();      // Gets a context from the system.
        CharSequence text = "Disabled while charging";       // Pop up information to the person
        int duration = Toast.LENGTH_SHORT;      // Shows the toast only for a short amount of time.
        Toast toast = Toast.makeText(context, text, duration);          // A short message at the end to say thank you.
        toast.show();       // Shows the toast.
    }

    private void LogActivityCharge()        // Logs the times when the battery is charging.
    {
        String data;        // This is the data to be logged
        String timeStamp = new SystemInformation().getTimeStamp();      // This is the time stamp from the system

        if (isCharging)     // If the system is charging
        {
            data =  (timeStamp + ",Plugged");      // Data is the time that we are charging
        }
        else      // If we are not charging
        {
            data =  (timeStamp + ",Unplugged");     // Data is the time that we are not charging
        }

        DataLogger datalog = new DataLogger("Battery_Activity.csv",data);      // Logs it into a file called Charging time.
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
    public void onEnterAmbient (Bundle ambientDetails)      // When you enter ambient mode
    {
        super.onEnterAmbient(ambientDetails);       // Set it to the ambient details set.
    }

    @Override
    public void onResume()
    {
        final Intent PedomService = new Intent(getBaseContext(), PedometerSensor.class);        // Creates an intent for calling the pedometer service.
        if(!isRunning(PedometerSensor.class))       // If the pedometer service is not running
        {
            String data =  ("Main Activity Resuming Started Pedometer Sensor at " + new SystemInformation().getTimeStamp());       // This is the format it is logged at.
            DataLogger datalog = new DataLogger("Sensor_Activity.csv",data);      // Logs it into a file called System Activity.
            datalog.LogData();      // Saves the data into the directory.

            startService(PedomService);        // Starts the service.
        }

        final Intent EstimService = new Intent(getBaseContext(), ESTimerService.class);        // Creates an intent for calling the Estimote Timer service.
        if(!isRunning(ESTimerService.class))       // If the Estimote Timer service is not running
        {
            String data =  ("Main Activity Resuming Started Estimote Timer at " + new SystemInformation().getTimeStamp());       // This is the format it is logged at.
            DataLogger datalog = new DataLogger("Sensor_Activity.csv",data);      // Logs it into a file called System Activity.
            datalog.LogData();      // Saves the data into the directory.

            startService(EstimService);        // Starts the service.
        }

        super.onResume();       // Restarts the thread left.
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
