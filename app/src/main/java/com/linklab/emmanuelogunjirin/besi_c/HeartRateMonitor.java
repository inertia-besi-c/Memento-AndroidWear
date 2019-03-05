package com.linklab.emmanuelogunjirin.besi_c;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class HeartRateMonitor extends Service
{
    public HeartRateMonitor()
    {

    }

    @Override
    public IBinder onBind(Intent intent)
    {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
