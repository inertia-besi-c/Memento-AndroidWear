package com.linklab.INERTIA.besi_c;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import com.estimote.coresdk.observation.region.RegionUtils;
import com.estimote.coresdk.observation.region.beacon.BeaconRegion;
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

import static com.linklab.INERTIA.besi_c.DataLogger.writeToFile;

/* Special way to log data for the estimote.. (This was moved from Jamie's File and was just used) PLEASE DO NOT REMOVE */
public class EstimoteService extends Service
{
    private BeaconManager beaconManager;
    private BeaconRegion region;
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private ArrayList<Beacon> eas;
    Date starttime=Calendar.getInstance().getTime();
    StringBuilder strBuilder;
    public long Duration = new Preferences().ESSampleDuration;        // This is the sampling rate in milliseconds gotten from preferences.

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
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        final Timer ESSensorTimer = new Timer();          // Makes a new timer for HRSensorTimer.
        ESSensorTimer.schedule( new TimerTask()     // Initializes a timer.
        {
            public void run()       // Runs the imported file based on the timer specified.
            {
                onDestroy();        // Destroys the service
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
            }
        });

        beaconManager.setRangingListener(new BeaconManager.BeaconRangingListener()
        {
            @Override
            public void onBeaconsDiscovered(BeaconRegion region, List<com.estimote.coresdk.recognition.packets.Beacon> list)
            {
                if (!list.isEmpty())
                {
                    int t = 0;
                    for (com.estimote.coresdk.recognition.packets.Beacon beacon : list)
                    {
                        Date dt = Calendar.getInstance().getTime();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US);
                        String time1 = sdf.format(dt);
                        eas.add(new Beacon(t++, list.size(), beacon.getRssi(), RegionUtils.computeAccuracy(beacon), time1));
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
        return START_NOT_STICKY;
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
        String data =  ("Estimote Service killed Estimote Service at " + new SystemInformation().getTimeStamp());       // This is the format it is logged at.
        DataLogger datalog = new DataLogger("Sensor_Activity.csv",data);      // Logs it into a file called System Activity.
        datalog.LogData();      // Saves the data into the directory.

        super.onDestroy();
        beaconManager.stopRanging(region);
        stopForeground(true);
        stopSelf();

        final Intent ESService = new Intent(getBaseContext(), EstimoteService.class);       // Starts a ES service intent from the sensor class.
        startService(ESService);    // Starts the Estimote service
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}