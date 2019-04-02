package com.linklab.INERTIA.besi_c;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import com.estimote.coresdk.observation.region.RegionUtils;
import com.estimote.coresdk.observation.region.beacon.BeaconRegion;
import com.estimote.coresdk.recognition.packets.Beacon;
import com.estimote.coresdk.service.BeaconManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.linklab.INERTIA.besi_c.Commn.Sharedd.writeToFile;




public class proximityService extends Service  {
    // private BeacAdapter adapter;
    private BeaconManager beaconManager;
    // Observable<List> getDataFrombeacons;
    // private ObservableEmitter<List> dataObserver;
    private BeaconRegion region;
    private ArrayList<Beac> eas;
    Date starttime=Calendar.getInstance().getTime();
    StringBuilder strBuilder;
    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;
    private NotificationManager manager;
    private int NOTIFICATION_ID = 121;
    public static final String ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE";
    public static final String ACTION_START_SERVICE = "ACTION_START_SERVICE";
    private static final String NOTIFICATION_CHANNEL_ID ="notification_channel_id";
    private static final String NOTIFICATION_Service_CHANNEL_ID = "service_channel";



    @Override
    public int onStartCommand(Intent intent, int flags, int startId ) {
        //Intent intent1 = intent.getgetI();
        //Toast.makeText(this,"onStartCommand",Toast.LENGTH_SHORT).show();




        String action11 = intent.getAction();
        if(action11 == ACTION_STOP_SERVICE){
            String data =  ("Proximity Service Stopped Estimote" + new SystemInformation().getTimeStamp());       // This is the format it is logged at.
            DataLogger datalog = new DataLogger("System_Activity.csv",data);      // Logs it into a file called System Activity.
            datalog.LogData();      // Saves the data into the directory.

            Log.d("er","called to cancel service");
            beaconManager.stopRanging(region);
            stopForeground(true);
            stopSelf();
            onDestroy();
        }
        else {
            String data =  ("Proximity Service Started Estimote" + new SystemInformation().getTimeStamp());       // This is the format it is logged at.
            DataLogger datalog = new DataLogger("System_Activity.csv",data);      // Logs it into a file called System Activity.
            datalog.LogData();      // Saves the data into the directory.
            // NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            //manager.cancel(NOTIFICATION_ID);
            // NOTIFICATION_ID=0;
            // Log.i("notification","cancelll" );
            //  stopForeground(false);
            // stopSelf();

            /*
                int icon = R.mipmap.ic_launcher;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                    icon = R.mipmap.ic_launcher_round;
                }
              //  Log.i("notification","has" );
                Intent notificationIntent = new Intent(this, proximityService.class);
                notificationIntent.setAction(this.ACTION_STOP_SERVICE);
                PendingIntent pStopSelf = PendingIntent.getService(this, 0, notificationIntent,flags=0);
                NotificationCompat.Action action =new NotificationCompat.Action(R.drawable.action_item_icon_background, "Stop",pStopSelf);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                        .setSmallIcon(icon)
                        //     .setContentIntent(pendingIntent)
                        .setContentTitle("Service")
                        .setContentText("Running...")
                        .addAction(action);
                Notification notification=builder.build();
                //builder.addAction(R.drawable.action_item_icon_background, "Stop",pendingIntent);
                if(Build.VERSION.SDK_INT>=26) {
                    NotificationChannel channel = new NotificationChannel(NOTIFICATION_Service_CHANNEL_ID, "Sync Service", NotificationManager.IMPORTANCE_HIGH);
                    channel.setDescription("Service Name");
                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.createNotificationChannel(channel);
                    NotificationCompat.Action action1 =new NotificationCompat.Action(R.drawable.action_item_icon_background, "Stop",pStopSelf);
                    notification = new NotificationCompat.Builder(this,NOTIFICATION_Service_CHANNEL_ID)
                            .setContentTitle("Service")
                            .setContentText("Running...")
                            .setSmallIcon(icon)
                            .addAction(action1)
                            .build();
                }
                // builder.addAction(R.drawable.action_item_icon_background, "Stop",pStopSelf);
                startForeground(NOTIFICATION_ID, notification);
*/
            // Log.i("write File thread", "has");

            //Toast.makeText(this,"Test",Toast.LENGTH_SHORT).show();

            eas = new ArrayList<>();
            strBuilder = new StringBuilder();
            // Works on Huawei
            beaconManager = new BeaconManager(this);

            region = new BeaconRegion("ranged region",
                    UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"), null, null);

//            UUID ESTIMOTE_PROXIMITY_UUID = UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D");
            //final BeaconRegion ALL_ESTIMOTE_BEACONS = new BeaconRegion("rid", ESTIMOTE_PROXIMITY_UUID, null, null);


            // Enables Always-on
            //setAmbientEnabled();
            beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
                @Override
                public void onServiceReady() {
                    beaconManager.startRanging(region);
                    //beaconManager.startRanging(ALL_ESTIMOTE_BEACONS);
                    Log.i("beacon connection ", "");
                }
            });
            //
            beaconManager.setRangingListener(new BeaconManager.BeaconRangingListener() {
                //beaconManager.setNearableListener(new BeaconManager.NearableListener() {
                @Override
                //public void onBeaconsDiscovered(BeaconRegion region, List<Beacon> list) {
                public void onBeaconsDiscovered(BeaconRegion region, List<Beacon> list) {
                    Log.i("cooo File thread", "Beacons empty:" + list.isEmpty());
                    //  Toast.makeText(this," beacon detected",Toast.LENGTH_SHORT).show();
                    if (!list.isEmpty()) {
                        int t = 0;
                        //mListView.setAdapter(adapter);
                        //  Log.i(" ile thad", "has");
                        for (Beacon beacon : list) {
                            Date dt = Calendar.getInstance().getTime();
                            //SimpleDateFormat sd = new SimpleDateFormat("hh:mm:ss.mmm ");
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                            String time1 = sdf.format(dt);
                            //  String time2=sd.format(dt);
                            eas.add(new Beac(t++, list.size(), beacon.getRssi(), RegionUtils.computeAccuracy(beacon), time1));
                            Log.i(" beaconfound", "" + beacon.getMajor());
                            //adapter.notifyDataSetChanged();
                            //  easfun(eas);
                                /*
                                if (differtim(starttime, Calendar.getInstance().getTimeStamp())) { //ENABLE THIS!!!
                                    writeData();
                                    // Toast.makeText(MainActivity.this,"Eor saving file",Toast.LENGTH_SHORT).show();
                                    Log.i(" writing", "has");
                                } //ENABLE THIS!!!
                                */
                            //Toast.makeText(MainActivity.this," saving file",Toast.LENGTH_SHORT).show();
                            strBuilder.append(String.valueOf(beacon.getMajor()));
                            strBuilder.append(",");
                            strBuilder.append(String.valueOf(beacon.getRssi()));
                            strBuilder.append(",");
                            strBuilder.append(String.valueOf(RegionUtils.computeAccuracy(beacon)));
                            strBuilder.append(",");
                            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                            String sametime = sdf2.format(Calendar.getInstance().getTime());
                            strBuilder.append(String.valueOf(sametime));
                            strBuilder.append("\n");
                            if (strBuilder != null) {

                                // Toast.makeText(this," writeData",Toast.LENGTH_SHORT).show();
                                starttime=Calendar.getInstance().getTime();
                                new writethread(strBuilder,starttime).start();
                                strBuilder = new StringBuilder();
                            }

                        }
                        Log.i("cooooo File thread", "has");


                    }
                    Log.i("outside of if condition", "---");

                }
            });

        }

        //  eas = intent;
        //Toast.makeText(this,"End of onStart",Toast.LENGTH_SHORT).show();
        super.onStartCommand(intent, flags, startId);
        return Service.START_STICKY;
        //return Service.START_REDELIVER_INTENT;





    }




    @Override
    public void onCreate() {
        super.onCreate();


        // setAmbientEnabled();

        powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        //wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"Estimote:MyWakeLock");
        //wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"Estimote:MyWakeLock");
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"MyWakeLockTag:");

        wakeLock.acquire();


        //  startForeground(1,new Notification());

    }

















    public class writethread extends Thread {
        Date st;
        StringBuilder buf;

        public writethread(StringBuilder sb, Date starttime) {
            this.st = starttime;
            this.buf = sb;
            // Log.i("Thread Called", "for saving sensor samples" + "_" + buf.length());
        }

        @Override
        public void run() {
            try {

                //Toast.makeText(this,"Error saving file",Toast.LENGTH_SHORT).show();

                String str = String.valueOf(buf);

                if (buf.length() == 0)
                    return;
                //  Log.i("Thread Called", "for saving sensor samples" + "_" + str.length());
                //boolean check = writeToFile(st, str);
                boolean check = writeToFile(st, str);
                String data =  ("Logging Estimote" + new SystemInformation().getTimeStamp());       // This is the format it is logged at.
                DataLogger datalog = new DataLogger("System_Activity.csv",data);      // Logs it into a file called System Activity.
                datalog.LogData();      // Saves the data into the directory.

                if (check)
                    Log.i("Saving file", "Hi" + "_" + str.length());
                // return check ;

            } catch (Exception ex) {
                Log.i("saving fail", ex.toString());
            }
        }
    }


    //  public void easfun(ArrayList<Beac> haser) {
    //    Intent intent = new Intent();
    //  intent.setAction("com.example.davealex.estimservice");
    //intent.putParcelableArrayListExtra("dews",haser);
    // sendBroadcast(intent);
    // Log.i("cooooo File thread","hasfun");
    //}

    @Override
    public void onDestroy() {
        super.onDestroy();
        //unregisterListener(this);
        stopForeground(true);
        stopSelf();
        //wakeLock.release();
        //  sendData();
    }








    public boolean differtim (Date time1,Date time2)
    {
        long diffInMillisec = time2.getTime()-time1.getTime();
        long diffInSec = TimeUnit.MILLISECONDS.toSeconds(diffInMillisec);
        long minutes = 0;
        long seconds=0;
        seconds = diffInSec % 60;
        diffInSec/= 60;
        minutes =diffInSec % 60;
        /*
        if(minutes>=5) {
            Log.i("-", "comparision");
            return true;
        }
        */
        /*
        if(seconds>=1) {
            Log.i("-", "comparision");
            return true;
        }
        else
            return false;
            */
        return true;
    }


    public void writeData() {
        if (strBuilder != null) {

            Toast.makeText(this," writeData",Toast.LENGTH_SHORT).show();
            starttime=Calendar.getInstance().getTime();
            new writethread(strBuilder,starttime).start();
            strBuilder = new StringBuilder();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

}