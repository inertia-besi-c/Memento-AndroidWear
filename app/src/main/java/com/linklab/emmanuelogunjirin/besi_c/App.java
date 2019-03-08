package com.linklab.emmanuelogunjirin.besi_c;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class App extends Application {

    public App() {
        // this method fires only once per application start.
        // getApplicationContext returns null here

        Log.i("main", "Constructor fired");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        ScheduleEndOfDayEMA(this);
        // this method fires once as well as constructor
        // but also application has context here

        Log.i("main", "onCreate fired");
    }

    private void ScheduleEndOfDayEMA(Context context)
    {
        final Context thisContext = context;

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, new Preferences().EoDEMA_Time_Hour);
        calendar.set(Calendar.MINUTE, new Preferences().EoDEMA_Time_Minute);
        calendar.set(Calendar.SECOND, new Preferences().EoDEMA_Time_Second);

        Log.i("Schedule",String.valueOf(calendar.getTime()));

        try {
            long delay = calendar.getTimeInMillis() - System.currentTimeMillis();

            Timer timer = new Timer();

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Intent intent = new Intent(thisContext, EndOfDayEMA.class);
                    startActivity(intent);
                }
            }, delay, 24 * 60 * 60 * 1000);
        }
        catch(Exception ex){
            long delay = calendar.getTimeInMillis() - System.currentTimeMillis() + 24*60*60*1000;

            Timer timer = new Timer();

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Intent intent = new Intent(thisContext, EndOfDayEMA.class);
                    startActivity(intent);
                }
            }, delay, 24 * 60 * 60 * 1000);
        }
//        Intent intent = new Intent(context, EndOfDayEMA.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
//        ((AlarmManager) getSystemService(ALARM_SERVICE)).setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY, pendingIntent);


    }
}

