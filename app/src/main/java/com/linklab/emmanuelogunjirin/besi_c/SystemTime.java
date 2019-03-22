package com.linklab.emmanuelogunjirin.besi_c;

// Imports
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

class SystemTime     // Class that acquires the current time from the system and saves it.
{
    String getTime()        // Puts the system time acquired into the desired format wanted.
    {
        DateFormat datetimeFormat = new SimpleDateFormat("MM/dd/yyy HH:mm:ss.SSS", Locale.US);      // Specified format of the time, in US style.
        Date current = new Date();      // Calls the current date from the system.
        return datetimeFormat.format(current);  // Returns the date and time the system is in.
    }
}
