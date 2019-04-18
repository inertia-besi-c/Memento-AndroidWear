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
import android.os.PowerManager;
import android.util.Log;

import java.io.File;

public class AccelerometerSensor extends Service implements SensorEventListener     // This initializes the accelerometer sensor.
{
    private SensorManager mSensorManager;       // Creates the sensor manager that looks into the sensor
    private PowerManager.WakeLock wakeLock;     // Creates the ability for the screen to turn on partially.
    private Preferences Preference = new Preferences();     // Gets an instance from the preferences module.
    private String Accelerometer = Preference.Accelerometer;     // This is the file name set from preferences.
    private int MaxDataCount = Preference.DataCount;        // Gets the Data count number from preferences.
    private int currentCount = 0;       // This is the initial data count for the sensor
    StringBuilder stringBuilder;

    @SuppressLint("WakelockTimeout")        // Stops the error message from the wakelock
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)        // Establishes the sensor and the ability to collect data at the start of the data collection
    {
        Log.i("Accelerometer", "Started Accelerometer Sensor Service");     // Logs on Console.

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);    // Controls the power distribution of the system.
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "AccelService:wakeLock");      // Gets partial power to run the sensor.
        wakeLock.acquire();     // Turns on the wakelock and acquires what is needed.

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);       // Initializes the ability to get a sensor from the system.
        Sensor mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);     // Gets the specific sensor called accelerometer.
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);       // It listens to the data acquires from the accelerometer
        stringBuilder = new StringBuilder();
        return START_STICKY;        // Restarts the sensor if it is killed by the system.
    }

    @Override
    public void onSensorChanged(SensorEvent event)      // This is where the data collected by the sensor is saved into a csv file which can be accessed.
    {
        currentCount ++;
//        double[] gravity = new double[3];       // Ability to remove gravity from the sensor.
        double[] linear_accel = new double[3];      // Initializes the accelerometer value from the sensor.
//        final double alpha = 0.8;        // This removes gravity from the accelerometer data using a high pass filter.

//        gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];        // The gravity value on the x-axis
//        gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];        // The gravity value on the y-axis
//        gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];        // The gravity value on the z-axis

//        linear_accel[0] = event.values[0] - gravity[0];     // Accelerometer value without gravity on the x-axis
//        linear_accel[1] = event.values[1] - gravity[1];     // Accelerometer value without gravity on the y-axis
//        linear_accel[2] = event.values[2] - gravity[2];     // Accelerometer value without gravity on the z-axis

        linear_accel[0] = event.values[0];     // Accelerometer value without gravity on the x-axis
        linear_accel[1] = event.values[1];     // Accelerometer value without gravity on the y-axis
        linear_accel[2] = event.values[2];     // Accelerometer value without gravity on the z-axis

        final String accelerometerValues =      // Shows the values in a string.
                new SystemInformation().getTimeStamp() + "," + String.valueOf(event.timestamp) + "," +          // Starts a new string line.
                String.valueOf(linear_accel[0]) + "," +         // Acceleration value on x-axis
                String.valueOf(linear_accel[1]) + "," +         // Acceleration value on y-axis
                String.valueOf(linear_accel[2]);        // Acceleration value on z-axis

        stringBuilder.append(accelerometerValues);
        stringBuilder.append("\n");

        if ((currentCount >= MaxDataCount) && (stringBuilder != null))
        {
            new Thread(new Runnable()       // Runs this when one or more of the values change
            {
                public void run()       // Re-runs every time.
                {
                    File accelerometer = new File(new Preferences().Directory + new SystemInformation().Accelerometer_Path);     // Gets the path to the accelerometer from the system.
                    if (accelerometer.exists())      // If the file exists
                    {
                        Log.i("Accelerometer", "No Header Created");     // Logs to console
                    } else        // If the file does not exist
                    {
                        Log.i("Accelerometer", "Creating Header");     // Logs on Console.

                        DataLogger dataLogger = new DataLogger(Accelerometer, new Preferences().Accelerometer_Data_Headers);        /* Logs the Accelerometer data in a csv format */
                        dataLogger.LogData();       // Saves the data to the directory.
                    }

                    Log.i("Accelerometer", "Saving Accelerometer Sensor Service Values");     // Logs on Console.

                    DataLogger dataLogger = new DataLogger(Accelerometer, stringBuilder.toString());       // Logs the data into a file that can be retrieved from the watch.
                    dataLogger.LogData();   // Logs the data to a folder on the watch.
                    stringBuilder.setLength(0); //Empties the stringBuilder before next set. 
                    currentCount = 0;
                }
            }).start();     // This starts the runnable thread.
        }
    }

    @Override
    public void onDestroy()     // A destroy service switch (kill switch)
    {
        Log.i("Accelerometer", "Destroying Accelerometer Sensor Service");     // Logs on Console.

        mSensorManager.unregisterListener(this);    // Kills the service that listens to the accelerometer sensor.
        wakeLock.release();     // Releases the wakelock on the service.
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
