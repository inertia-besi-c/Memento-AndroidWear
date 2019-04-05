package com.linklab.INERTIA.besi_c;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.util.Timer;
import java.util.TimerTask;

public class FollowUpEMASchedulerService extends Service {
    public FollowUpEMASchedulerService() {
    }

    private Timer FollowUpEMATimer;     // This is the timer for the follow up EMA
    private long FollowUpEMADelay = new Preferences().FollowUpEMADelay; //Time before followup EMA / EMA2 following submission

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        try{
        FollowUpEMATimer.cancel();}
        catch (Exception ex){}

        FollowUpEMATimer = new Timer();     // Creates a timer for the follow up EMA

        FollowUpEMATimer.schedule(new TimerTask()       // If they did, it starts a timer for the follow up EMA.
        {
            @Override
            public void run()       // When the timer is called, this is run.
            {
                Intent StartEMAActivity = new Intent(getBaseContext(), FollowUpEMA.class);      // Links to the Follow up EMA file
                startActivity(StartEMAActivity);    // Starts the Follow up EMA file.
                stopSelf();
            }
        },FollowUpEMADelay);        // Waits the specified time as specified in the preference section.

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy()
    {
        FollowUpEMATimer.cancel();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
