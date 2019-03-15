package com.linklab.emmanuelogunjirin.besi_c;

// Imports
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.PowerManager;

public class AccelerometerSensor extends Service implements SensorEventListener     // This initializes the accelerometer sensor.
{
    private SensorManager mSensorManager;       // Creates the sensor manager that looks into the sensor
    private PowerManager.WakeLock wakeLock;     // Creates the ability for the screen to turn on partially.

    @Override

    /* Establishes the sensor and the ability to collect data at the start of the data collection */
    public int onStartCommand(Intent intent, int flags, int startId)        // Starts when initialized.
    {
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);    // Controls the power of the system.
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "HRService:wakeLock");
        wakeLock.acquire();
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        // Sensor object reference
        Sensor mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        return START_STICKY;
    }

    @Override
    public void onDestroy()     // A destroy service switch (kill switch)
    {
        mSensorManager.unregisterListener(this);
        wakeLock.release();
    }

    @Override
    public void onAccuracyChanged(android.hardware.Sensor sensor, int accuracy)
    {
        // Please do not remove this, the code needs this to function properly. Thank you :-)
    }

    @Override
    public void onSensorChanged(SensorEvent event)      // This is where the data collected by the sensor is saved into a csv file which can be accessed.
    {
        double[] gravity = new double[3];
        double[] lin_accel = new double[3];
        // This removes gravity from the accelerometer data using a high pass filter.
        final double alpha = 0.8;

        gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
        gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
        gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

        lin_accel[0] = event.values[0] - gravity[0];
        lin_accel[1] = event.values[1] - gravity[1];
        lin_accel[2] = event.values[2] - gravity[2];


        final String logstring = new Utils().getTime() + "," +
                String.valueOf(event.timestamp) +
                "," +
                String.valueOf(lin_accel[0]) + // Accel on x-axis
                "," +
                String.valueOf(lin_accel[1]) + // Accel on y-axis
                "," +
                String.valueOf(lin_accel[2]); // Accel on z-axis

        new Thread(new Runnable()
        {
            public void run()
            {
                DataLogger dataLogger = new DataLogger("Accelerometer_Data.csv", logstring);       // Logs the data into a file that can be retrieved.
                dataLogger.LogData();   // Logs the data to the computer.
            }
        }).start();
    }

    @Override
    /* Unknown but necessary function */
    public IBinder onBind(Intent intent)
    {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
