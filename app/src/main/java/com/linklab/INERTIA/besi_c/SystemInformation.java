package com.linklab.INERTIA.besi_c;

// Imports

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

class SystemInformation     // Class that acquires the current time from the system and saves it.
{
    String getTime()        // This gets only the current time from the system
    {
        DateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.US);      // The time format is called in US format.
        Date current = new Date();      // The current date and timer is set.
        return timeFormat.format(current);       // The current time is set to show on the time text view.
    }

    String getDate()        // This gets only the current date from the system
    {
        Date current = new Date();      // The current date and timer is set.
        DateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy", Locale.US);     // The date is called in US format.
        return dateFormat.format(current);       // The current date is set to show on the date text view.
    }

    String getTimeStamp()        // Puts the system time acquired into the desired format wanted.
    {
        DateFormat datetimeFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS", Locale.US);      // Specified format of the time, in US style.
        Date current = new Date();      // Calls the current date from the system.
        return datetimeFormat.format(current);  // Returns the date and time the system is in.
    }

    String getFolderTimeStamp()        // Puts the system time acquired into the desired format wanted.
    {
        DateFormat datetimeFormat = new SimpleDateFormat("yyMMdd_HHmm", Locale.US);      // Specified format of the time, in US style.
        Date current = new Date();      // Calls the current date from the system.
        return datetimeFormat.format(current);  // Returns the date and time the system is in.
    }

    String getBatteryLevel(Context context)     // This returns a string that displays the battery level
    {
        IntentFilter battery = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);     // Starts an intent that calls the battery level service.
        Intent batteryStatus = context.registerReceiver(null, battery);     // This gets the battery status from that service.
        assert batteryStatus != null;       // Asserts that the battery level is not null.
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);      // Initializes an integer value for the battery level
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);      // Scales the battery level to 100 from whatever default value it is.
        int batteryPct = (level*100/scale);     // Sets the battery level as a percentage.
        return String.valueOf(batteryPct);      // This is the battery level string
    }

    boolean isSystemCharging(Context context)       // Returns a boolean that checks if the system is charging or not.
    {
        IntentFilter battery = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);     // Starts an intent that calls the battery level service.
        Intent batteryStatus = context.registerReceiver(null, battery);     // This gets the battery status from that service.
        assert batteryStatus != null;       // Asserts that the battery level is not null.
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);        //  Gets extra data from the battery level service.
        AtomicBoolean isCharging = new AtomicBoolean(status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_PLUGGED_AC);      // If the system is charging.
        return isCharging.get();        // Return true, or false.
    }
}
