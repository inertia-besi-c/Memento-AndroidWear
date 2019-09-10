package com.linklab.INERTIA.besi_c;

// Imports

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class FollowUpEMA extends WearableActivity       // This is the followup activity for the EMA questions
{
    private Button res, res2, back, next;     // These are the buttons shown on the screen to navigate the watch
    private TextView req;   // This is a text view for the question
    private final ArrayList<String> responses = new ArrayList<>();    // This is a string that is appended to.
    private String[] UserResponses;     // This is the user response.
    private String[] Questions;     // This is the variable question that is assigned a position from the preference menu
    private String[][] Answers;     // Based on the assigned questions the variable answer is modified.
    private String followupEMAStartTime;      // This is the start time of the followup EMA
    private String followupEMAStopTime;      // This is the stop time of the followup EMA
    private final Preferences Preference = new Preferences();     // Gets an instance from the preferences module.
    private final SystemInformation SystemInformation = new SystemInformation();  // Gets an instance from the system information module
    private final String System = Preference.System;      // Gets the System File label from Preferences
    private String Sensors = Preference.Sensors;     // Gets the sensors from preferences.
    private final String Followup_Activity = Preference.Followup_Activity;      // Gets the Followup Activity File label from Preferences
    private final String Followup_Results = Preference.Followup_Results;      // Gets the Followup Results File label from Preferences
    private final String Subdirectory_DeviceLogs = Preference.Subdirectory_DeviceLogs;        // This is where all the system logs and data are kept.
    private final String Subdirectory_EMAActivities = Preference.Subdirectory_EMAActivities;      // This is where the EMA activity data are kept
    private final String Subdirectory_EMAResults = Preference.Subdirectory_EMAResults;        // This is where the EMA responses data are kept
    private Timer EMARemindertimer;     // This is a timer that is called after the person stops in the middle of  the survey.
    private int[] UserResponseIndex;        // This is the user response index that keeps track of the response of the user.
    private int resTaps = 0;        // This is the number of taps that dictates what answer option is visible.
    private final int EMAReminderDelay = Preference.FollowupEMAReminderDelay;  // Calls the Follow up EMA delay from the preferences.
    private final long EMAReminderInterval = Preference.FollowUpEMAReminderInterval; //Time before pinging user after not finishing EMA
    private final int ReminderNumber = Preference.FollowUpEMAReminderNumber;       // Calls the reminder numbers for the follow up from preferences.
    private final int HapticFeedback = Preference.HapticFeedback;      // This is the haptic feedback for button presses.
    private final int ActivityBeginning = Preference.ActivityBeginning;      // This is the haptic feedback for button presses.
    private final int ActivityReminder = Preference.ActivityReminder;      // This is the haptic feedback for button presses.
    private int ReminderCount = 0;      // This is the reminder count that keeps track of the reminders.
    private int CurrentQuestion = 0;        // This is the current question that the person is on.
    private Vibrator v;      // The vibrator that provides haptic feedback.
    private boolean firstRes2 = false;       // This is the boolean that checks if the res2 was clicked for the caregiver.

    private final String[] CaregiverQuestions =       // These are the questions for the care giver in order.
            {
                    "Is patient still in pain now?",
                    "What is the patient's pain level?",
                    "How distressed are you?",
                    "How distressed is the patient?",
                    "Did the patient take another opioid for the pain?",
                    "Why not?",
                    "Ready to submit your answers?",
            };
    private final String[][] CaregiverAnswers =       // These are the answers for the care giver in order.
            {
                    {"Yes", "No", "Unsure"},
                    {"1","2","3","4","5","6","7","8","9","10"},
                    {"Not at all", "A little", "Fairly", "Very"},
                    {"Not at all", "A little", "Fairly", "Very", "Unsure"},
                    {"Yes", "No", "Unsure"},
                    {"Not time yet", "Side effects", "Out of pills", "Worried taking too many", "Pain not bad enough", "Other Reason", "Unsure"},
                    {"Yes", "No"},
            };

    private final String[] PatientQuestions =         // These are the patient questions in order.
            {
                    "Are you still having pain now?",
                    "What is your pain level?",
                    "How distressed are you?",
                    "How distressed is your caregiver?",
                    "Did you take another opioid for the pain?",
                    "Why not?",
                    "Ready to submit your answers?",
            };
    private final String[][] PatientAnswers =        // These are the patient answers in order.
            {
                    {"Yes", "No"},
                    {"1","2","3","4","5","6","7","8","9","10"},
                    {"Not at all", "A little", "Fairly", "Very"},
                    {"Not at all", "A little", "Fairly", "Very", "Unsure"},
                    {"Yes", "No"},
                    {"Not time yet", "Side effects", "Out of pills", "Worried taking too many", "Pain not bad enough", "Other Reason"},
                    {"Yes", "No"},
            };

    @Override
    protected void onCreate(Bundle savedInstanceState)    // When the screen is created, this is run.
    {
        CheckFiles();       // Checks that the file needed dby the system are present
        unlockScreen();     // Unlocks the screen

        Log.i("Followup EMA", "Starting Followup Service");     // Logs on Console.

        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);          /* Vibrator values and their corresponding requirements */
        v.vibrate(ActivityBeginning);        // The watch vibrates for the allotted amount of time.

        super.onCreate(savedInstanceState);     // Creates an instance for the activity.
        setContentView(R.layout.activity_ema);      // Get the layout made for the general EMA in the res files.

        EMARemindertimer = new Timer();     // Creates the EMA reminder timer.
        followupEMAStartTime = String.valueOf(SystemInformation.getTimeMilitary());     // This is the time that the EMA started.

        back = findViewById(R.id.Back);         // Sets the back button to a variable.
        next = findViewById(R.id.Next);         // Sets the next button to a variable.
        req = findViewById(R.id.EMA_req);       // Sets the req button to a variable.
        res = findViewById(R.id.EMA_res);       // Sets the res button to a variable.
        res2 = findViewById(R.id.EMA_res2);       // Sets the res button to a variable.

        if (Preference.Role.equals("PT"))        // This is where the role is set, it checks if the role is PT
        {
            Log.i("Followup EMA", "This is Patient");     // Logs on Console.

            Questions = PatientQuestions;       // If it is, it sets the set of questions to be asked to the patient questions.
            Answers = PatientAnswers;       // And it sets the available answers to be asked to the patient answers.
        }
        else if (Preference.Role.equals("CG"))        // This is where the role is set, it checks if the role is CG
        {
            Log.i("Followup EMA", "This is Care Giver");     // Logs on Console.

            Questions = CaregiverQuestions;     // If it is, it sets the set of questions to be asked to the caregiver questions.
            Answers = CaregiverAnswers;       // And it sets the available answers to be asked to the caregiver answers.
        }

        UserResponses = new String[Questions.length];       // Shows the user response to a new string.
        UserResponseIndex = new int[UserResponses.length];      // Makes the user response to an integer.

        EMARemindertimer.schedule(new TimerTask()       // Assigns the timer a new task when it starts.
        {
            @Override
            public void run()       // Starts this logic when it is run.
            {
                if (ReminderCount <= ReminderNumber)        // If there are still questions to be answered, move to the question.
                {
                    Log.i("Followup EMA", "Reminding User to Continue Survey");     // Logs on Console.

                    v.vibrate(ActivityReminder);     // Vibrate for the assigned time.
                    ReminderCount ++;       // Increment the reminder count by 1.
                }
                else        // If their are no more questions left to ask
                {
                    Log.i("Followup EMA", "Automatically Ending Survey");     // Logs on Console.

                    Submit();       // Submit the response to the questions.
                }
            }
        },EMAReminderDelay,EMAReminderInterval);        // Sets the time and the delay that they should follow.

        QuestionSystem();       // Calls the question system method
        setAmbientEnabled();        // Keeps the screen awake when working.
        setAutoResumeEnabled(true);          // Resumes the main activity.
    }

    @SuppressLint("SetTextI18n")        // Suppresses an error encountered.
    private void QuestionSystem()       // This is the logic behind the question system.
    {
        if (CurrentQuestion == 0 || CurrentQuestion == Questions.length-1 || CurrentQuestion == Questions.length-3)       // If the current question is the first question.
        {
            if (CurrentQuestion == Questions.length-1)      // If this is the last question
            {
                res.setVisibility(View.INVISIBLE);        // Makes the first answer button visible
                res2.setVisibility(View.INVISIBLE);     // Makes the second answer button invisible.
                next.setText(Answers[Questions.length-1][0]);       // Leave the text of the button as the first option in the question
                back.setText(Answers[Questions.length-1][1]);     // Sets the back button to the second option in the questions
            }
            else if (CurrentQuestion == Questions.length-3)      // If this is the 3rd to last question
            {
                res.setVisibility(View.INVISIBLE);        // Makes the first answer button visible
                next.setText(Answers[Questions.length-3][0]);       // Leave the text of the button as the first option in the question
                back.setText(Answers[Questions.length-3][1]);     // Sets the back button to the second option in the questions

                if (Preference.Role.equals("CG"))        // If this is the caregiver watch
                {
                    res2.setVisibility(View.VISIBLE);           // Sets the second button to visible.
                    res2.setBackgroundColor(Color.BLUE);        // Makes the button grey
                    res2.setText(Answers[Questions.length-3][2]);       // Makes the answer on the button the third option in the answer choices
                }
                if (Preference.Role.equals("PT"))        // If this is the patient watch
                {
                    res2.setVisibility(View.INVISIBLE);     // Makes the second answer button invisible.
                }
            }
            else if (CurrentQuestion == 0)      // If this is the 3rd to last question
            {
                res.setVisibility(View.INVISIBLE);        // Makes the first answer button visible
                next.setText(Answers[0][0]);       // Leave the text of the button as the first option in the question
                back.setText(Answers[0][1]);     // Sets the back button to the second option in the questions

                if (Preference.Role.equals("CG"))        // If this is the caregiver watch
                {
                    res2.setVisibility(View.VISIBLE);           // Sets the second button to visible.
                    res2.setBackgroundColor(Color.BLUE);        // Makes the button grey
                    res2.setText(Answers[0][2]);       // Makes the answer on the button the third option in the answer choices
                }
                if (Preference.Role.equals("PT"))        // If this is the patient watch
                {
                    res2.setVisibility(View.INVISIBLE);     // Makes the second answer button invisible.
                }
            }
        }
        else        // If we are in any other question.
        {
            next.setText("Next");       // Leave the text of the button as next.
            back.setText("Back");       // Sets the back button to back
            res.setVisibility(View.VISIBLE);        // Makes the first answer button visible
            res2.setVisibility(View.INVISIBLE);     // Makes the second answer button invisible.
        }

        if (CurrentQuestion < Questions.length)     // If there are still question left to answer.
        {
            resTaps = UserResponseIndex[CurrentQuestion];       // Get the amount of taps from res taps.
            req.setText(Questions[CurrentQuestion]);        // Set the text in the text view to the question number from res taps.
            responses.clear();      // Clear the possible responses possible.
            Collections.addAll(responses, Answers[CurrentQuestion]);        // Keeps the answer that was picked and remembers it.
            Cycle_Responses();      // Calls the cycle response method.

            res.setOnClickListener( new View.OnClickListener()        /* This is the haptic feedback feel that is done when the EMA buttons are pressed. */
            {
                public void onClick(View view)      // When the res button is clicked, this is run.
                {
                    Log.i("Followup EMA", "First Answer Button Tapped");     // Logs on Console.

                    String data =  ("Followup EMA," + "'First Answer Toggle' Button Tapped at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
                    DataLogger datalog = new DataLogger(Subdirectory_DeviceLogs, System, data);      // Logs it into a file called Preferences.
                    datalog.LogData();      // Saves the data into the directory.

                    v.vibrate(HapticFeedback);      // A slight vibration for haptic feedback.
                    resTaps+=1;     // Increments the amount of taps by 1
                    Cycle_Responses();      // Calls the Cycles response method to show the next available answer in the list.
                }
            });

            res2.setOnClickListener( new View.OnClickListener()        /* This is the haptic feedback feel that is done when the EMA buttons are pressed. */
            {
                public void onClick(View view)      // When the res2 button is clicked, this is run.
                {
                    Log.i("Followup EMA", "Second Answer Button Tapped");     // Logs on Console.

                    String data =  ("Followup EMA," + "'Second Answer Toggle' Button Tapped at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
                    DataLogger datalog = new DataLogger(Subdirectory_DeviceLogs, System, data);      // Logs it into a file called Preferences.
                    datalog.LogData();      // Saves the data into the directory.

                    v.vibrate(HapticFeedback);      // A slight vibration for haptic feedback.

                    final Intent HRService = new Intent(getBaseContext(), HeartRateSensor.class);        // Gets an intent for the start of the heartrate sensor.
                    final Intent HRTService = new Intent(getBaseContext(), HRTimerService.class);        // Gets an intent for the start of the heartrate timer sensor.
                    final Intent ESService = new Intent(getBaseContext(), EstimoteService.class);        // Gets an intent for the start of the estimote sensor.
                    final Intent ESTService = new Intent(getBaseContext(), ESTimerService.class);        // Gets an intent for the start of the estimote timer sensor.

                    if (CurrentQuestion == 0 || CurrentQuestion == Questions.length-2 || CurrentQuestion == Questions.length-3)       // If the current question is the first question.
                    {
                        if (Preference.Role.equals("CG"))    // If this is the caregiver watch
                        {
                            UserResponses[CurrentQuestion] = res2.getText().toString();      // The user response question is moved.
                            LogActivity();      // The log activity method is called.

                            if (CurrentQuestion == 0)       // If this is the first question
                            {
                                firstRes2 = true;       // Sets the value of the res2 button to be true

                                UserResponses[CurrentQuestion+1] = null;        // Resets the response index value to null
                                LogActivity();      // The log activity method is called.

                                CurrentQuestion += 2;      // Increments the current question.
                                QuestionSystem();       // The question system method is called again for the next question.

                                stopService(HRTService);     // Stops the Heart Rate Timer Service
                                stopService(ESTService);        // Stops the Estimote Timer Service

                                String dataHRT =  ("Followup EMA," + "Stopped Heart Rate Timer at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
                                String dataEST =  ("Followup EMA," + "Stopped Estimote Timer at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.

                                DataLogger datalogHRT = new DataLogger(Subdirectory_DeviceLogs, Sensors, dataHRT);      // Logs it into a file called System Activity.
                                DataLogger datalogEST = new DataLogger(Subdirectory_DeviceLogs, Sensors, dataEST);      // Logs it into a file called System Activity.

                                datalogEST.LogData();      // Saves the data into the directory.
                                datalogHRT.LogData();      // Saves the data into the directory.

                                if (isRunning(HeartRateSensor.class) || isRunning(EstimoteService.class))       // Starts the heart rate timer controller
                                {
                                    stopService(HRService);        // This stops the heartrate sensor class
                                    stopService(ESService);         // This stops the estimote sensor class

                                    startService(HRService);        // This starts the heartrate sensor class
                                    startService(ESService);        // This starts the estimote sensor class

                                    String dataHR =  ("Followup EMA," + "Stopped and Started Heart Rate Sensor at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
                                    String dataES =  ("Followup EMA," + "Stopped and Started Estimote Sensor at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.

                                    DataLogger datalogHR = new DataLogger(Subdirectory_DeviceLogs, Sensors, dataHR);      // Logs it into a file called System Activity.
                                    DataLogger datalogES = new DataLogger(Subdirectory_DeviceLogs, Sensors, dataES);      // Logs it into a file called System Activity.

                                    datalogHR.LogData();      // Saves the data into the directory.
                                    datalogES.LogData();      // Saves the data into the directory.
                                }
                                else
                                {
                                    startService(HRService);        // This starts the heartrate sensor if it is not already running.
                                    startService(ESService);        // This starts the estimote sensor class if it is not running

                                    String dataHR =  ("Followup EMA," + "Started Heart Rate Sensor at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
                                    String dataES =  ("Followup EMA," + "Started Estimote Sensor at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.

                                    DataLogger datalogHR = new DataLogger(Subdirectory_DeviceLogs, Sensors, dataHR);      // Logs it into a file called System Activity.
                                    DataLogger datalogES = new DataLogger(Subdirectory_DeviceLogs, Sensors, dataES);      // Logs it into a file called System Activity.

                                    datalogHR.LogData();      // Saves the data into the directory.
                                    datalogES.LogData();      // Saves the data into the directory.
                                }
                            }
                            if (CurrentQuestion == Questions.length - 2)        // if this is the last question
                            {
                                CurrentQuestion ++;      // Increments the current question.
                                QuestionSystem();       // The question system method is called again for the next question.
                            }
                            if (CurrentQuestion == Questions.length - 3)        // if this is the last question
                            {
                                UserResponses[CurrentQuestion+1] = null;        // Resets the response index value to null
                                LogActivity();      // The log activity method is called.

                                CurrentQuestion += 2;      // Increments the current question.
                                QuestionSystem();       // The question system method is called again for the next question.
                            }
                        }

                        if (Preference.Role.equals("PT"))
                        {
                            // Do nothing for now.
                        }
                    }

                }
            });

            next.setOnClickListener( new View.OnClickListener()       // Waits for the next button to be clicked.
            {
                public void onClick(View view)      // When the next/submit button is clicked.
                {
                    Log.i("Followup EMA", "Next/Submit Button Tapped");     // Logs on Console.

                    String data =  ("Followup EMA," + "'Next/Submit' Button Tapped at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
                    DataLogger datalog = new DataLogger(Subdirectory_DeviceLogs, System, data);      // Logs it into a file called Preferences.
                    datalog.LogData();      // Saves the data into the directory.

                    v.vibrate(HapticFeedback);      // A slight haptic feedback is provided.

                    if (CurrentQuestion == 0)     // If the answer to is "yes", moves on to question 2
                    {
                        final Intent HRService = new Intent(getBaseContext(), HeartRateSensor.class);        // Gets an intent for the start of the heartrate sensor.
                        final Intent HRTService = new Intent(getBaseContext(), HRTimerService.class);        // Gets an intent for the start of the heartrate timer sensor.
                        final Intent ESService = new Intent(getBaseContext(), EstimoteService.class);        // Gets an intent for the start of the estimote sensor.
                        final Intent ESTService = new Intent(getBaseContext(), ESTimerService.class);        // Gets an intent for the start of the estimote timer sensor.

                        UserResponses[CurrentQuestion] = next.getText().toString();      // The user response question is moved.
                        LogActivity();      // The log activity method is called.

                        CurrentQuestion++;      // Increments the current question.
                        QuestionSystem();       // The question system method is called again for the next question.

                        stopService(HRTService);     // Stops the Heart Rate Timer Service
                        stopService(ESTService);        // Stops the Estimote Timer Service

                        String dataHRT =  ("Followup EMA," + "Stopped Heart Rate Timer at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
                        String dataEST =  ("Followup EMA," + "Stopped Estimote Timer at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.

                        DataLogger datalogHRT = new DataLogger(Subdirectory_DeviceLogs, Sensors, dataHRT);      // Logs it into a file called System Activity.
                        DataLogger datalogEST = new DataLogger(Subdirectory_DeviceLogs, Sensors, dataEST);      // Logs it into a file called System Activity.

                        datalogEST.LogData();      // Saves the data into the directory.
                        datalogHRT.LogData();      // Saves the data into the directory.

                        if (isRunning(HeartRateSensor.class) || isRunning(EstimoteService.class))       // Starts the heart rate timer controller
                        {
                            stopService(HRService);        // This stops the heartrate sensor class
                            stopService(ESService);         // This stops the estimote sensor class

                            startService(HRService);        // This starts the heartrate sensor class
                            startService(ESService);        // This starts the estimote sensor class

                            String dataHR =  ("Followup EMA," + "Stopped and Started Heart Rate Sensor at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
                            String dataES =  ("Followup EMA," + "Stopped and Started Estimote Sensor at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.

                            DataLogger datalogHR = new DataLogger(Subdirectory_DeviceLogs, Sensors, dataHR);      // Logs it into a file called System Activity.
                            DataLogger datalogES = new DataLogger(Subdirectory_DeviceLogs, Sensors, dataES);      // Logs it into a file called System Activity.

                            datalogHR.LogData();      // Saves the data into the directory.
                            datalogES.LogData();      // Saves the data into the directory.
                        }
                        else
                        {
                            startService(HRService);        // This starts the heartrate sensor if it is not already running.
                            startService(ESService);        // This starts the estimote sensor class if it is not running

                            String dataHR =  ("Followup EMA," + "Started Heart Rate Sensor at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
                            String dataES =  ("Followup EMA," + "Started Estimote Sensor at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.

                            DataLogger datalogHR = new DataLogger(Subdirectory_DeviceLogs, Sensors, dataHR);      // Logs it into a file called System Activity.
                            DataLogger datalogES = new DataLogger(Subdirectory_DeviceLogs, Sensors, dataES);      // Logs it into a file called System Activity.

                            datalogHR.LogData();      // Saves the data into the directory.
                            datalogES.LogData();      // Saves the data into the directory.
                        }
                    }
                    else if (CurrentQuestion == Questions.length-1)      // If this is the last question
                    {
                        UserResponses[CurrentQuestion] = next.getText().toString();      // The user response question is moved.
                        LogActivity();      // The log activity method is called.

                        Submit();       // Submit the survey
                    }
                    else if (CurrentQuestion == Questions.length-3)      // If this is the last question
                    {
                        UserResponses[CurrentQuestion] = next.getText().toString();      // The user response question is moved.
                        LogActivity();      // The log activity method is called.

                        UserResponses[CurrentQuestion+1] = null;        // Resets the response index value to null
                        LogActivity();      // The log activity method is called.

                        CurrentQuestion += 2;     // Increment the question amount to go forward to the next question
                        QuestionSystem();       // Call the question method again.
                    }
                    else        // If we are not on the first question
                    {
                        UserResponses[CurrentQuestion] = res.getText().toString();      // The user response question is moved.
                        UserResponseIndex[CurrentQuestion] = Cycle_Responses();     // The question index is incremented
                        LogActivity();      // The log activity method is called.

                        CurrentQuestion ++;     // Increment the question amount to go forward to the next question
                        QuestionSystem();       // Call the question method again.
                    }
                }
            });

            back.setOnClickListener( new View.OnClickListener()    // If the back button is clicked
            {
                public void onClick(View view)      // When the back button is clicked.
                {
                    Log.i("Followup EMA", "Back Button Tapped");     // Logs on Console.

                    String data =  ("Followup EMA," + "'Back' Button Tapped at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
                    DataLogger datalog = new DataLogger(Subdirectory_DeviceLogs, System, data);      // Logs it into a file called Preferences.
                    datalog.LogData();      // Saves the data into the directory.

                    v.vibrate(HapticFeedback);      // A slight haptic feedback is provided.

                    if (CurrentQuestion == 0)       // If we are on the first question
                    {
                        UserResponses[CurrentQuestion] = back.getText().toString();      // The user response question is moved.
                        LogActivity();      // The log activity method is called.

                        Submit();       // Submit the survey
                    }
                    else if (Preference.Role.equals("CG") && CurrentQuestion == 2)
                    {
                        if (firstRes2)
                        {
                            CurrentQuestion = 0;        // Current question is 0
                            firstRes2 = false;      // Resets the initial variable
                        }
                        else
                        {
                            CurrentQuestion --;     // Moves back one question
                        }
                        UserResponses[CurrentQuestion] = back.getText().toString();      // The user response question is moved.
                        LogActivity();      // The log activity method is called.

                        QuestionSystem();       // Calls the question system method
                    }
                    else if (CurrentQuestion == Questions.length-1)     // If this is the last question
                    {
                        UserResponses[CurrentQuestion] = back.getText().toString();      // The user response question is moved.
                        LogActivity();      // The log activity method is called.

                        CurrentQuestion = 0;        // Current question is 0
                        QuestionSystem();       // Calls the question system method
                    }
                    else if (CurrentQuestion == Questions.length-3)     // If this is the last question
                    {
                        UserResponses[CurrentQuestion] = back.getText().toString();      // The user response question is moved.
                        LogActivity();      // The log activity method is called.

                        CurrentQuestion++;        // Current question is 0
                        QuestionSystem();       // Calls the question system method
                    }
                    else        // If we are not on the first question
                    {
                        UserResponses[CurrentQuestion] = res.getText().toString();      // The user response question is moved.
                        UserResponseIndex[CurrentQuestion] = Cycle_Responses();     // The question index is incremented
                        LogActivity();      // Logs the activity.

                        CurrentQuestion --;     // Decrement the question amount to go back to the previous question
                        QuestionSystem();       // Call the question method again.
                    }
                }
            });
        }

        else        // If there are no more questions to ask.
        {
            Submit();       // Submits the survey.
        }
    }

    private void CheckFiles()       // Checks that the files in the system needed are present
    {
        File Result = new File(Preference.Directory + SystemInformation.Followup_EMA_Results_Path);     // Gets the path to the system from the system.
        if (!Result.exists())      // If the file exists
        {
            Log.i("Followup EMA", "Creating Header");     // Logs on Console.

            DataLogger dataLogger = new DataLogger(Subdirectory_EMAResults, Followup_Results, Preference.Followup_EMA_Results_Headers);        /* Logs the system data in a csv format */
            dataLogger.LogData();       // Saves the data to the directory.
        }

        File Activity = new File(Preference.Directory + SystemInformation.Followup_EMA_Activity_Path);     // Gets the path to the system from the system.
        if (!Activity.exists())      // If the file exists
        {
            Log.i("Followup EMA", "Creating Header");     // Logs on Console.

            DataLogger dataLogger = new DataLogger(Subdirectory_EMAActivities, Followup_Activity, Preference.Followup_EMA_Activity_Headers);        /* Logs the system data in a csv format */
            dataLogger.LogData();       // Saves the data to the directory.
        }

        File system = new File(Preference.Directory + SystemInformation.System_Path);     // Gets the path to the system from the system.
        if (!system.exists())      // If the file exists
        {
            Log.i("Followup EMA", "Creating Header");     // Logs on Console.

            DataLogger dataLogger = new DataLogger(Subdirectory_DeviceLogs, System, Preference.System_Data_Headers);        /* Logs the system data in a csv format */
            dataLogger.LogData();       // Saves the data to the directory.
        }
    }

    private void Submit()    /* This is the end of survey part. It submits the data. */
    {
        final Intent HRService = new Intent(getBaseContext(), HeartRateSensor.class);        // Gets an intent for the start of the heartrate sensor.
        final Intent HRTService = new Intent(getBaseContext(), HRTimerService.class);        // Gets an intent for the start of the heartrate timer sensor.
        final Intent ESService = new Intent(getBaseContext(), EstimoteService.class);        // Gets an intent for the start of the estimote sensor.
        final Intent ESTService = new Intent(getBaseContext(), ESTimerService.class);        // Gets an intent for the start of the estimote timer sensor.

        followupEMAStopTime = String.valueOf(SystemInformation.getTimeMilitary());     // This is the time that the EMA stopped.

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.US);     // A date variable is initialized
        Date date = new Date();     // Makes a new date call from the system
        StringBuilder log = new StringBuilder(dateFormat.format(date));     // Starts to log the data

        for (String UserResponse : UserResponses)       // For every response that the user logs in the survey.
        {
            log.append(",").append(UserResponse);       // Any string that has data, get it and append it to the file.
        }
        log.append(",").append(EMADuration());        // This logs the duration of the followup EMA

        DataLogger dataLogger = new DataLogger(Subdirectory_EMAResults, Followup_Results, log.toString());        /* Logs the pain data in a csv format */
        dataLogger.LogData();       // Saves the data to the directory.

        if (!isRunning(HeartRateSensor.class) || !isRunning(EstimoteService.class))       // Starts the heart rate timer controller
        {
            startService(HRService);        // This starts the heartrate sensor class
            startService(ESService);        // This starts the estimote sensor class

            String dataHR =  ("Followup EMA," + "Started Heart Rate Sensor at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
            String dataES =  ("Followup EMA," + "Started Estimote Sensor at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.

            DataLogger datalogHR = new DataLogger(Subdirectory_DeviceLogs, Sensors, dataHR);      // Logs it into a file called System Activity.
            DataLogger datalogES = new DataLogger(Subdirectory_DeviceLogs, Sensors, dataES);      // Logs it into a file called System Activity.

            datalogHR.LogData();      // Saves the data into the directory.
            datalogES.LogData();      // Saves the data into the directory.
        }

        startService(HRTService);        // This starts the heartrate sensor if it is not already running.
        startService(ESTService);        // This starts the estimote sensor class if it is not running

        String dataHRT =  ("Followup EMA," + "Started Heart Rate Timers at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
        String dataEST =  ("Followup EMA," + "Started Estimote Timers at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.

        DataLogger datalogHRT = new DataLogger(Subdirectory_DeviceLogs, Sensors, dataHRT);      // Logs it into a file called System Activity.
        DataLogger datalogEST = new DataLogger(Subdirectory_DeviceLogs, Sensors, dataEST);      // Logs it into a file called System Activity.

        datalogHRT.LogData();      // Saves the data into the directory.
        datalogEST.LogData();      // Saves the data into the directory.

        ThankYou();     // Calls the thank you method.
    }

    private String EMADuration()      // This is the duration of the EMA
    {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss");        // This is the format that the times given for comparison will be in.
        String EMADurationFormat;        // This is the pain EMA duration time.

        try     // The system tires the following.
        {
            Date startTime = timeFormatter.parse(followupEMAStartTime);     // Sets the start time to the start time
            Date stopTime = timeFormatter.parse(followupEMAStopTime);     // Sets the stop time to be the immediate time
            long EMADuration = stopTime.getTime() - startTime.getTime();        // Gets the difference between both times
            String EMADurationHours = String.valueOf(EMADuration / (60 * 60 * 1000) % 24);      // Sets the hour difference to the variable
            String EMADurationMinutes = String.valueOf(EMADuration / (60 * 1000) % 60);     // Sets the minutes difference to the variable
            String EMADurationSeconds = String.valueOf((EMADuration / 1000) % 60);      // Sets the seconds difference to the variable
            EMADurationFormat = EMADurationHours + ":" + EMADurationMinutes + ":" + EMADurationSeconds;       // Sets the duration to the variable
        }
        catch (ParseException e)        // If an error occurs in the process
        {
            EMADurationFormat = "Error, Please Consult the Followup EMA Activities File for the EMA Duration";      // This is the time between the EMAs
        }

        return EMADurationFormat;     // Returns the duration time as a string
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

    private boolean isRunning(Class<?> serviceClass)        // A general file that checks if a system is running.
    {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);     // Starts the activity manager to check the service called.
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))        // For each service called by the running service.
        {
            if (serviceClass.getName().equals(service.service.getClassName()))      // It checks if it is running.
            {
                return true;        // Returns true
            }
        }
        return false;       // If not, it returns false.
    }
    private void LogActivity()      // Logs the activity of the person.
    {
        Log.i("Followup EMA", "Logging Activity");     // Logs on Console.

        String data =  (SystemInformation.getTimeStamp()) + ",EMA_Followup," + CurrentQuestion + "," + UserResponses[CurrentQuestion];        // This is the log that is saved.
        DataLogger datalog = new DataLogger(Subdirectory_EMAActivities, Followup_Activity, data);      // This saves the data into a datalog.
        datalog.LogData();      // Logs the data into the directory specified.
    }

    private void unlockScreen()         // This unlocks the screen if called
    {
        Window window = this.getWindow();       // Gets the window that is being used
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);      // Dismisses the button
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);      // Ignores the screen if locked
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);        // Turns on the screen
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);        // Keeps the Screen on
        window.addFlags(WindowManager.LayoutParams.FLAG_TOUCHABLE_WHEN_WAKING);        // Keeps the Screen on while waking up
    }

    @Override
    public void onDestroy()     // This is called when the activity is destroyed.
    {
        Log.i("Followup EMA", "Destroying Followup EMA");     // Logs on Console.

        EMARemindertimer.cancel();      // The timers are canceled.
        super.onDestroy();      // The activity is killed.
    }

    private int Cycle_Responses()       // This cycles through all the possible responses that the person can provide.
    {
        int index = resTaps%responses.size();       // Index gets the size of all the possible responses.
        res.setText(responses.get(index));      // It sets the text to the index
        return index;       // Returns the number of the index.
    }
}
