package com.linklab.emmanuelogunjirin.besi_c;

// Imports
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class PedometerSensor extends Service implements SensorEventListener
{

    private SensorManager mSensorManager;       // Creates the sensor manager that looks into the sensor
    private Sensor mPedometer;     // Sensor object reference

    @Override
    /* Establishes the sensor and the ability to collect data at the start of the data collection */
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
         mPedometer = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        mSensorManager.registerListener(this, mPedometer, SensorManager.SENSOR_DELAY_NORMAL);
        return START_STICKY;
    }


    public void onResume()  // A resume service switch
    {
        mSensorManager.registerListener(this, mPedometer, SensorManager.SENSOR_DELAY_NORMAL);
    }


    public void onPause()   // A pause service switch
    {
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onDestroy()     // A destroy service switch (kill switch)
    {
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(android.hardware.Sensor sensor, int accuracy)
    {
        // Please do not remove this, the code needs this to function properly. Thank you :-)
    }

    @Override
    public void onSensorChanged(SensorEvent event)      // This is where the data collected by the sensor is saved into a csv file which can be accessed.
    {

        final String logstring = new Utils().getTime() + "," +
                String.valueOf(event.timestamp) +
                "," +
                String.valueOf(event.values[0]) +
                "," +
                String.valueOf(event.accuracy);

        new Thread(new Runnable()
        {
            public void run() {
                DataLogger dataLogger = new DataLogger("PedometerData.csv", logstring);       // Logs the data into a file that can be retrieved.
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
