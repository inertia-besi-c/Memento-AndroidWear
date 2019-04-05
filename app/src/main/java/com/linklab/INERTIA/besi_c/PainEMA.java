package com.linklab.INERTIA.besi_c;

// Imports
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


public class PainEMA extends WearableActivity       // This is the main activity for the pain survey questions.
{
    private PowerManager.WakeLock wakeLock;     // This is the power manager service for the system.
    private Button res, back, next;     // These are the buttons shown on the screen to navigate the watch
    private TextView req;   // This is a text view for the question
    private Timer EMARemindertimer;     // This is a timer that is called after the person stops in the middle of  the survey.
    private ArrayList<String> responses = new ArrayList<>();    // This is a string that is appended to.
    private String[] UserResponses;     // This is the user response.
    private String[] Questions;     // This is the variable question that is assigned a position from the preference menu
    private String[][] Answers;     // Based on the assigned questions the variable answer is modified.
    private int[] UserResponseIndex;        // This is the user response index that keeps track of the response of the user.
    private int EMAReminderDelay = new Preferences().PainEMAReminderDelay;      // This is the ema reminder delay that is set for this specific EMA.
    private long EMAReminderInterval = new Preferences().PainEMAReminderInterval; //Time before pinging user after not finishing EMA
    private int ReminderNumber = new Preferences().PainEMAReminderNumber;       // This is the amount of reminders that you want to give before submitting automatically.
    private int ReminderCount = 0;      // This is the reminder count that keeps track of the reminders.
    private int CurrentQuestion = 0;        // This is the current question that the person is on.
    private int resTaps = 0;        // This is the number of taps that dictates what answer option is visible.
    public Vibrator v;      // The vibrator that provides haptic feedback.

    private String[] CaregiverQuestions =       // These are the questions for the care giver in order.
            {
                    "Is patient having pain now?",
                    "What is patient's pain level?",
                    "How distressed are you?",
                    "How distressed is the patient?",
                    "Did patient take an opioid for the pain?"
            };
    private String[][] CaregiverAnswers =       // These are the answers for the care giver in order.
            {
                    {"Yes", "No"},
                    {"1","2","3","4","5","6","7","8","9","10"},
                    {"Not at all", "A little", "Moderately", "Very"},
                    {"Not at all", "A little", "Moderately", "Very", "Unsure"},
                    {"Yes", "No", "Unsure"}
            };

    private String[] PatientQuestions =         // These are the patient questions in order.
            {
                    "Are you in pain now?",
                    "What is your pain level?",
                    "How distressed are you?",
                    "How distressed is your caregiver?",
                    "Did you take an opioid for the pain?"
            };
    private String[][] PatientAnswers =         // These are the patient answers in order.
            {
                    {"Yes", "No"},
                    {"1","2","3","4","5","6","7","8","9","10"},
                    {"Not at all", "A little", "Moderately", "Very"},
                    {"Not at all", "A little", "Moderately", "Very", "Unsure"},
                    {"Yes", "No"}
            };

    @Override
    protected void onCreate(Bundle savedInstanceState)    // When the screen is created, this is run.
    {
        Log.i("Pain EMA", "Starting Followup Service");     // Logs on Console.

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);     // Power manager calls the power distribution service.
        wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "Pain EMA:wakeLock");        // It initiates a full wakelock to turn on the screen.
        wakeLock.acquire((1+ReminderNumber)*EMAReminderInterval+5000);      // The screen turns off after the timeout is passed.

        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);          /* Vibrator values and their corresponding requirements */
        v.vibrate(400);        // The watch vibrates for the allotted amount of time.

        super.onCreate(savedInstanceState);     // Creates an instance for the activity.
        setContentView(R.layout.activity_ema);      // Get the layout made for the general EMA in the res files.

        EMARemindertimer = new Timer();     // Creates the EMA reminder timer.

        back = findViewById(R.id.Back);         // Sets the back button to a variable.
        next = findViewById(R.id.Next);         // Sets the next button to a variable.
        req = findViewById(R.id.EMA_req);       // Sets the req button to a variable.
        res = findViewById(R.id.EMA_res);       // Sets the res button to a variable.

        if (new Preferences().Role.equals("PT"))        // This is where the role is set, it checks if the role is PT
        {
            Log.i("Pain EMA", "This is Patient");     // Logs on Console.

            Questions = PatientQuestions;       // If it is, it sets the set of questions to be asked to the patient questions.
            Answers = PatientAnswers;       // And it sets the available answers to be asked to the patient answers.
        }
        else if (new Preferences().Role.equals("CG"))        // This is where the role is set, it checks if the role is CG
        {
            Log.i("Pain EMA", "This is Care Giver");     // Logs on Console.

            Questions = CaregiverQuestions;     // If it is, it sets the set of questions to be asked to the caregiver questions.
            Answers = CaregiverAnswers;       // And it sets the available answers to be asked to the caregiver answers.
        }

        UserResponses = new String[Questions.length];       // Shows the user response to a new string.
        UserResponseIndex = new int[UserResponses.length];      // Makes the user response to an integer.

        res.setOnClickListener( new View.OnClickListener()        /* This is the haptic feedback feel that is done when the EMA buttons are pressed. */
        {
            public void onClick(View view)      // When the res button is clicked, this is run.
            {
                Log.i("Pain EMA", "Answer Button Tapped");     // Logs on Console.

                String data =  ("Pain EMA 'Answer Toggle' Button Tapped at " + new SystemInformation().getTimeStamp());       // This is the format it is logged at.
                DataLogger datalog = new DataLogger("System_Activity.csv",data);      // Logs it into a file called System Activity.
                datalog.LogData();      // Saves the data into the directory.

                v.vibrate(20);      // A slight vibration for haptic feedback.
                resTaps+=1;     // Increments the amount of taps by 1
                Cycle_Responses();      // Calls the Cycles response method to show the next available answer in the list.
            }
        });

        EMARemindertimer.schedule(new TimerTask()       // Assigns the timer a new task when it starts.
        {
            @Override
            public void run()       // Starts this logic when it is run.
            {
                if (ReminderCount <= ReminderNumber)        // If there are still questions to be answered, move to the question.
                {
                    Log.i("Pain EMA", "Reminding User to Continue Survey");     // Logs on Console.

                    v.vibrate(600);     // Vibrate for the assigned time.
                    ReminderCount ++;       // Increment the reminder count by 1.
                }
                else        // If their are no more questions left to ask
                {
                    Log.i("Pain EMA", "Automatically Ending Survey");     // Logs on Console.

                    Submit();       // Submit the response to the questions.
                }
            }
        },EMAReminderDelay,EMAReminderInterval);        // Sets the time and the delay that they should follow.

        QuestionSystem();       // Calls the question system method
        setAmbientEnabled();        // Keeps the screen awake when working.
        setAutoResumeEnabled(true);     // Resumes the main activity.
    }

    @SuppressLint("SetTextI18n")        // Suppresses an error encountered.
    private void QuestionSystem()       // This is the logic behind the question system.
    {
        if (CurrentQuestion == 0 || CurrentQuestion == Questions.length-1)       // If the current question is the first question.
        {
            next.setText("Yes");       // Leave the text of the button as next.
            back.setText("No");     // Sets the back button to No
            res.setVisibility(View.INVISIBLE);      // Makes the answer toggle invisible
        }
        else        // If we are in any other question.
        {
            next.setText("Next");       // Leave the text of the button as next.
            back.setText("Back");       // Sets the back button to back
            res.setVisibility(View.VISIBLE);        // Makes them visible
        }

        if (CurrentQuestion < Questions.length)     // If there are still question left to answer.
        {
            resTaps = UserResponseIndex[CurrentQuestion];       // Get the amount of taps from res taps.
            req.setText(Questions[CurrentQuestion]);        // Set the text in the text view to the question number from res taps.
            responses.clear();      // Clear the possible responses possible.
            Collections.addAll(responses, Answers[CurrentQuestion]);        // Keeps the answer that was picked and remembers it.
            Cycle_Responses();      // Calls the cycle response method.


            next.setOnClickListener( new View.OnClickListener()       // Waits for the next button to be clicked.
            {
                public void onClick(View view)      // When the next/submit button is clicked.
                {
                    Log.i("Pain EMA", "Next/Submit Button Tapped");     // Logs on Console.

                    String data =  ("Pain EMA 'Next/Submit' Button Tapped at " + new SystemInformation().getTimeStamp());       // This is the format it is logged at.
                    DataLogger datalog = new DataLogger("System_Activity.csv",data);      // Logs it into a file called System Activity.
                    datalog.LogData();      // Saves the data into the directory.

                    v.vibrate(20);      // A slight haptic feedback is provided.
                    UserResponses[CurrentQuestion] = res.getText().toString();      // The user response question is moved.
                    UserResponseIndex[CurrentQuestion] = Cycle_Responses();     // The question index is incremented
                    LogActivity();      // The log activity method is called.

                    if (CurrentQuestion == Questions.length-1)      // If this is the last question
                    {
                        UserResponses[Questions.length -1] = "Yes";     // And they answer yes
                        Submit();       // Submit the survey
                    }
                    else if (UserResponses[0].equals("Yes"))     // If the answer to is "yes", moves on to question 2
                    {
                        CurrentQuestion++;      // Increments the current question.
                        QuestionSystem();       // The question system method is called again for the next question.
                    }
                    else        // If the users first response is not Yes.
                    {
                        Cancel();    // It closes the question screen.
                    }
                }
            });

            back.setOnClickListener( new View.OnClickListener()    // If the back button is clicked
            {
                public void onClick(View view)      // When the back button is clicked.
                {
                    Log.i("Pain EMA", "Back Button Tapped");     // Logs on Console.

                    String data =  ("Pain EMA 'Back' Button Tapped at " + new SystemInformation().getTimeStamp());       // This is the format it is logged at.
                    DataLogger datalog = new DataLogger("System_Activity.csv",data);      // Logs it into a file called System Activity.
                    datalog.LogData();      // Saves the data into the directory.

                    v.vibrate(20);      // A slight haptic feedback is provided.
                    UserResponses[CurrentQuestion] = res.getText().toString();      // The user response question is moved.
                    UserResponseIndex[CurrentQuestion] = Cycle_Responses();     // The question index is incremented
                    LogActivity();      // Logs the activity.

                    if (CurrentQuestion == 0)       // If we are on the first question
                    {
                        ThankYou();
                    }
                    else if (CurrentQuestion == Questions.length-1)     // If this is the last question
                    {
                        UserResponses[Questions.length -1] = "No";      // And they answer no
                        Submit();       // Submit the survey
                    }
                    else        // If we are not on the first question
                    {
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

    private void Submit()    /* This is the end of survey part. It submits the data. */
    {
        Log.i("Pain EMA", "Submitting Results");     // Logs on Console.

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.US);     // A date variable is initialized
        Date date = new Date();     // Makes a new date call from the system
        StringBuilder log = new StringBuilder(dateFormat.format(date));     // Starts to log the data

        for (String UserResponse : UserResponses)       // For every response that the user logs in the survey.
        {
            log.append(",").append(UserResponse);       // Any string that has data, get it and append it to the file.
        }

        DataLogger dataLogger = new DataLogger("Pain_EMA_Results.csv", log.toString());        /* Logs the pain data in a csv format */
        dataLogger.LogData();       // Saves the data to the directory.

        if(UserResponses[Questions.length -1] != null && UserResponses[Questions.length - 1].toLowerCase().contains("yes"))     // Checks if the person answered yes to the first question of the pain EMA.
        {
            // Start FollowUpScheduler
            Intent FollowUpScheduler = new Intent(getApplicationContext(), FollowUpEMASchedulerService.class);
            startService(FollowUpScheduler);
        }

        ThankYou();     // Calls the thank you method.
    }

    private void ThankYou()     // This is a little thank you toast.
    {
        EMARemindertimer.cancel();      // Cancels the EMA reminder timer.
        Context context = getApplicationContext();      // Gets a context from the system.
        CharSequence text = "Thank You!";       // Pop up information to the person
        int duration = Toast.LENGTH_LONG;      // Shows the toast only for a short amount of time.
        Toast toast = Toast.makeText(context, text, duration);          // A short message at the end to say thank you.
        toast.show();       // Shows the toast.
        finish();       // Finishes the toast.
    }

    private int Cycle_Responses()       // This cycles through all the possible responses that the person can provide.
    {
        int index = resTaps%responses.size();       // Index gets the size of all the possible responses.
        res.setText(responses.get(index));      // It sets the text to the index
        return index;       // Returns the number of the index.
    }

    private void LogActivity()      // Logs the activity of the person.
    {
        Log.i("Pain EMA", "Logging Activity");     // Logs on Console.

        String data =  (new SystemInformation().getTimeStamp()) + ",EMA_Pain," + String.valueOf(CurrentQuestion) + "," + UserResponses[CurrentQuestion];        // This is the log that is saved.
        DataLogger datalog = new DataLogger("Pain_EMA_Activity.csv",data);      // This saves the data into a datalog.
        datalog.LogData();      // Logs the data into the directory specified.
    }

    @Override
    public void onDestroy()     // This is called when the activity is destroyed.
    {
        Log.i("Pain EMA", "Destroying Followup EMA");     // Logs on Console.

        wakeLock.release();     // The wakelock system is released.
        EMARemindertimer.cancel();      // The timers are canceled.
        super.onDestroy();      // The activity is killed.
    }

    private void Cancel()       // Cancels the current process.
    {
        ThankYou();     // Calls the thank you method.
        finish();   // Closes the entire survey.
    }
}

