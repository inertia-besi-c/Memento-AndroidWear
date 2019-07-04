package com.linklab.INERTIA.besi_c;

// Imports

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.estimote.coresdk.observation.region.RegionUtils;
import com.estimote.coresdk.observation.region.beacon.BeaconRegion;
import com.estimote.coresdk.service.BeaconManager;

import java.io.File;
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

/* ------------------------------- Special way to log data for the estimote.. (This was moved from Jamie's File and was just used) PLEASE DO NOT REMOVE ------------------------------- */

public class EstimoteService extends Service
{
    private final Preferences Preference = new Preferences();     // Gets an instance from the preferences module.
    private final SystemInformation SystemInformation = new SystemInformation();  // Gets an instance from the system information module
    private BeaconManager beaconManager;        // This is the beacon manager
    private BeaconRegion region;
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private ArrayList<Beacon> eas;
    private Date starttime=Calendar.getInstance().getTime();
    private StringBuilder strBuilder;       // This is the string builder to build the string.
    private final long Duration = Preference.ESSampleDuration;        // This is the sampling rate in milliseconds gotten from preferences.
    private final String Sensors = Preference.Sensors;     // Gets the sensors from preferences.
    private final String Estimote = Preference.Estimote;     // Gets the sensors from preferences.
    private final String Subdirectory_DeviceLogs = Preference.Subdirectory_DeviceLogs;        // This is where all the system logs and data are kept.
    private final String Subdirectory_Estimote = Preference.Subdirectory_Estimote;        // This is where the estimote is kept
    private final Timer ESSensorTimer = new Timer();          // Makes a new timer for ESSensorTimer.

    @Override
    public void onCreate()
    {
        Log.i("Estimote", "Starting Estimote Service");     // Logs on Console.

        CheckFiles(); // Checks Files

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        ESSensorTimer.schedule( new TimerTask()     // Initializes a timer.
        {
            public void run()       // Runs the imported file based on the timer specified.
            {
                Log.i("Estimote", "Destroying Estimote Service");     // Logs on Console.

                String data =  ("Estimote Sensor," + "Killed Sensor at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
                DataLogger datalog = new DataLogger(Subdirectory_DeviceLogs, Sensors, data);      // Logs it into a file called System Activity.
                datalog.LogData();      // Saves the data into the directory.

                stopSelf();         // Stops the service.
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
                        strBuilder.append(beacon.getMajor());
                        strBuilder.append(",");
                        strBuilder.append(beacon.getRssi());
                        strBuilder.append(",");
                        strBuilder.append(RegionUtils.computeAccuracy(beacon));
                        strBuilder.append(",");
                        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US);
                        String sametime = sdf2.format(Calendar.getInstance().getTime());
                        strBuilder.append(sametime);
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

    class writethread extends Thread
    {
        final Date st;
        final StringBuilder buf;

        writethread(StringBuilder sb, Date starttime)
        {
            this.st = starttime;
            this.buf = sb;
        }

        @Override
        public void run()
        {
            CheckFiles();

            try
            {
                String str = String.valueOf(buf);

                if (buf.length() == 0)
                    return;

                boolean check = writeToFile(st, str);

                if (check)
                {
                    // Do nothing
                }
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
        Log.i("Estimote", "Destroying Estimote Service");     // Logs on Console.

        String data =  ("Estimote Service," + "Killed Estimote Service at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
        DataLogger datalog = new DataLogger(Subdirectory_DeviceLogs, Sensors, data);      // Logs it into a file called System Activity.
        datalog.LogData();      // Saves the data into the directory.

        ESSensorTimer.cancel();
        super.onDestroy();
        beaconManager.stopRanging(region);
        stopForeground(true);
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void CheckFiles()
    {
        File sensors = new File(Preference.Directory + SystemInformation.Sensors_Path);     // Gets the path to the Sensors from the system.
        if (!sensors.exists())      // If the file exists
        {
            Log.i("Estimote Sensor", "Creating Header");     // Logs on Console.

            DataLogger dataLogger = new DataLogger(Subdirectory_DeviceLogs, Sensors, Preference.Sensor_Data_Headers);        /* Logs the Sensors data in a csv format */
            dataLogger.LogData();       // Saves the data to the directory.
        }

        File estimote = new File(Preference.Directory + SystemInformation.Estimote_Path);     // Gets the path to the Sensors from the system.
        if (!estimote.exists())      // If the file exists
        {
            Log.i("Estimote Sensor", "Creating Header");     // Logs on Console.

            DataLogger dataLogger = new DataLogger(Subdirectory_Estimote, Estimote, Preference.Estimote_Data_Headers);        /* Logs the Sensors data in a csv format */
            dataLogger.LogData();       // Saves the data to the directory.
        }
    }
}