package com.linklab.INERTIA.besi_c;

// Imports.
import android.app.Application;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class EODTimerService extends Application        // Starts the EOD EMA Timer Service when called.
{
    @Override
    public void onCreate()      // Creates the instance when it is started.
    {
        super.onCreate();       // Starts the creation.
        ScheduleEndOfDayEMA(this);      // Links the schedule EOD EMA to this.
    }

    private void ScheduleEndOfDayEMA(Context context)       // When the timer is called, the schedule is activated.
    {
        final Context thisContext = context;        // Gets a context for the file name.

        Calendar calendar = Calendar.getInstance();     // Gets the calendar.
        calendar.set(Calendar.HOUR_OF_DAY, new Preferences().EoDEMA_Time_Hour);     // Gets the hour of the day from the preference.
        calendar.set(Calendar.MINUTE, new Preferences().EoDEMA_Time_Minute);     // Gets the minute of the day from the preference.
        calendar.set(Calendar.SECOND, new Preferences().EoDEMA_Time_Second);     // Gets the seconds of the day from the preference.

        try
        {
            long delay = calendar.getTimeInMillis() - System.currentTimeMillis();       // Starts a long delay variable.

            Timer EODTimerService = new Timer();      // When called the timer is started.
            EODTimerService.schedule(new TimerTask()        // Starts the EODTimerService
            {
                @Override
                public void run()       // Runs when it is called.
                {
                    Intent StartEMAActivity = new Intent(thisContext, EndOfDayPrompt1.class);     // Starts the first EOD EMA prompt.
                    startActivity(StartEMAActivity);      // Starts the StartEMAActivity.
                }
            }, delay, new Preferences().EoDEMA_Period);     // Gets the preferences setting from the preference system.
        }
        catch(Exception ex){
            long delay = calendar.getTimeInMillis() - System.currentTimeMillis() + 24*60*60*1000;       // If it failed, start it manually.

            Timer EODTimerService = new Timer();      // When called the timer is started.
            EODTimerService.schedule(new TimerTask()        // Starts the EODTimerService
            {
                @Override
                public void run()       // Runs when it is called.
                {
                    Intent intent = new Intent(thisContext, EndOfDayPrompt1.class);     // Starts the first EOD EMA prompt.
                    startActivity(intent);      // Starts the intent.
                }
            }, delay, new Preferences().EoDEMA_Period);     // Gets the preferences setting from the preference system.
        }
    }
}
