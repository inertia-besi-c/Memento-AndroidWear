package com.linklab.INERTIA.besi_c;

// Imports
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

class SystemInformation     // Class that acquires the current time from the system and saves it.
{
    String getTime()        // Puts the system time acquired into the desired format wanted.
    {
        DateFormat datetimeFormat = new SimpleDateFormat("MM/dd/yyy HH:mm:ss.SSS", Locale.US);      // Specified format of the time, in US style.
        Date current = new Date();      // Calls the current date from the system.
        return datetimeFormat.format(current);  // Returns the date and time the system is in.
    }

    public static boolean isCharging(Context context)       // This is a boolean that returns if the system is charging.
    {
        try     // Tries to perform this action
        {
            final boolean ChargeState;      // Saves a boolean state for the charge state.
            Intent batteryStatus = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));        // Checks if the battery is currently charging.
            assert batteryStatus != null;       // Asserts that the battery level is not null.
            int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);        //  Gets extra data from the battery level service.
            ChargeState = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL || status == BatteryManager.BATTERY_PLUGGED_AC; // Checks if the battery is currently charging.
            return ChargeState;     // Returns either true or false based on charging state.
        }
        catch(Exception ex)     // If there is an error
        {
            return false;       // Returns false.
        }
    }

    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver()    /* Gets the current battery status, date, and time and sets the text field data */
    {
        @Override
        public void onReceive(final Context context, Intent intent)     // Receives the broadcast.
        {
            // This is just a receiver.
        }
    };
}
