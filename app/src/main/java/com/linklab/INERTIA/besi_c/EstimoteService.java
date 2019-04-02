package com.linklab.INERTIA.besi_c;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import com.estimote.coresdk.observation.region.RegionUtils;
import com.estimote.coresdk.observation.region.beacon.BeaconRegion;
import com.estimote.coresdk.recognition.packets.Beacon;
import com.estimote.coresdk.service.BeaconManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import static com.linklab.INERTIA.besi_c.Commn.Sharedd.writeToFile;

public class EstimoteService extends Service
{
    private BeaconManager beaconManager;
    private BeaconRegion region;
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private ArrayList<Beac> eas;
    Date starttime=Calendar.getInstance().getTime();
    StringBuilder strBuilder;
    public long Duration = new Preferences().ESSampleDuration;        // This is the sampling rate in milliseconds gotten from preferences.
    final Timer ESSensorTimer = new Timer();          // Makes a new timer for HRSensorTimer.

    @SuppressLint("WakelockTimeout")

    @Override
    public void onCreate()
    {
        super.onCreate();
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakeLockTag:");
        wakeLock.acquire();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId )
    {
        ESSensorTimer.schedule( new TimerTask()     // Initializes a timer.
        {
            public void run()       // Runs the imported file based on the timer specified.
            {
                stopSelf();    // Stops the Heart Rate Sensor
            }
        }, Duration);       // Waits for this amount of duration.

        eas = new ArrayList<>();
        strBuilder = new StringBuilder();
        beaconManager = new BeaconManager(this);

        region = new BeaconRegion("ranged region", UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"), null, null);

        beaconManager.connect(new BeaconManager.ServiceReadyCallback()
        {
            @Override
            public void onServiceReady()
            {
                beaconManager.startRanging(region);
                Log.i("beacon connection ", "");
            }
        });

        beaconManager.setRangingListener(new BeaconManager.BeaconRangingListener()
        {
            @Override
            public void onBeaconsDiscovered(BeaconRegion region, List<Beacon> list)
            {
                if (!list.isEmpty())
                {
                    int t = 0;
                    for (Beacon beacon : list)
                    {
                        Date dt = Calendar.getInstance().getTime();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US);
                        String time1 = sdf.format(dt);
                        eas.add(new Beac(t++, list.size(), beacon.getRssi(), RegionUtils.computeAccuracy(beacon), time1));
                        strBuilder.append(String.valueOf(beacon.getMajor()));
                        strBuilder.append(",");
                        strBuilder.append(String.valueOf(beacon.getRssi()));
                        strBuilder.append(",");
                        strBuilder.append(String.valueOf(RegionUtils.computeAccuracy(beacon)));
                        strBuilder.append(",");
                        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US);
                        String sametime = sdf2.format(Calendar.getInstance().getTime());
                        strBuilder.append(String.valueOf(sametime));
                        strBuilder.append("\n");

                        if (strBuilder != null)
                        {
                            starttime=Calendar.getInstance().getTime();
                            new writethread(strBuilder,starttime).start();
                            strBuilder = new StringBuilder();
                        }
                    }
                }
            }
        });

        super.onStartCommand(intent, flags, startId);
        return Service.START_STICKY;
    }

    public class writethread extends Thread
    {
        Date st;
        StringBuilder buf;

        writethread(StringBuilder sb, Date starttime)
        {
            this.st = starttime;
            this.buf = sb;
        }

        @Override
        public void run()
        {
            try
            {
                String str = String.valueOf(buf);

                if (buf.length() == 0)
                    return;

                boolean check = writeToFile(st, str);

                if (check)
                    Log.i("Saving file", "Hi" + "_" + str.length());
            }

            catch (Exception ex)
            {
                // Do nothing
            }
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        beaconManager.stopRanging(region);
        stopForeground(true);
        stopSelf();
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}