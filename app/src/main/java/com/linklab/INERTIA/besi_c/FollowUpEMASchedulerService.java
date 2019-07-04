package com.linklab.INERTIA.besi_c;

// Imports

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class FollowUpEMASchedulerService extends Service        // This is a service file that allows a new pain EMA to interrupt the followup EMA.
{
    private Timer FollowUpEMATimer;     // This is the timer for the follow up EMA
    private final Preferences Preference = new Preferences();     // Gets an instance from the preferences module.
    private final SystemInformation SystemInformation = new SystemInformation();  // Gets an instance from the system information module
    private final long FollowUpEMADelay = Preference.FollowUpEMATimer;     // Time before followup EMA / EMA2 following submission
    private final String Sensors = Preference.Sensors;     // Gets the sensors from preferences.
    private final String Subdirectory_DeviceLogs = Preference.Subdirectory_DeviceLogs;        // This is where all the system logs and data are kept.

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)        // When the service is called this is started.
    {
        File sensors = new File(Preference.Directory + SystemInformation.Sensors_Path);     // Gets the path to the Sensors from the system.
        if (!sensors.exists())      // If the file exists
        {
            Log.i("End of Day EMA prompts", "Creating Header");     // Logs on Console.

            DataLogger dataLogger = new DataLogger(Subdirectory_DeviceLogs, Sensors, Preference.Sensor_Data_Headers);        /* Logs the Sensors data in a csv format */
            dataLogger.LogData();       // Saves the data to the directory.
        }

        String data =  ("Followup EMA Scheduler Timer," + "(Re)Initiated at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
        DataLogger datalog = new DataLogger(Subdirectory_DeviceLogs, Sensors, data);      // Logs it into a file called System Activity.
        datalog.LogData();      // Saves the data into the directory.

        try     // Tries the following first if it can.
        {
            Log.i("Followup EMA", "Canceling the Previous Timer");     // Logs on Console.

            FollowUpEMATimer.cancel();      // It tries to cancel the old timer.
        }
        catch (Exception ex)   // If it cannot
        {
            Log.i("Followup EMA", "In Catch Block");     // Logs on Console.

            String dataC =  ("Followup EMA Scheduler Timer," + "Failed to Cancel at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
            DataLogger datalogC = new DataLogger(Subdirectory_DeviceLogs, Sensors, dataC);      // Logs it into a file called System Activity.
            datalogC.LogData();      // Saves the data into the directory.
        }

        FollowUpEMATimer = new Timer();     // Creates a timer for the follow up EMA
        FollowUpEMATimer.schedule(new TimerTask()       // If they did, it starts a timer for the follow up EMA.
        {
            @Override
            public void run()       // When the timer is called, this is run.
            {
                Log.i("Followup EMA", "Timer is started");     // Logs on Console.

                String data =  ("Followup EMA Timer Scheduler," + "Started at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
                DataLogger datalog = new DataLogger(Subdirectory_DeviceLogs, Sensors, data);      // Logs it into a file called System Activity.
                datalog.LogData();      // Saves the data into the directory.

                Intent StartEMAActivity = new Intent(getBaseContext(), FollowUpEMA.class);      // Links to the Follow up EMA file
                startActivity(StartEMAActivity);    // Starts the Follow up EMA file.

                stopSelf();     // Automatically stops and kills this service.
            }
        }, FollowUpEMADelay);        // Waits the specified time as specified in the preference section.

        return START_NOT_STICKY;        // Does not restart the service if killed.
    }

    @Override
    public void onDestroy()     // When the system is called to be destroyed
    {
        Log.i("Followup EMA", "Destroying Followup EMA Scheduler Service");     // Logs on Console.

        FollowUpEMATimer.cancel();      // When the service is destroyed, the timer is canceled.
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
