package com.linklab.INERTIA.besi_c;

// Imports
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.PowerManager;

public class PedometerSensor extends Service implements SensorEventListener     // Starts a service for the pedometer.
{
    private SensorManager mSensorManager;       // Creates the sensor manager that looks into the sensor
    private PowerManager.WakeLock wakeLock;     // Creates a wakelock power manager for the service.
    private boolean Started = false;        // Creates a boolean to check if it is started.
    @SuppressLint("WakelockTimeout")        // Suppresses some error messages.

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)    /* Establishes the sensor and the ability to collect data at the start of the data collection */
    {
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);     // Regulates the power to the service.
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "HRService:wakeLock");      // Calls a partial wakelock for the service.
        wakeLock.acquire();     // Calls the wakelock.

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);       // Starts a sensor data collection service
        Sensor mPedometer = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);      // Starts it for the sensor pedometer.
        mSensorManager.registerListener(this, mPedometer, SensorManager.SENSOR_DELAY_NORMAL);       // Calls the sensor delay to normal.
        return START_STICKY;    // Makes it so the service restarts if it is stopped.
    }

    @Override
    public void onSensorChanged(SensorEvent event)      // This is where the data collected by the sensor is saved into a csv file which can be accessed.
    {
        if (!Started)       // If the system is not started.
        {
            Started = true;     // Set the boolean value
        }
        else        // If the system has started.
        {
            new DataLogger("StepActivity","yes").WriteData();       // Start logging yes to the file.
        }

        final String logstring = new SystemInformation().getTimeStamp() + "," + String.valueOf(event.timestamp) + "," + String.valueOf(event.values[0]) + "," + String.valueOf(event.accuracy);     // Format the data is in.

        new Thread(new Runnable()       // Starts a new thread for the service.
        {
            public void run()       // This is run in the thread.
            {
                DataLogger dataLogger = new DataLogger("Pedometer_Data.csv", logstring);       // Logs the data into a file that can be retrieved.
                dataLogger.LogData();   // Logs the data to the directory.
            }
        }).start();     // Starts the runnable.
    }

    @Override
    public void onDestroy()     // A destroy service switch (kill switch)
    {
        mSensorManager.unregisterListener(this);        // Kills the listener for the service.
        wakeLock.release();     // Releases the wakelock power.
    }

    @Override
    public void onAccuracyChanged(android.hardware.Sensor sensor, int accuracy)
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
