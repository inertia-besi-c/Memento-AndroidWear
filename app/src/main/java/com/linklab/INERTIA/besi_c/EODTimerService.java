package com.linklab.INERTIA.besi_c;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class EODTimerService extends Service
{
    public int onStartCommand(Intent intent, int flags, int startId)    /* Establishes the sensor and the ability to collect data at the start of the data collection */
    {
        File sensors = new File(Preference.Directory + SystemInformation.Sensors_Path);     // Gets the path to the Sensors from the system.
        if (!sensors.exists())      // If the file exists
        {
            Log.i("End of Day EMA prompts", "Creating Header");     // Logs on Console.

            DataLogger dataLogger = new DataLogger(Subdirectory_DeviceLogs, Sensors, Preference.Sensor_Data_Headers);        /* Logs the Sensors data in a csv format */
            dataLogger.LogData();       // Saves the data to the directory.
        }

        Log.i("End of Day EMA", "End of Day EMA Timer is starting");     // Logs on Console.

        ScheduleEndOfDayEMA(this);      // Links the schedule EOD EMA to this.
        return START_STICKY;    // This allows it to restart if the service is killed
    }

    private final Preferences Preference = new Preferences();     // Gets an instance from the preferences module.
    private final SystemInformation SystemInformation = new SystemInformation();  // Gets an instance from the system information module
    private final String Sensors = Preference.Sensors;     // Gets the sensors from preferences.
    private final String Directory = Preference.Directory;     // Gets the directory from the preferences class.
    private final String FileName = SystemInformation.EODEMA_Date_Path;        // Initiates a variable for the filename from preferences
    private final String EODEMA_Date = Preference.EODEMA_Date;        // This is the file name from preferences
    private final String Subdirectory_DeviceLogs = Preference.Subdirectory_DeviceLogs;        // This is where all the system logs and data are kept.
    private final String Subdirectory_DeviceActivities = Preference.Subdirectory_DeviceActivities;        // This is where all the system logs and data are kept.
    private String currentLine;     // Line reader variable
    private String lastLine;        // Last line variable
    Timer EODTimerService;      // The timer for the service.

    public void ScheduleEndOfDayEMA(Context context)       // When the timer is called, the schedule is activated.
    {
        final Context thisContext = context;        // Gets a context for the file name.
        Calendar calendar = Calendar.getInstance();     // Gets the calendar.
        calendar.set(Calendar.HOUR_OF_DAY, Preference.EoDEMA_Time_Hour);     // Gets the hour of the day from the preference.
        calendar.set(Calendar.MINUTE, Preference.EoDEMA_Time_Minute);     // Gets the minute of the day from the preference.
        calendar.set(Calendar.SECOND, Preference.EoDEMA_Time_Second);     // Gets the seconds of the day from the preference.

        try     // Try the scheduled task.
        {
            long delay = calendar.getTimeInMillis() - System.currentTimeMillis();       // Starts a long delay variable.

            EODTimerService = new Timer();      // When called the timer is started.
            EODTimerService.schedule(new TimerTask()        // Starts the Application
            {
                @Override
                public void run()       // Runs when it is called.
                {
                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.US);     // A date variable is initialized
                    Date date = new Date();     // Starts a new date call.
                    File file = new File(Directory, FileName);       // Looks for a filename with the new filename

                    try     // Tries to run the following.
                    {
                        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));      // Reads the buffer in the system

                        while ((currentLine = bufferedReader.readLine()) != null)     // While the line is not blank
                        {
                            lastLine = currentLine;     // Set the last line to the last line with words
                        }
                        bufferedReader.close() ;       // Close the buffer reader.
                    }
                    catch (IOException e)   // Catch statement
                    {
                        e.printStackTrace();        // Ignore this.
                    }

                    if(!file.exists())      // Checks if the file even exist in the system. If not, it makes one and calls the EMA.
                    {
                        Log.i("End of Day EMA", "End of Day EMA Timer is starting First EMA Prompt");     // Logs on Console.

                        String data = ("End of Day Timer Service," + "Started Prompt 1 at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.

                        DataLogger datalog = new DataLogger(Subdirectory_DeviceLogs, Sensors, data);      // Logs it into a file called System Activity.
                        DataLogger DailyActivity = new DataLogger(Subdirectory_DeviceActivities, EODEMA_Date, "Date");      // Logs date data to the file.

                        datalog.LogData();      // Saves the data into the directory.
                        DailyActivity.LogData();      // Logs the data to the BESI_C directory.

                        Intent StartEMAActivity = new Intent(thisContext, EndOfDayPrompt1.class);     // Starts the first EOD EMA prompt.
                        startActivity(StartEMAActivity);      // Starts the StartEMAActivity.
                    }
                    if (lastLine.equals(String.valueOf(dateFormat.format(date))))       // if the last line says that an EOD EMA has been completed that day
                    {
                        // Do nothing
                    }
                    else        // If an EOD EMA has not been completed
                    {
                        Log.i("End of Day EMA", "End of Day EMA Timer is starting First EMA Prompt");     // Logs on Console.

                        String data = ("End of Day Timer Service," + "Started Prompt 1 at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.

                        DataLogger datalog = new DataLogger(Subdirectory_DeviceLogs, Sensors, data);      // Logs it into a file called System Activity.
                        DataLogger DailyActivity = new DataLogger(Subdirectory_DeviceActivities, EODEMA_Date, "Date");      // Logs date data to the file.

                        datalog.LogData();      // Saves the data into the directory.
                        DailyActivity.LogData();      // Logs the data to the BESI_C directory.

                        Intent StartEMAActivity = new Intent(thisContext, EndOfDayPrompt1.class);     // Starts the first EOD EMA prompt.
                        startActivity(StartEMAActivity);      // Starts the StartEMAActivity.
                    }
                }
            }, delay, Preference.EoDEMA_Period);     // Gets the preferences setting from the preference system.
        }
        catch(Exception ex)     // If it fails manually start the service.
        {
            long delay = calendar.getTimeInMillis() - System.currentTimeMillis() + 24*60*60*1000;       // If it failed, start it manually.

            Timer EODTimerService = new Timer();      // When called the timer is started.
            EODTimerService.schedule(new TimerTask()        // Starts the App
            {
                @Override
                public void run()       // Runs when it is called.
                {
                    Log.i("End of Day EMA", "End of Day EMA Timer is starting First EMA Prompt");     // Logs on Console.

                    String data =  ("End of Day Timer Service," + "Started Prompt 1 at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
                    DataLogger datalog = new DataLogger(Subdirectory_DeviceLogs, Sensors, data);      // Logs it into a file called System Activity.
                    datalog.LogData();      // Saves the data into the directory.

                    Intent intent = new Intent(thisContext, EndOfDayPrompt1.class);     // Starts the first EOD EMA prompt.
                    startActivity(intent);      // Starts the intent.
                }
            }, delay, Preference.EoDEMA_Period);     // Gets the preferences setting from the preference system.
        }
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
