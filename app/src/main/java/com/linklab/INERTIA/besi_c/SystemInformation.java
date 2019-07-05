package com.linklab.INERTIA.besi_c;

// Imports

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

@SuppressWarnings("ALL")
class SystemInformation     // Class that acquires the current time from the system and saves it.
{
    private Preferences Preference = new Preferences();     // Gets an instance from the preferences module.

    private String DeviceID = Preference.DeviceID;       // Gets the Device ID from preferences
    private String Accelerometer = Preference.Accelerometer;       // Gets the Accelerometer file from preferences
    private String Battery = Preference.Battery;       // Gets the Battery level file from preferences
    private String Estimote = Preference.Estimote;       // Gets the Estimote file from preferences
    private String Pedometer = Preference.Pedometer;            // Gets the Pedometer file from preferences
    private String Pain_Activity = Preference.Pain_Activity;           // Gets the Pain Activity file from preferences
    private String Pain_Results = Preference.Pain_Results;              // Gets the Pain Results file from preferences
    private String Followup_Activity = Preference.Followup_Activity;           // Gets the Followup Activity file from preferences
    private String Followup_Results = Preference.Followup_Results;           // Gets the Followup Results file from preferences
    private String EndOfDay_Activity = Preference.EndOfDay_Activity;           // Gets the End of Day Activity file from preferences
    private String EndOfDay_Results = Preference.EndOfDay_Results;           // Gets the End of Day Results file from preferences
    private String Sensors = Preference.Sensors;           // Gets the Sensors file from preferences
    private String Steps = Preference.Steps;           // Gets the Steps file from preferences
    private String EODEMA_Date = Preference.EODEMA_Date;           // Gets the EODEMA date file from preferences
    private String System = Preference.System;           // Gets the System file from preferences
    private String Heart_Rate = Preference.Heart_Rate;       // Gets the Heart Rate files from preferences

    /* File path for Adding Headers to Individual File Name */
    public String Accelerometer_Path = Preference.Subdirectory_Accelerometer + "/" + DeviceID + "_" + Preference.Accelerometer + "_" + getDateStamp() + ".csv";     // This is the Accelerometer File path
    public String Battery_Path = Preference.Subdirectory_DeviceLogs + "/" + DeviceID + "_" + Battery;        // This is the Battery Information Folder path
    public String Estimote_Path = Preference.Subdirectory_Estimote + "/" + DeviceID + "_" + Estimote;      // This is the Estimote File path
    public String Pedometer_Path = Preference.Subdirectory_DeviceLogs + "/" + DeviceID + "_" + Pedometer;        // This is the Pedometer File path
    public String Pain_EMA_Activity_Path = Preference.Subdirectory_EMAActivities + "/" + DeviceID + "_" + Pain_Activity;     // This is the Pain EMA Activity File path
    public String Pain_EMA_Results_Path = Preference.Subdirectory_EMAResults + "/" + DeviceID + "_" + Pain_Results;       // This is the Pain EMA Response File path
    public String Followup_EMA_Activity_Path = Preference.Subdirectory_EMAActivities + "/" + DeviceID + "_" + Followup_Activity;     // This is the Followup EMA Activity File path
    public String Followup_EMA_Results_Path = Preference.Subdirectory_EMAResults + "/" + DeviceID + "_" + Followup_Results;     // This is the Followup EMA Response File path
    public String EndOfDay_Activity_Path = Preference.Subdirectory_EMAActivities + "/" + DeviceID + "_" + EndOfDay_Activity;     // This is the End OF Day EMA Activity File Path
    public String EndOfDay_Results_Path = Preference.Subdirectory_EMAResults + "/" + DeviceID + "_" + EndOfDay_Results;       // This is the End of Day Response File path
    public String Sensors_Path = Preference.Subdirectory_DeviceLogs + "/" + DeviceID + "_" + Sensors;    // This is the Sensor Activity File path
    public String Steps_Path = Preference.Subdirectory_DeviceActivities + "/" + DeviceID + "_" + Steps;     // This is the Step Activity File path
    public String EODEMA_Date_Path = Preference.Subdirectory_DeviceActivities + "/" + DeviceID + "_" + EODEMA_Date;           // Gets the EODEMA date file from preferences
    public String System_Path = Preference.Subdirectory_DeviceLogs + "/" + DeviceID + "_" + System;      // This is the System Activity File path
    public String Heart_Rate_Path = Preference.Subdirectory_HeartRate + "/" + DeviceID + "_" + Heart_Rate;        // This is the Heart Rate path

    List <String> Subdirectories = new ArrayList<>          /* Subdirectories to be made by the system */
    (Arrays.asList        // Creates a list of the subdirectories to be created.
            (
                    Preference.Subdirectory_Accelerometer,        // This is where the accelerometer data is kept
                    Preference.Subdirectory_HeartRate,        // This is where the Heartrate data is kept
                    Preference.Subdirectory_Estimote,      // This is where the estimote is kept
                    Preference.Subdirectory_EMAActivities,        // This is where the EMA activity data are kept
                    Preference.Subdirectory_EMAResults,          // This is where the EMA responses data are kept
                    Preference.Subdirectory_DeviceActivities,     // This is where the device data that is used to update something in the app is kept
                    Preference.Subdirectory_DeviceLogs        // This is where all the system logs and data are kept.
            )
    );

    String getTime()        // This gets only the current time from the system
    {
        DateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.US);      // The time format is called in US format.
        Date current = new Date();      // The current date and timer is set.
        return timeFormat.format(current);       // The current time is set to show on the time text view.
    }

    String getTimeStamp()        // Puts the system time acquired into the desired format wanted.
    {
        DateFormat datetimeFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS", Locale.US);      // Specified format of the time, in US style.
        Date current = new Date();      // Calls the current date from the system.
        return datetimeFormat.format(current);  // Returns the date and time the system is in.
    }

    String getTimeMilitary()        // Gets the current time in military format
    {
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.US);      // The time format military wise is called in US format.
        Date current = new Date();      // The current date and timer is set.
        return timeFormat.format(current);       // The current time in military format is returned
    }

    String getDate()        // This gets only the current date from the system
    {
        Date current = new Date();      // The current date and timer is set.
        DateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy", Locale.US);     // The date is called in US format.
        return dateFormat.format(current);       // The current date is set to show on the date text view.
    }

    String getDateStamp()       // Gets the data stamp from the system
    {
        Date current = new Date();      // The current date and timer is set.
        DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd", Locale.US);     // The date is called in US format.
        return dateFormat.format(current);       // The current date is set to show on the date text view.
    }

    String getFolderTimeStamp()        // Puts the system time acquired into the desired format wanted.
    {
        DateFormat datetimeFormat = new SimpleDateFormat("yyMMdd_HHmm", Locale.US);         // Sets the date and time for the file.
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

    int getBatteryPercent(Context context)      // This returns the battery level as an integer
    {
        IntentFilter battery = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);     // Starts an intent that calls the battery level service.
        Intent batteryStatus = context.registerReceiver(null, battery);     // This gets the battery status from that service.
        assert batteryStatus != null;       // Asserts that the battery level is not null.
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);      // Initializes an integer value for the battery level
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);      // Scales the battery level to 100 from whatever default value it is.
        return (level*100/scale);       // Returns the current battery level
    }

    boolean isSystemCharging(Context context)       // Returns a boolean that checks if the system is charging or not.
    {
        IntentFilter battery = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);     // Starts an intent that calls the battery level service.
        Intent batteryStatus = context.registerReceiver(null, battery);     // This gets the battery status from that service.
        assert batteryStatus != null;       // Asserts that the battery level is not null.
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);        //  Gets extra data from the battery level service.
        AtomicBoolean isCharging = new AtomicBoolean(status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_PLUGGED_AC
        || status == BatteryManager.BATTERY_STATUS_FULL);      // If the system is charging.
        return isCharging.get();        // Return true, or false.
    }

    boolean isTimeBetweenTimes (String currentTime, int startHour, int endHour, int startMinute, int endMinute, int startSecond, int endSecond)     // Checks if the current time is between two times
    {
        if (true)        // If the system time does match
        {
            String hourString = currentTime.split(":")[0];     // It set the first string to the hour
            String minuteString = currentTime.split(":")[1];       // It sets the second string to the minutes
            String secondString = currentTime.split(":")[2];       // It sets the thrid string to the seconds

            int hour = Integer.parseInt(hourString);        // Makes the string an integer for the hour
            int minute = Integer.parseInt(minuteString);        // Makes the string an integer for the minute
            int second = Integer.parseInt(secondString);        // Makes the string an integer for the seconds

            if ((hour >= startHour) && (hour < endHour))    // If the time of the system is between the given time limits
            {
                return true;        // Return true
            }
            else        // If it fails the first check
            {
                if (hour == endHour)        // Check if the hour is the current hour
                {
                    if ((minute >= startMinute) && (minute < endMinute))      // Check the minute, and if it is less than the end minute time
                    {
                        return true;        // Return true
                    }
                    else        // If it fails the first check
                    {
                        if (minute == endMinute)        // Check if the minute is the current minute
                        {
                            if ((second >= startSecond) && (second < endSecond))      // Check the seconds, and if it is less than the end second time
                            {
                                return true;        // Return true
                            }
                        }
                        return false;       // If not return false
                    }
                }
                return false;       // If not return false
            }
        }
        return false;       // If it does not match the time needed, return false.
    }
}
