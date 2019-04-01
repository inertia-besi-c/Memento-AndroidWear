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

    String getBatteryLevel(Context context)
    {
        IntentFilter battery = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);     // Starts an intent that calls the battery level service.
        Intent batteryStatus = context.registerReceiver(null, battery);     // This gets the battery status from that service.

        assert batteryStatus != null;       // Asserts that the battery level is not null.
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);      // Initializes an integer value for the battery level
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);      // Scales the battery level to 100 from whatever default value it is.
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);        //  Gets extra data from the battery level service.
        int batteryPct = (level*100/scale);     // Sets the battery level as a percentage.
        return String.valueOf(batteryPct);
    }
}
