package com.linklab.INERTIA.besi_c;

// Imports
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import java.util.Timer;
import java.util.TimerTask;

public class FollowUpEMASchedulerService extends Service        // This is a service file that allows a new pain EMA to interrupt the followup EMA.
{
    private Timer FollowUpEMATimer;     // This is the timer for the follow up EMA
    private long FollowUpEMADelay = new Preferences().FollowUpEMADelay;     // Time before followup EMA / EMA2 following submission

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)        // When the service is called this is started.
    {
        String data =  ("Followup EMA Scheduler Timer (Re)Initiated at" + new SystemInformation().getTimeStamp());       // This is the format it is logged at.
        DataLogger datalog = new DataLogger("Sensor_Activity.csv", data);      // Logs it into a file called System Activity.
        datalog.LogData();      // Saves the data into the directory.

        try     // Tries the following first if it can.
        {
            Log.i("Followup EMA", "Canceling the Previous Timer");     // Logs on Console.

            FollowUpEMATimer.cancel();      // It tries to cancel the old timer.
        }
        catch (Exception ex)   // If it cannot
        {
            Log.i("Followup EMA", "In Catch Block");     // Logs on Console.
        }

        FollowUpEMATimer = new Timer();     // Creates a timer for the follow up EMA
        FollowUpEMATimer.schedule(new TimerTask()       // If they did, it starts a timer for the follow up EMA.
        {
            @Override
            public void run()       // When the timer is called, this is run.
            {
                Log.i("Followup EMA", "Timer is started");     // Logs on Console.

                String data =  ("Followup EMA Timer Scheduler Started at" + new SystemInformation().getTimeStamp());       // This is the format it is logged at.
                DataLogger datalog = new DataLogger("Sensor_Activity.csv", data);      // Logs it into a file called System Activity.
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
