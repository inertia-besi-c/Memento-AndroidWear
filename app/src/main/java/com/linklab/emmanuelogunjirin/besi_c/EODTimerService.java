package com.linklab.emmanuelogunjirin.besi_c;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class EODTimerService extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();
        ScheduleEndOfDayEMA(this);
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

        try
        {
            long delay = calendar.getTimeInMillis() - System.currentTimeMillis();

            Timer timer = new Timer();
            timer.schedule(new TimerTask()
            {
                @Override
                public void run()
                {
                    Intent intent = new Intent(thisContext, EndOfDayPrompt1.class);
                    startActivity(intent);
                }
            }, delay, new Preferences().EoDEMA_Period);
        }
        catch(Exception ex){
            long delay = calendar.getTimeInMillis() - System.currentTimeMillis() + 24*60*60*1000;
            Timer timer = new Timer();
            timer.schedule(new TimerTask()
            {
                @Override
                public void run()
                {
                    Intent intent = new Intent(thisContext, EndOfDayPrompt1.class);
                    startActivity(intent);
                }
            }, delay, new Preferences().EoDEMA_Period);
        }
    }

}

