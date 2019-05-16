
package com.linklab.INERTIA.besi_c;

// Imports
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


public class EndOfDayEMA extends WearableActivity       // This is the main service file for the End of Day EMA questions
{
    private PowerManager.WakeLock wakeLock;
    private Button res, res2, back, next;     // These are the buttons shown on the screen to navigate the watch
    private TextView req;   // This is a text view for the question
    private ArrayList<String> responses = new ArrayList<>();    // This is a string that is appended to.
    private String[] UserResponses;     // This sets the person whose watch it is.
    private String [] Questions;        // These are the questions present
    private String[][] Answers;         // These are their possible answers.
    private Preferences Preference = new Preferences();     // Gets an instance from the preferences module.
    private SystemInformation SystemInformation = new SystemInformation();  // Gets an instance from the system information module
    private String System = Preference.System;      // Gets the System File label from Preferences
    private String EODEMA_Date = Preference.EODEMA_Date;     // Gets the EODEMA date File label from Preferences
    private String EndOfDay_Activity = Preference.EndOfDay_Activity;      // Gets the End of Day Activity File label from Preferences
    private String EndOfDay_Results = Preference.EndOfDay_Results;      // Gets the End of Day Results File label from Preferences
    private Timer EMARemindertimer;     // This is the EMA reminder time interval
    private int[] UserResponseIndex;        // This is the user response index.
    private int resTaps = 0;        // This is a tap that increments to show the different options.
    private int EMAReminderDelay = Preference.EoDEMAReminderDelay;      // This is the EMA reminder delay set from preferences.
    private long EMAReminderInterval = Preference.EoDEMAReminderInterval; //    Time before pinging user after not finishing EMA
    private int ReminderNumber = Preference.EoDEMAReminderNumber;       // This is the number of reminders i will get to finish the EMA.
    private int HapticFeedback = Preference.HapticFeedback;      // This is the haptic feedback for button presses.
    private int ActivityBeginning = Preference.ActivityBeginning;      // This is the haptic feedback for button presses.
    private int ActivityReminder = Preference.ActivityReminder;      // This is the haptic feedback for button presses.
    private String Subdirectory_DeviceLogs = Preference.Subdirectory_DeviceLogs;        // This is where all the system logs and data are kept.
    private String Subdirectory_DeviceActivities = Preference.Subdirectory_DeviceActivities;       // This is where the device data that is used to update something in the app is kept
    private String Subdirectory_EMAActivities = Preference.Subdirectory_EMAActivities;      // This is where the EMA activity data are kept
    private String Subdirectory_EMAResults = Preference.Subdirectory_EMAResults;        // This is where the EMA responses data are kept
    private int ReminderCount = 0;      // This is the amount of reminders left to do.
    private int CurrentQuestion = 0;       // This is the current question that the person is on.
    private Vibrator v;      // The vibrator that provides haptic feedback.

    private String[] CaregiverQuestions =       // These are strictly the care giver questions.
            {
                    "How active were you?",
                    "How busy was your home?",
                    "Time spent outside the home?",
                    "Time spent with the patient?",
                    "Time spent with other people?",
                    "How was your sleep?",
                    "How did patient's pain bother you?",
                    "How was your mood?",
                    "How distressed were you overall?",
                    "How distressed was the patient overall?",
            };
    private String[][] CaregiverAnswers =      // These are strictly the care giver answers.
            {
                    {"Not at all", "A little", "Fairly", "Very"},
                    {"Not at all", "A little", "Fairly", "Very"},
                    {"None", "A little", "Medium", "A lot"},
                    {"None", "A little", "Medium", "A lot"},
                    {"None", "A little", "Medium", "A lot"},
                    {"Poor", "Fair", "Good", "Very Good"},
                    {"Not at all", "A little", "Medium", "A lot"},
                    {"Poor", "Fair", "Good", "Very Good"},
                    {"Not at all", "A little", "Fairly", "Very"},
                    {"Not at all", "A little", "Fairly", "Very", "Unsure"},
            };

    private String[] PatientQuestions =      // These are strictly the patient questions.
            {
                    "How active were you?",
                    "How busy was your home?",
                    "Time spent outside the home?",
                    "Time spent with your caregiver?",
                    "Time spent with other people?",
                    "How was your sleep?",
                    "How much did pain bother you?",
                    "How was your mood?",
                    "How distressed were you overall?",
                    "How distressed was your caregiver overall?",
            };
    private String[][] PatientAnswers =      // These are strictly the patient answers.
            {
                    {"Not at all", "A little", "Fairly", "Very"},
                    {"Not at all", "A little", "Fairly", "Very"},
                    {"None", "A little", "Medium", "A lot"},
                    {"None", "A little", "Medium", "A lot"},
                    {"None", "A little", "Medium", "A lot"},
                    {"Poor", "Fair", "Good", "Very Good"},
                    {"Not at all", "A little", "Medium", "A lot"},
                    {"Poor", "Fair", "Good", "Very Good"},
                    {"Not at all", "A little", "Fairly", "Very"},
                    {"Not at all", "A little", "Fairly", "Very", "Unsure"},
            };

    @SuppressLint("WakelockTimeout")
    @Override
    protected void onCreate(Bundle savedInstanceState)    // When the screen is created, this is run.
    {
        File EODResults = new File(Preference.Directory + SystemInformation.EndOfDay_Results_Path);     // Gets the path to the End of day from the system.
        if (EODResults.exists())      // If the file exists
        {
            Log.i("End of Day EMA", "No Header Created");     // Logs to console
        }
        else        // If the file does not exist
        {
            Log.i("End of Day EMA", "Creating Header");     // Logs on Console.

            DataLogger dataLogger = new DataLogger(Subdirectory_EMAResults, EndOfDay_Results, Preference.EndOfDay_EMA_Results_Headers);        /* Logs the End of day data in a csv format */
            dataLogger.LogData();       // Saves the data to the directory.
        }

        File EODActivity = new File(Preference.Directory + SystemInformation.EndOfDay_Activity_Path);     // Gets the path to the End of day from the system.
        if (EODActivity.exists())      // If the file exists
        {
            Log.i("End of Day EMA", "No Header Created");     // Logs to console
        }
        else        // If the file does not exist
        {
            Log.i("End of Day EMA", "Creating Header");     // Logs on Console.

            DataLogger dataLogger = new DataLogger(Subdirectory_EMAActivities, EndOfDay_Activity, Preference.EndOfDay_EMA_Activity_Headers);        /* Logs the End of day data in a csv format */
            dataLogger.LogData();       // Saves the data to the directory.
        }

        File system = new File(Preference.Directory + SystemInformation.System_Path);     // Gets the path to the system from the system.
        if (system.exists())      // If the file exists
        {
            Log.i("End of Day EMA Prompts", "No Header Created");     // Logs to console
        }
        else        // If the file does not exist
        {
            Log.i("End of Day EMA prompts", "Creating Header");     // Logs on Console.

            DataLogger dataLogger = new DataLogger(Subdirectory_DeviceLogs, System, Preference.System_Data_Headers);        /* Logs the system data in a csv format */
            dataLogger.LogData();       // Saves the data to the directory.
        }

        Log.i("End of Day EMA", "Starting End of Day EMA Service");     // Logs on Console.

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);     // Power manager calls the power distribution service.
        wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "End of Day EMA:wakeLock");        // It initiates a full wakelock to turn on the screen.
        wakeLock.acquire();      // The screen turns off after the timeout is passed.

        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);          /* Vibrator values and their corresponding requirements */
        v.vibrate(ActivityBeginning);        // The watch vibrates for the allotted amount of time.

        super.onCreate(savedInstanceState);     // Creates an instance for the activity.
        setContentView(R.layout.activity_ema);      // Get the layout made for the general EMA in the res files.

        back = findViewById(R.id.Back);         // Sets the back button to a variable.
        next = findViewById(R.id.Next);         // Sets the next button to a variable.
        req = findViewById(R.id.EMA_req);       // Sets the req button to a variable.
        res = findViewById(R.id.EMA_res);       // Sets the res button to a variable.
        res2 = findViewById(R.id.EMA_res2);      // Sets the res2 button to a variable.

        if (Preference.Role.equals("PT"))        // This is where the role is set, it checks if the role is PT
        {
            Log.i("End of Day EMA", "This is a Patient");     // Logs on Console.

            Questions = PatientQuestions;       // If it is, it sets the set of questions to be asked to the patient questions.
            Answers = PatientAnswers;       // And it sets the available answers to be asked to the patient answers.
        }
        else if (Preference.Role.equals("CG"))        // This is where the role is set, it checks if the role is CG
        {
            Log.i("End of Day EMA", "This is a Care Giver");     // Logs on Console.

            Questions = CaregiverQuestions;     // If it is, it sets the set of questions to be asked to the caregiver questions.
            Answers = CaregiverAnswers;       // And it sets the available answers to be asked to the caregiver answers.
        }

        UserResponses = new String[Questions.length];       // Shows the user response to a new string.
        UserResponseIndex = new int[UserResponses.length];      // Makes the user response to an integer.

        EMARemindertimer = new Timer();     // Starts a new timer for the reminder EMA.
        EMARemindertimer.schedule(new TimerTask()       // Assigns the timer a new task when it starts.
        {
            @Override
            public void run()       // Starts this logic when it is run.
            {
                if (ReminderCount <= ReminderNumber)        // If there are still questions to be answered, move to the question.
                {
                    Log.i("End of Day EMA", "Reminding User to Continue Survey");     // Logs on Console.

                    String data =  ("End Of Day EMA," + "'Survey Reminder' Timer Initiated at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
                    DataLogger datalog = new DataLogger(Subdirectory_DeviceLogs, System, data);      // Logs it into a file called System Activity.
                    datalog.LogData();      // Saves the data into the directory.

                    v.vibrate(ActivityReminder);     // Vibrate for the assigned time.
                    ReminderCount ++;       // Increment the reminder count by 1.
                }
                else        // If their are no more questions left to ask
                {
                    Log.i("End of Day EMA", "Automatically Ending Survey");     // Logs on Console.

                    String data =  ("End Of Day EMA," + "'Survey Reminder' Automatically Submitted at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
                    DataLogger datalog = new DataLogger(Subdirectory_DeviceLogs, System, data);      // Logs it into a file called System Activity.
                    datalog.LogData();      // Saves the data into the directory.

                    Submit();       // Submit the response to the questions.
                }
            }
        }, EMAReminderDelay, EMAReminderInterval);        // Sets the time and the delay that they should follow.

        res.setOnClickListener( new View.OnClickListener()        /* This is the haptic feedback feel that is done when the EMA buttons are pressed. */
        {
            public void onClick(View view)      // When the res button is clicked, this is run.
            {
                Log.i("End of Day EMA", "Answer Button Tapped");     // Logs on Console.

                String data =  ("End Of Day EMA," + "'Answer Toggle' Button Tapped at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
                DataLogger datalog = new DataLogger(Subdirectory_DeviceLogs, System, data);      // Logs it into a file called System Activity.
                datalog.LogData();      // Saves the data into the directory.

                v.vibrate(HapticFeedback);      // A slight vibration for haptic feedback.
                resTaps += 1;     // Increments the amount of taps by 1
                Cycle_Responses();      // Calls the Cycles response method to show the next available answer in the list.
            }
        });

        QuestionSystem();       // Calls the question system method
        setAmbientEnabled();        // Allows the screen to be on.
        setAutoResumeEnabled(true);     // Resumes the main activity.
    }

    @SuppressLint("SetTextI18n")        // Suppresses an error encountered.
    private void QuestionSystem()       // This is the logic behind the question system.
    {
        res2.setVisibility(View.INVISIBLE);     // Sets the second button to be invisible at all times.

        if (CurrentQuestion == 0)       // If the current question is the first question.
        {
            back.setBackgroundColor(getColor(R.color.grey));        // Make the back button greyed out and unresponsive.
        }
        else        // If we are in any other question.
        {
            back.setBackgroundColor(getColor(R.color.dark_red));        // make the back button dark red and activate it.
        }

        if (CurrentQuestion == Questions.length-1)      // If we are on the last question available
        {
            next.setText("Done");     // Change the text of the next button to be submit.
        }
        else        // if we are on any other question
        {
            next.setText("Next");       // Leave the text of the button as next.
        }

        if (CurrentQuestion < Questions.length)     // If there are still question left to answer.
        {
            resTaps = UserResponseIndex[CurrentQuestion];       // Get the amount of taps from res taps.
            req.setText(Questions[CurrentQuestion]);        // Set the text in the text view to the question number from res taps.
            responses.clear();      // Clear the possible responses possible.
            Collections.addAll(responses, Answers[CurrentQuestion]);        // Keeps the answer that was picked and remembers it.
            Cycle_Responses();      // Calls the cycle response method.

            next.setOnClickListener( new View.OnClickListener()             // Waits for the next button to be clicked.
            {
                public void onClick(View view)      // When the next/submit button is clicked.
                {
                    Log.i("End of Day EMA", "Next/Submit Button Tapped");     // Logs on Console.

                    String data =  ("End of Day EMA," + "'Next/Submit' Button Tapped at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
                    DataLogger datalog = new DataLogger(Subdirectory_DeviceLogs, System, data);      // Logs it into a file called System Activity.
                    datalog.LogData();      // Saves the data into the directory.

                    v.vibrate(HapticFeedback);      // A slight haptic feedback is provided.
                    UserResponses[CurrentQuestion] = res.getText().toString();      // The user response question is moved.
                    UserResponseIndex[CurrentQuestion] = Cycle_Responses();     // The question index is incremented

                    LogActivity();      // The log activity method is called.

                    if (CurrentQuestion == Questions.length-1)      // If there are no more questions left or this is the last question
                    {
                        Submit();       // Submit the survey.
                    }
                    else        // If there are still some more questions.
                    {
                        CurrentQuestion++;      // The number of questions answered is incremented.
                        QuestionSystem();       // The question system method is called again for the next question.
                    }
                }
            });

            back.setOnClickListener( new View.OnClickListener()    // If the back button is clicked
            {
                public void onClick(View view)      // When the back button is clicked.
                {
                    Log.i("End of Day EMA", "Back Button Tapped");     // Logs on Console.

                    String data =  ("End of Day EMA," + "'Back' Button Tapped at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
                    DataLogger datalog = new DataLogger(Subdirectory_DeviceLogs, System, data);      // Logs it into a file called System Activity.
                    datalog.LogData();      // Saves the data into the directory.

                    v.vibrate(HapticFeedback);      // A slight haptic feedback is provided.
                    UserResponses[CurrentQuestion] = res.getText().toString();      // The user response question is moved.
                    UserResponseIndex[CurrentQuestion] = Cycle_Responses();     // The question index is incremented
                    LogActivity();      // Logs the activity.

                    if (CurrentQuestion == 0)       // If we are on the first question
                    {
                        // Do nothing for this first question only.
                    }
                    else        // If we are not on the first question
                    {
                        CurrentQuestion --;     // Decrement the question amount to go back to the previous question
                        QuestionSystem();       // Call the question method again.
                    }
                }
            });
        }

        else        // If there are no more questions to be asked.
        {
            Submit();       // Submit the survey.
        }
    }

    private void Submit()    /* This is the end of survey part. It submits the data. */
    {
        Log.i("End of Day EMA", "Submitting Results");     // Logs on Console.

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.US);     // A date variable is initialized
        DateFormat dateFormatII = new SimpleDateFormat("yyyy/MM/dd", Locale.US);     // A date variable is initialized
        Date date = new Date();     // Starts a new date call.
        StringBuilder log = new StringBuilder(dateFormat.format(date));     // Starts to log the data

        for (String UserResponse : UserResponses)       // For every response that the user enters
        {
            log.append(",").append(UserResponse);       // The data is logged and appended to a string.
        }

        DataLogger dataLogger = new DataLogger(Subdirectory_EMAResults, EndOfDay_Results, log.toString());     // Logs the data in a csv format.
        dataLogger.LogData();       // Logs the data into the BESI_C directory.

        DataLogger DailyActivity = new DataLogger(Subdirectory_DeviceActivities, EODEMA_Date, String.valueOf(dateFormatII.format(date)));      // Logs date data to the file.
        DailyActivity.WriteData();      // Logs the data to the BESI_C directory.

        ThankYou();     // Calls the thank you method.
    }

    private void ThankYou()     // This is a little thank you toast.
    {
        Context context = getApplicationContext();      // Gets a context from the system.
        CharSequence showntext = "Thank You!";       // Pop up information to the person
        int duration = Toast.LENGTH_SHORT;      // Shows the toast only for a short amount of time.
        Toast toast = Toast.makeText(context, showntext, duration);          // A short message at the end to say thank you.
        View view = toast.getView();        // Gets the view from the toast maker
        TextView text = view.findViewById(android.R.id.message);        // Finds the text being used
        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);        // Sets the toast to show up at the center of the screen
        text.setTextColor(Color.WHITE);     // Changes the color of the text
        toast.show();       // Shows the toast.
        finish();       // Finishes the toast.
    }

    private void LogActivity()      // Logs the activity of the person.
    {
        Log.i("End of Day EMA", "Logging Activity");     // Logs on Console.

        String data =  (SystemInformation.getTimeStamp()) + ",EMA_EndOfDay," + CurrentQuestion + "," + UserResponses[CurrentQuestion];        // This is the log that is saved.
        DataLogger datalog = new DataLogger(Subdirectory_EMAActivities, EndOfDay_Activity, data);      // This saves the data into a datalog.
        datalog.LogData();      // Logs the data into the directory specified.
    }

    private int Cycle_Responses()       // This cycles through all the possible responses that the person can provide.
    {
        int index = resTaps%responses.size();       // Index gets the size of all the possible responses.
        res.setText(responses.get(index));      // It sets the text to the index
        return index;       // Returns the number of the index.
    }

    @Override
    public void onDestroy()     // This is called when the activity is destroyed.
    {
        Log.i("End of Day EMA", "Destroying End Of Day EMA");     // Logs on Console.

        wakeLock.release();     // The wakelock system is released.
        EMARemindertimer.cancel();      // The timers are canceled.
        super.onDestroy();      // The activity is killed.
    }
}
