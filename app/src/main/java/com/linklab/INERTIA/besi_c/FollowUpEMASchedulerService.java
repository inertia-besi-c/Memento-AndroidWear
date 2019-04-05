package com.linklab.INERTIA.besi_c;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.util.Timer;
import java.util.TimerTask;

public class FollowUpEMASchedulerService extends Service        // This is a service file that allows a new pain EMA to interrupt the followup EMA.
{
    private Timer FollowUpEMATimer;     // This is the timer for the follow up EMA
    private long FollowUpEMADelay = new Preferences().FollowUpEMADelay;     // Time before followup EMA / EMA2 following submission

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)        // When the service is called this is started.
    {
        try     // Tries the following first if it can.
        {
            FollowUpEMATimer.cancel();      // It tries to cancel the old timer.
        }
        catch (Exception ex)   // If it cannot
        {

        }

        FollowUpEMATimer = new Timer();     // Creates a timer for the follow up EMA
        FollowUpEMATimer.schedule(new TimerTask()       // If they did, it starts a timer for the follow up EMA.
        {
            @Override
            public void run()       // When the timer is called, this is run.
            {
                Intent StartEMAActivity = new Intent(getBaseContext(), FollowUpEMA.class);      // Links to the Follow up EMA file
                startActivity(StartEMAActivity);    // Starts the Follow up EMA file.
                stopSelf();     // Automatically stops and kills this service.
            }
        }, FollowUpEMADelay);        // Waits the specified time as specified in the preference section.

        return START_NOT_STICKY;        // Does not restart the service if killed.
    }

    @Override
    public void onDestroy()
    {
        FollowUpEMATimer.cancel();      // When the service is destroyed, the timer is canceled.
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
