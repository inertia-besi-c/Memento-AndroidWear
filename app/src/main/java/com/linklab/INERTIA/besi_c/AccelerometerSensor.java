package com.linklab.INERTIA.besi_c;

// Imports

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;
import java.text.*;

import java.io.File;

public class AccelerometerSensor extends Service implements SensorEventListener     // This initializes the accelerometer sensor.
{
    private SensorManager mSensorManager;       // Creates the sensor manager that looks into the sensor
    private final Preferences Preference = new Preferences();     // Gets an instance from the preferences module.
    private final SystemInformation SystemInformation = new SystemInformation();  // Gets an instance from the system information module
    private final String Accelerometer = Preference.Accelerometer + "_" + SystemInformation.getDateStamp() + ".csv";     // This is the file name set from preferences.
    private final int MaxDataCount = Preference.AccelDataCount;        // Gets the Data count number from preferences.
    private final String Subdirectory_Accelerometer = Preference.Subdirectory_Accelerometer;       // This is where the accelerometer data is kept
    private int currentCount = 0;       // This is the initial data count for the sensor
    private StringBuilder stringBuilder1, stringBuilder2;

    @SuppressLint("WakelockTimeout")        // Stops the error message from the wakelock
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)        // Establishes the sensor and the ability to collect data at the start of the data collection
    {
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);       // Initializes the ability to get a sensor from the system.
        Sensor mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);     // Gets the specific sensor called accelerometer.
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);       // It listens to the data acquires from the accelerometer
        stringBuilder1 = new StringBuilder();
        stringBuilder2 = new StringBuilder();
        return START_STICKY;        // Restarts the sensor if it is killed by the system.
    }

    @Override
    public void onSensorChanged(SensorEvent event)      // This is where the data collected by the sensor is saved into a csv file which can be accessed.
    {
        Log.i("Accelerometer", "Logging to StringBuilder");     // Logs to console
        DecimalFormat decimalformat = new DecimalFormat("#.####");

        currentCount ++;
        double[] linear_accel = new double[3];      // Initializes the accelerometer value from the sensor.

        linear_accel[0] = event.values[0];     // Accelerometer value with gravity on the x-axis
        linear_accel[1] = event.values[1];     // Accelerometer value with gravity on the y-axis
        linear_accel[2] = event.values[2];     // Accelerometer value with gravity on the z-axis

        String linearx = decimalformat.format(linear_accel[0]);     // Limits the length of the x-axis value to 4 digits
        String lineary = decimalformat.format(linear_accel[1]);     // Limits the length of the y-axis value to 4 digits
        String linearz = decimalformat.format(linear_accel[2]);     // Limits the length of the z-axis value to 4 digits

        final String accelerometerValues =      // Shows the values in a string.
                SystemInformation.getTimeStamp() + "," +          // Starts a new string line.
                linearx + "," +         // Acceleration value on x-axis
                lineary + "," +         // Acceleration value on y-axis
                linearz;        // Acceleration value on z-axis

        stringBuilder1.append(accelerometerValues);      // Appends the values to the string builder
        stringBuilder1.append("\n");     // Makes a new line

        if ((currentCount >= MaxDataCount) && (stringBuilder1 != null))      // If the string builder length is thing and it is not empty
        {
            stringBuilder2.append(stringBuilder1);        // Appends all the values from the the first string builder to the second string builder
            currentCount = 0;       // Reset the count so that stringBuilder1 can now be ready to start gathering of more data without duplication
            stringBuilder1.setLength(0);     // Empties the stringBuilder before next set.

            new Thread(new Runnable()       // Runs this when one or more of the values change
            {
                public void run()       // Re-runs every time.
                {
                    File accelerometer = new File(Preference.Directory + SystemInformation.Accelerometer_Path);     // Gets the path to the accelerometer from the system.
                    if (!accelerometer.exists())      // If the file exists
                    {
                        Log.i("Accelerometer", "Creating Header");     // Logs on Console.

                        DataLogger dataLogger = new DataLogger(Subdirectory_Accelerometer, Accelerometer, Preference.Accelerometer_Data_Headers);        /* Logs the Accelerometer data in a csv format */
                        dataLogger.LogData();       // Saves the data to the directory.
                    }

                    Log.i("Accelerometer", "Saving Accelerometer Sensor Service Values");     // Logs on Console.

                    DataLogger dataLogger = new DataLogger(Subdirectory_Accelerometer, Accelerometer, stringBuilder2.toString());       // Logs the data into a file that can be retrieved from the watch.
                    dataLogger.LogData();       // Logs the data to a folder on the watch.
                    stringBuilder2.setLength(0);     // Empties the stringBuilder before next set.
                }
            }).start();     // This starts the runnable thread.
        }
    }

    @Override
    public void onDestroy()     // A destroy service switch (kill switch)
    {
        Log.i("Accelerometer", "Destroying Accelerometer Sensor Service");     // Logs on Console.

        mSensorManager.unregisterListener(this);    // Kills the service that listens to the accelerometer sensor.
    }

    @Override
    public void onAccuracyChanged(android.hardware.Sensor sensor, int accuracy)     // Ability to increase the accuracy of the sensor.
    {
        // Please do not remove this, the code needs this to function properly. Thank you :-)
    }

    @Override
    /* Unknown but necessary function */
    public IBinder onBind(Intent intent)
    {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
